package com.qtlws.android.jupdater;

import androidx.annotation.NonNull;

public class JUpdaterConfig {
    private boolean forceUpdateEnabled = false;
    private boolean isDebug = false;
    private int forceUpdateThreshold = 5;
    private String updatedApkUrl;
    private String customServerUrl;

    public String getCustomServerUrl() {
        return customServerUrl;
    }

    protected void setCustomServerUrl(String customServerUrl) {
        this.customServerUrl = customServerUrl;
    }

    public JUpdaterConfig(@NonNull String updatedApkUrl,@NonNull String customServerUrl) {
        if (updatedApkUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Update APK URL cannot be null or empty.");
        }
        if (customServerUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot be null or empty.");
        }
        this.updatedApkUrl = updatedApkUrl;
        this.customServerUrl = customServerUrl;
        enableForceUpdate(forceUpdateThreshold);
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    protected boolean isDebug() {
        return isDebug;
    }

    public void enableForceUpdate(int threshold) {
        if (threshold<=0) {
            throw new IllegalArgumentException("Threshold cannot be less then 1");
        }
        this.forceUpdateEnabled = true;
        this.forceUpdateThreshold = threshold;
    }

    public boolean isForceUpdateEnabled() {
        return forceUpdateEnabled;
    }

    public int getForceUpdateThreshold() {
        return forceUpdateThreshold;
    }

    public String getUpdatedApkUrl() {
        return updatedApkUrl;
    }
}
