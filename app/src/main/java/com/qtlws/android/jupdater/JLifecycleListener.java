
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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class JLifecycleListener {

    @VisibleForTesting
    public static long CHECK_DELAY = 500L;
    protected static ForegroundListener listener;

    private static WeakReference<Activity> currentActivityRef = null;

    protected static Activity getCurrentActivity() {
        return currentActivityRef != null ? currentActivityRef.get() : null;
    }


    protected interface ForegroundListener {

        void onAppForeground(Activity var1);

        void onAppBackground(Context var1);
    }

    protected static void $$b(Context var0, final ForegroundListener var1) {
        listener = var1;

        Application.ActivityLifecycleCallbacks var2 = new Application.ActivityLifecycleCallbacks() {
            boolean isResume;
            boolean isPause = true;
            private Executor ex = Executors.newSingleThreadExecutor();

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity var1x) {
                currentActivityRef = new WeakReference<>(var1x);
                this.ex.execute(() -> {
                    if (!isResume) {
                        try {
                            var1.onAppForeground(var1x);
                        } catch (Exception var212) {
                            JLogger.errorLog("Listener thrown an exception: " + var212.getLocalizedMessage());
                        }
                    }

                    isPause = false;
                    isResume = true;
                });
            }

            @Override
            public void onActivityPaused(@NonNull Activity var1x) {
                if (currentActivityRef != null && currentActivityRef.get() == var1x) {
                    currentActivityRef.clear();
                }
                this.ex.execute(() -> {
                    isPause = true;
                    final Context var1xx = var1x.getApplicationContext();

                    try {
                        (new Timer()).schedule(new TimerTask() {
                            public void run() {
                                if (isResume && isPause) {
                                    isResume = false;

                                    try {
                                        var1.onAppBackground(var1xx);
                                        return;
                                    } catch (Exception var21) {
                                        JLogger.errorLog("Listener threw exception! " + var21.getLocalizedMessage());
                                    }
                                }

                            }
                        }, JLifecycleListener.CHECK_DELAY);
                    } catch (Throwable var3) {
                        JLogger.errorLog("Background task failed with a throwable: " + var3.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        };
        if (var0 instanceof Activity) {
            var2.onActivityResumed((Activity) var0);
        }
        ((Application) var0.getApplicationContext()).registerActivityLifecycleCallbacks(var2);
    }
}