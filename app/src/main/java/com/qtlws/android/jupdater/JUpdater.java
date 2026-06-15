
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

abstract public class JUpdater {

    public abstract void init(@NonNull Context context,JUpdaterConfig updaterConfig);
    public abstract void launchUrlInBrowser(@NonNull Context context,@NonNull String urlToOpen);
    protected abstract void launchUpdateScreen(@NonNull boolean isForceUpdate);

    public static JUpdater getInstance(){
        return  JUpdaterCore.getInstance();
    }


}
