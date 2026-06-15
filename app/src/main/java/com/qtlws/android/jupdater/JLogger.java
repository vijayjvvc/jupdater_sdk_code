
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import static com.qtlws.android.jupdater.JUpdaterCore.SDKNAME;
import static com.qtlws.android.jupdater.JUpdaterCore.VERSION;

import android.util.Log;

class JLogger {

    private static String TAG = SDKNAME + "_V" + VERSION;

    protected static boolean var36dgx = false;

    public static void errorLog(Object s) {
        Log.e(TAG, s.toString());
    }

    public static void debugLog(Object s) {
        if (var36dgx) {
            Log.d(TAG, s.toString());
        }
    }

    public static void verboseLog(Object s) {
        if (var36dgx) {
            Log.v(TAG, s.toString());
        }
    }

    public static void infoLog(Object s) {
        Log.i(TAG, s.toString());
    }

}
