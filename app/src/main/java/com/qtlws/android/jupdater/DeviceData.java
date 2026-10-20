/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Builds the payload sent in <b>managed mode only</b>. Keep this list in sync with PRIVACY.md.
 * All values are sent as strings for compatibility with existing server versions.
 * The Android ID is intentionally NOT collected.
 */
final class DeviceData {

    private DeviceData() {
    }

    @NonNull
    static JSONObject collect(@NonNull AppInfo app, @NonNull String installationId, @Nullable String apiKey)
            throws JSONException {
        JSONObject o = new JSONObject();
        o.put("uid", installationId);
        o.put("apiKey", apiKey);
        o.put("build_number", BuildConfig.VERSION);          // SDK version
        o.put("brand", Build.BRAND);
        o.put("device", Build.DEVICE);
        o.put("os_version", Build.VERSION.RELEASE);
        o.put("model", Build.MODEL);
        o.put("manufacturer", Build.MANUFACTURER);
        o.put("mainboard", Build.BOARD);
        o.put("country", Locale.getDefault().getCountry());
        o.put("sdk", Integer.toString(Build.VERSION.SDK_INT));
        o.put("package_name", app.packageName);
        o.put("app_version_code", Integer.toString(app.versionCode));
        o.put("app_version_name", app.versionName);
        o.put("sdk", app.sdk);
        o.put("firstInstallTime", app.firstInstallTimeFormatted());
        return o;
    }
}
