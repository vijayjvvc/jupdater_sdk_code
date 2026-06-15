
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JDateFormat {
    public static String dateFormatUTC(SimpleDateFormat var0, long var1) {
        var0.setTimeZone(TimeZone.getTimeZone("UTC"));
        return var0.format(new Date(var1));
    }

    public static SimpleDateFormat getDataFormatter(String var0) {
        return new SimpleDateFormat(var0, Locale.US);
    }
}
