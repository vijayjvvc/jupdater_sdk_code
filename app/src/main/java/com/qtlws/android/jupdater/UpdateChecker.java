
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import static com.qtlws.android.jupdater.Constants.var35sp;
import static com.qtlws.android.jupdater.Constants.var34sp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class UpdateChecker {

    public static void checkForUpdate(Context context, JUpdaterConfig config, Map<String, Object> data) {
        try {
            String packageName = context.getPackageName();
            int currentVersionCode = getAppVersionCode(context);
            JLogger.debugLog("JUpdater Got AppVersionCode");

            HttpURLConnection connection = getHttpURLConnection(config, packageName, currentVersionCode, data);

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                reader.close();
                connection.disconnect();

                // Parse JSON
                JSONObject jsonObject = new JSONObject(jsonBuilder.toString());

                String serverPid = jsonObject.getString("pid");
                int serverVersionCode = jsonObject.getInt("version_code");
                JLogger.debugLog("Data res : " + jsonObject.toString());

                context.getSharedPreferences(var34sp, Context.MODE_PRIVATE).edit().
                        putInt(var35sp, serverVersionCode).apply();

                if (!packageName.equals(serverPid)) {
                    throw new Exception("Package Name doesn't match");
                }

                JLogger.debugLog("Versions no : " + serverVersionCode);
                JLogger.debugLog("Versions no : " + currentVersionCode);

                if (serverVersionCode > currentVersionCode) {
                    boolean isBlocked = jsonObject.getBoolean("is_blocked");
                    if (isBlocked) throw new Exception("App Is Blocked");
                    String updateUrl = jsonObject.getString("update_url");
                    int force_update_gap = jsonObject.getInt("force_update_gap");
                    config.setUpdatedApkUrl(updateUrl);
                    config.enableForceUpdate(force_update_gap);
                    JLogger.debugLog("Setting APK URL : " + updateUrl + " and threshold : " + force_update_gap);
                    boolean isForce = config.isForceUpdateEnabled()
                            && (serverVersionCode - currentVersionCode >= config.getForceUpdateThreshold());


                    JLogger.debugLog("Launching " + (isForce ? "FORCE" : "NON-FORCE") + " update screen");
                    JUpdaterCore.getInstance().launchUpdateScreen(isForce);

                } else {
                    JLogger.debugLog("You are using Latest version of the application.");
                }

            } else {
                JLogger.errorLog("Server response error: " + responseCode + "with message " + responseMessage);
            }

        } catch (Exception e) {
            JLogger.errorLog("Error while checking update: " + e.getLocalizedMessage());
            if (e instanceof java.net.UnknownHostException ||
                    e instanceof java.net.SocketTimeoutException ||
                    e instanceof java.net.ConnectException) {
                int savedServerVersionCode = context.getSharedPreferences(var34sp, Context.MODE_PRIVATE)
                        .getInt(var35sp, 1);
                int currentVersionCode = getAppVersionCode(context);

                if (savedServerVersionCode > currentVersionCode) {
                    boolean isForce = config.isForceUpdateEnabled()
                            && (savedServerVersionCode - currentVersionCode >= config.getForceUpdateThreshold());

                    JLogger.debugLog("Launching fallback " + (isForce ? "FORCE" : "NON-FORCE") + " update screen");
                    JUpdaterCore.getInstance().launchUpdateScreen(isForce);
                }
            }
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(JUpdaterConfig config, String packageName, int currentVersionCode, Map<String, Object> data) throws IOException {
//        String finalServerUrl = config.getCustomServerUrl();
//
//        if (finalServerUrl==null||finalServerUrl.trim().isEmpty()){
//            finalServerUrl = UPDATEVERSIONURL;
//        }

        // Prepare the URL with parameters
        String urlString = config.getCustomServerUrl()
                + "?pid=" + packageName
                + "&version_code=" + currentVersionCode;


        JLogger.debugLog("JUpdater Final Url : " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        if (config.isUsingManagedServer()) {
            byte[] var3 = convertMapToJson(data).getBytes();
            int var14 = var3.length;
            connection.setRequestProperty("Content-Length", String.valueOf(var14));
            connection.setRequestMethod("POST");
            connection.setRequestProperty("x-api-key", config.getApiKey());
            connection.setRequestProperty("Content-Type", "application/json");

            DataOutputStream var15 = null;
            boolean var48 = false;

            try {
                var48 = true;
                (var15 = new DataOutputStream(connection.getOutputStream())).write(var3);
                var48 = false;
            } finally {
                if (var48) {
                    if (var15 != null) {
                        var15.close();
                    }

                }
            }

            var15.close();
            String var25 = url.toString();
            JLogger.infoLog("response url: ".concat(String.valueOf(var25)));
        } else {
            connection.setRequestMethod("GET");
        }
        connection.setConnectTimeout(15000); // 15 seconds
        connection.setReadTimeout(15000);    // 15 seconds

        return connection;
    }


    private static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) packageInfo.getLongVersionCode();
            } else {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static String convertMapToJson(Map<String, Object> mapData) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : mapData.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            String key = escapeString(entry.getKey());
            Object value = entry.getValue();
            String valueString = escapeString(value.toString());
            sb.append("\"").append(key).append("\"").append(":").append("\"").append(valueString).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeString(String str) {
        return str.replace("\"", "\\\"");
    }


}
