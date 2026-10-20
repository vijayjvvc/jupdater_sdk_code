/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Snapshot of the host app's package info, read with a single PackageManager call. */
final class AppInfo {

    final String packageName;
    final String sdk;
    final int versionCode;      // -1 if unknown
    final String versionName;
    final long firstInstallTime;

    private AppInfo(String packageName, String sdk, int versionCode, String versionName, long firstInstallTime) {
        this.packageName = packageName;
        this.sdk = sdk;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.firstInstallTime = firstInstallTime;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    static AppInfo read(@NonNull Context context) {

        String pkg = context.getPackageName();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(pkg, 0);
            int code = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? (int) pi.getLongVersionCode()
                    : pi.versionCode;
            return new AppInfo(pkg,Integer.toString(Build.VERSION.SDK_INT),code, pi.versionName, pi.firstInstallTime);
        } catch (Exception e) {
            JLogger.w("Could not read package info: " + e.getLocalizedMessage());
            return new AppInfo(pkg,Integer.toString(Build.VERSION.SDK_INT) ,-1, null, 0L);
        }
    }

    String firstInstallTimeFormatted() {
        if (firstInstallTime <= 0) return null;
        return new SimpleDateFormat("yyyy-MM-dd_HHmmssZ", Locale.US).format(new Date(firstInstallTime));
    }


}