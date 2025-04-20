package com.qtlws.android.jupdater;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Constants {


    public static final String var34sp = "j_updater_pref";
    public static final String var35sp = "j_updater_version";
//    public static String DATA = "data";
//    protected static final String UPDATEVERSIONURL = "https://bk-jupdater.onrender.com/update_status.json";

    public static String mapToString(Map<String, Object> map) {
        String r0 = new JSONObject(map).toString();
        String _r1 = r0.replaceAll("\\p{C}", "*Non-printing character*");
        boolean _r2 = _r1.equals(r0);
        if (!_r2) {
            JLogger.infoLog("Payload contains non-printing characters");
            r0 = _r1;
        }
            JLogger.infoLog("Payload saved");
        return r0;
    }

    public static Map<String, Object> stringToMap(String s) {
        try {
            Map<String,Object> map = new HashMap<>();;
            JSONObject jsonObject = new JSONObject(s);
            Iterator<String> keysItr = jsonObject.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    value = stringToMap((JSONObject) value);
                }
                map.put(key, value);
            }
            JLogger.debugLog("data = "+map.toString());
            return map;
        } catch (JSONException e) {
            JLogger.errorLog("[Error] While converting Map "+e.getLocalizedMessage());
            return null;
        }
    }
    public static Map<String, Object> stringToMap(JSONObject jsonObject) {
        try {
            Map<String,Object> map = new HashMap<>();;
            Iterator<String> keysItr = jsonObject.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    value = stringToMap((JSONObject) value);
                }
                map.put(key, value);
            }
            return map;
        } catch (JSONException e) {
            JLogger.errorLog("[Error] While converting Map jsonObject"+e.getLocalizedMessage());
            return null;
        }
    }
}
