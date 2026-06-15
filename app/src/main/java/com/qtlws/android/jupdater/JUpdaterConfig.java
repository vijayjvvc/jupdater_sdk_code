
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class JUpdaterConfig {
    private boolean forceUpdateEnabled = false;
    private boolean isDebug = false;
    private int forceUpdateThreshold = 5;
    private String updatedApkUrl;
    private String customServerUrl;

    private final String apiKey;

    public String getCustomServerUrl() {
        return customServerUrl;
    }

    protected void setCustomServerUrl(String customServerUrl) {
        this.customServerUrl = customServerUrl;
    }


    protected void setUpdatedApkUrl(String updatedApkUrl) {
        this.updatedApkUrl = updatedApkUrl;
    }

    public static JUpdaterConfig useCustomServer(@NonNull String updatedApkUrl,@NonNull String customServerUrl) {
        return new JUpdaterConfig(updatedApkUrl, customServerUrl);
    }

    public static JUpdaterConfig useManagedServer(@NonNull String apiKey) {
        return new JUpdaterConfig(apiKey);
    }

    private JUpdaterConfig(@NonNull String updatedApkUrl,@NonNull String customServerUrl) {
        if (updatedApkUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Update APK URL cannot be null or empty.");
        }
        if (customServerUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot be null or empty.");
        }
        this.updatedApkUrl = updatedApkUrl;
        this.customServerUrl = customServerUrl;
        this.apiKey = null;
        enableForceUpdate(forceUpdateThreshold);
    }

    private JUpdaterConfig(@NonNull String apiKey) {
        if (apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API KEY cannot be null or empty.");
        }
        this.apiKey = apiKey;
        this.updatedApkUrl = "backend";
//        this.customServerUrl = "https://kiqzkltcfhdjvcttzuxy.supabase.co/functions/v1/check-update";
        this.customServerUrl = "https://jupdater-main-web.onrender.com/api/check-update";
        enableForceUpdate(forceUpdateThreshold);
    }

    public boolean isUsingManagedServer() {
        return apiKey != null;
    }

    @Nullable
    public String getApiKey() {
        return apiKey;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    protected boolean isDebug() {
        return isDebug;
    }

    protected void enableForceUpdate(int threshold) {
        if (threshold<=0) {
            throw new IllegalArgumentException("Threshold cannot be less then 1");
        }
        this.forceUpdateEnabled = true;
        this.forceUpdateThreshold = threshold;
    }

    public boolean isForceUpdateEnabled() {
        return forceUpdateEnabled;
    }

    protected int getForceUpdateThreshold() {
        return forceUpdateThreshold;
    }


    protected String getUpdatedApkUrl() {
        return updatedApkUrl;
    }
}
