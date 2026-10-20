
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

class JUpdaterCore extends JUpdater {


    public static final String SDKNAME = BuildConfig.SDK_NAME;
    public static final String VERSION = BuildConfig.VERSION;
    public static final String BUILDNUMBER = BuildConfig.BUILDNUMBER;

    private static final JUpdaterCore INSTANCE = new JUpdaterCore();


    /** At most one worker thread, created on demand and released after 30s of idleness. */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            0, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            r -> {
                Thread t = new Thread(r, "JUpdater-check");
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            });

    private final AtomicBoolean checkInFlight = new AtomicBoolean(false);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private volatile Context appContext;
    private volatile JUpdaterConfig config;
    private volatile UpdateStore store;

    public static JUpdater getInstance() {
        return INSTANCE;
    }

    private JUpdaterCore() {
    }


    @Override
    public void init(@NonNull Context context, JUpdaterConfig updaterConfig) {
        try {
            JLogger.setLevel(updaterConfig.getLogLevel());
            Context app = context.getApplicationContext();
            if (!(app instanceof Application)) {
                JLogger.e("init() needs a Context with an Application. SDK not started.");
                return;
            }

            // Only application-scoped objects are retained (no Activity leaks).
            this.appContext = app;
            this.config = updaterConfig;
            if (store == null) {
                store = new UpdateStore(app);
            }

            JLogger.i("Init Successfully");
            JLogger.i(String.format("Starting %s v%s (build %s), mode: %s", SDKNAME, VERSION, BUILDNUMBER,
                    updaterConfig.isUsingManagedServer() ? "managed" : "custom"));

            JLifecycleListener.register((Application) app, new JLifecycleListener.Callback() {
                @Override
                public void onAppForeground() {
                    JLogger.d("App in foreground");
                    scheduleCheck();
                }

                @Override
                public void onAppBackground() {
                    JLogger.d("App in background");
                }
            });

            // init() called from an already visible Activity: don't wait for the next foreground.
            // Duplicate triggers are absorbed by the in-flight guard and the check interval.
            if (context instanceof Activity) {
                scheduleCheck();
            }

        } catch (Exception e) {
            JLogger.e("[Error] While init " + e.getLocalizedMessage());
        }


    }

    @Override
    public void launchUrlInBrowser(@NonNull Context context, @NonNull String urlToOpen) {
        UrlLauncher.open(context.getApplicationContext(), urlToOpen);
    }

    private void scheduleCheck() {
        if (config == null || appContext == null) return;
        if (!checkInFlight.compareAndSet(false, true)) {
            JLogger.d("Check already running, skipping");
            return;
        }
        try {
            EXECUTOR.execute(() -> {
                try {
                    runCheck();
                } catch (Throwable t) {
                    JLogger.e("Update check failed", t);
                } finally {
                    checkInFlight.set(false);
                }
            });
        } catch (RuntimeException e) {
            checkInFlight.set(false);
            JLogger.e("Could not schedule update check", e);
        }
    }
    /** Runs on the worker thread. */
    private void runCheck() {
        final Context ctx = appContext;
        final JUpdaterConfig cfg = config;
        final UpdateStore st = store;
        if (ctx == null || cfg == null || st == null) return;

        final AppInfo app = AppInfo.read(ctx);
        if (app.versionCode < 0) {
            JLogger.w("Unknown app version code, skipping update check");
            return;
        }

        final long now = System.currentTimeMillis();
        final UpdateStore.ServerState cached = st.load();
        final long last = st.lastCheckTime();

        // Throttle: answer from the last server response instead of hitting the network again.
        if (cached != null && now >= last && now - last < cfg.getMinCheckIntervalMs()) {
            JLogger.d("Using cached server state (check interval not elapsed)");
            evaluate(cfg, app, cached);
            return;
        }

        try {
            JSONObject payload = cfg.isUsingManagedServer()
                    ? DeviceData.collect(app, st.installationId(), cfg.getApiKey())
                    : null;
            JLogger.v(() -> "Payload: " + maskKey(payload, cfg));

            UpdateChecker.Response r = UpdateChecker.check(cfg, app, payload);

            if (!app.packageName.equals(r.pid)) {
                JLogger.w("Server pid does not match this app's package name, ignoring response");
                return;
            }

            UpdateStore.ServerState fresh =
                    new UpdateStore.ServerState(r.versionCode, r.updateUrl, r.forceGap, r.blocked, r.title,r.message,r.isForceUpdate);
            st.save(fresh, now);
            evaluate(cfg, app, fresh);

        } catch (UpdateChecker.HttpException e) {
            JLogger.e("Server responded with " + e.getMessage());
            // 4xx (bad key, unknown app, ...) must not resurrect stale state; 5xx is treated as offline.
            if (e.code >= 500 && cached != null) {
                evaluate(cfg, app, cached);
            }
        } catch (java.io.IOException e) {
            JLogger.w("Network unavailable (" + e.getClass().getSimpleName() + "), using last known state");
            if (cached != null) {
                evaluate(cfg, app, cached);
            }
        } catch (Exception e) {
            JLogger.e("Update check error", e);
        }
    }
    private void evaluate(@NonNull JUpdaterConfig cfg, @NonNull AppInfo app, @NonNull UpdateStore.ServerState s) {
        if (s.blocked) {
            JLogger.w("App is disabled on the update server, no update UI will be shown");
            return;
        }
        JLogger.d(() -> "Server version " + s.versionCode + ", installed " + app.versionCode);
        if (s.versionCode <= app.versionCode) {
            JLogger.d("Already on the latest version");
            return;
        }

        String url = s.updateUrl != null ? s.updateUrl : cfg.getFallbackApkUrl();
        if (!UrlLauncher.isHttpUrl(url)) {
            JLogger.w("No valid http(s) update URL available, not showing update UI");
            return;
        }

        int threshold = s.forceGap >= 1 ? s.forceGap : cfg.getForceUpdateThreshold();
        boolean force =  s.isForceUpdate|| (s.versionCode - app.versionCode > threshold);
        JLogger.i("Update available -> " + (force ? "FORCE" : "optional"));
        showUpdateUi(url, force,s.title,s.message);
    }

    private void showUpdateUi(@NonNull String url, boolean force,String title,String message) {
        mainHandler.post(() -> {
            try {
                if (force) {
                    launchBlockingScreen(url,title,message);
                } else {
                    JUpdateSheetController.show(url,title,message);
                }
            } catch (Exception e) {
                JLogger.e("Could not show update UI", e);
            }
        });
    }

    private void launchBlockingScreen(@NonNull String url,String title,String message) {
        Context ctx = appContext;
        if (ctx == null || UpdateBlocking.isShowing()) return;
        Intent intent = new Intent(ctx, UpdateBlocking.class);
        intent.putExtra(Constants.EXTRA_UPDATE_URL, url);
        intent.putExtra(Constants.EXTRA_TITLE, title);
        intent.putExtra(Constants.EXTRA_MESSAGE, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    @Nullable
    private static String maskKey(@Nullable JSONObject payload, @NonNull JUpdaterConfig cfg) {
        if (payload == null) return "(none, custom mode)";
        String s = payload.toString();
        String key = cfg.getApiKey();
        return key != null ? s.replace(key, "***") : s;
    }
}
