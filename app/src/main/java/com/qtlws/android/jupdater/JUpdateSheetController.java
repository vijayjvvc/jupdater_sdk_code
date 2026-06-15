
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.app.Activity;

class JUpdateSheetController {

    private static boolean hasSheetShown = false;
    private static String updateUrl;

    public static void launch(JUpdaterConfig var6xc) {
        updateUrl = var6xc.getUpdatedApkUrl();
        Activity var1xc = JLifecycleListener.getCurrentActivity();

        if (var1xc != null && !var1xc.isFinishing()) {
            showBottomSheet(var1xc);
        }
    }


    private static void showBottomSheet(Activity activity) {
        if (hasSheetShown || activity == null || activity.isFinishing()) return;
        hasSheetShown = true;
        JUpdateBottomSheet.show(activity, updateUrl);
    }
}
