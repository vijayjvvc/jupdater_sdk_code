
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import static com.qtlws.android.jupdater.Constants.INSTALLATION_ID;
import static com.qtlws.android.jupdater.Constants.var34sp;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

class JUpdaterCore extends JUpdater {


    private static JUpdaterCore var1xi;

    public static final String SDKNAME = BuildConfig.SDK_NAME;
    public static final String VERSION = BuildConfig.VERSION;
    public static final String BUILDNUMBER = BuildConfig.BUILDNUMBER;

    private static JThread var5tdx;

    private static Context var2xct;
    private static Context var2;
    private static JUpdaterConfig config;
    private boolean var111x = true;


    static {
        var1xi = new JUpdaterCore();
    }


    @Override
    public void init(@NonNull Context context, JUpdaterConfig updaterConfig) {
        try {

            if (updaterConfig.getUpdatedApkUrl() == null || updaterConfig.getUpdatedApkUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("Update APK URL is missing or empty. SDK initialization failed.");
            }
            var2 = context;
            var2xct = context.getApplicationContext();
            config = updaterConfig;
            JLogger.var36dgx = config.isDebug();
            JLogger.infoLog("Init Successfully");
            JLogger.infoLog(String.format("Starting JUpdater Service: (v%s.%s)", VERSION, BUILDNUMBER));
            JLogger.infoLog(("Build Number: " + BUILDNUMBER));

            if (JLifecycleListener.listener == null) {
                JLifecycleListener.$$b(var2xct, new JLifecycleListener.ForegroundListener() {
                    @Override
                    public void onAppForeground(Activity var1) {
                        JLogger.infoLog("onBecameForeground");
                        startService();
                    }

                    @Override
                    public void onAppBackground(Context var1) {
                        JLogger.infoLog("onBecameBackground");
                    }
                });
            }

        } catch (Exception e) {
            JLogger.errorLog("[Error] While init " + e.getLocalizedMessage());
        }


    }

    @Override
    public void launchUrlInBrowser(@NonNull Context context, @NonNull String urlToOpen) {
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            JLogger.infoLog("Launching :- " + urlToOpen);
            context.startActivity(intent);
        } catch (Exception e) {
            JLogger.errorLog(e);
        }
    }


    private void startService() {
        if (var2 instanceof Application) {
            JLogger.debugLog("Application context is found");
        } else if (var2 instanceof Activity) {
            JLogger.debugLog("Activity context is found");
        } else {
            JLogger.debugLog("Unknown context is found");
        }
        JLogger.debugLog("JUpdater initialized with URL: " + config.getUpdatedApkUrl());
        var5tdx = new JThread(() -> {
            Map<String,Object> data = startCollectionData();
            UpdateChecker.checkForUpdate(var2xct, config,data);
        });
        var5tdx.start();
    }

    private Map<String, Object> startCollectionData() {

        Map<String, Object> var0 = new HashMap<>();
        try {
            var0.put("uid", createUid());
            var0.put("apiKey", config.getApiKey());
            var0.put("build_number", VERSION);
            var0.put("brand", Build.BRAND);
            var0.put("device", Build.DEVICE);
            var0.put("os_version", Build.VERSION.RELEASE);
            var0.put("model", Build.MODEL);
            var0.put("manufacturer", Build.MANUFACTURER);
            var0.put("mainboard", Build.BOARD);
            var0.put("country", Locale.getDefault().getCountry());
            try {
                PackageInfo var84 = var2xct.getPackageManager().getPackageInfo(var2xct
                        .getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO);

                var0.put("package_name", var2xct.getPackageName());
                var0.put("app_version_code", Integer.toString(var84.versionCode));
                var0.put("app_version_name", var84.versionName);
                var0.put("firstInstallTime", JDateFormat.getDataFormatter("yyyy-MM-dd_HHmmssZ").
                        format(new Date(var84.firstInstallTime)));

            } catch (Exception var63) {
                JLogger.errorLog("Exception while collecting app version data " + var63.getLocalizedMessage());
            }

            try {
                String var2 = Settings.Secure.getString(var2xct.getContentResolver(), "android_id");
                var0.put("android_id", var2);
                var0.put("sdk", Integer.toString(Build.VERSION.SDK_INT));
            } catch (Exception e) {
                JLogger.errorLog("[Error] while collecting android id" + e.getLocalizedMessage());
            }

        } catch (Exception e) {
            JLogger.errorLog("[Error] while collecting data" + e.getLocalizedMessage());
        }
        JLogger.debugLog(var0.toString());
        return var0;
//        try {
//            JEvent var0x = new JEvent();
//            var0x.addParams(var0);
//            var0x.context(var2xct);
//            var0x.Url(INTSALLCOUNTURL);
//            JDB.$$B(var0x);
//        } catch (Exception e) {
//            JLogger.errorLog("[Error] while sending request " + e.getLocalizedMessage());
//        }
    }

    private static final String PRST = "lcbyTKasUajAKasAlenm78ac9w2";

    private static long getAppInstallTime(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            return System.currentTimeMillis();
        }
    }

    private static String generateSha256Uuid(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            return UUID.nameUUIDFromBytes(hashBytes).toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8)).toString();
        }
    }

    private String createUid() {
        SharedPreferences var0 = getSharedPreferences(var2xct);
        String installedUser = var0.getString(INSTALLATION_ID, null);
        if (installedUser != null) {
            return installedUser;
        }
        try {
            String model = Build.MANUFACTURER.replace(" ", "-");
            installedUser = getAppInstallTime(var2xct) + "-" +
                    var2xct.getPackageName() + "-" + model + "-" + PRST;
            installedUser = generateSha256Uuid(installedUser);
        } catch (Exception e) {
            JLogger.errorLog("[Error] While creating id " + e.getLocalizedMessage());
            installedUser = String.valueOf(System.currentTimeMillis() + "-" + var2xct.getPackageName());

        }
        var0.edit().putString(INSTALLATION_ID, installedUser).apply();
        return installedUser;
    }

    private static SharedPreferences getSharedPreferences(Context var0) {
        return var0.getSharedPreferences(var34sp, Context.MODE_PRIVATE);
    }

    @Override
    protected void launchUpdateScreen(@NonNull boolean isForceUpdate) {
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isForceUpdate) {
                    if (var111x) {
                        Intent intent = new Intent(var2xct, UpdateBlocking.class);
                        intent.putExtra("update_url", config.getUpdatedApkUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        var2xct.startActivity(intent);
                        var111x = false;
                    } else {
                        if (JLifecycleListener.getCurrentActivity() instanceof UpdateBlocking) {
                            JLogger.debugLog("OK_111x");
                        } else {
                            var111x = true;
                            launchUpdateScreen(true);
                        }
                    }
                } else {
                    JUpdateSheetController.launch(config);
                }
            });
            if (var5tdx != null) {
                var5tdx.interrupt();
                var5tdx = null;
            }
        } catch (Exception e) {
            JLogger.errorLog("[Error] While Closing Thread " + e.getLocalizedMessage());
        }

    }

    public static JUpdater getInstance() {
        return var1xi;
    }


}
