package com.qtlws.android.jupdater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class JThread extends Thread {

    public JThread() {
    }

    public JThread(@Nullable Runnable target) {
        super(target);
    }

    public JThread(@Nullable ThreadGroup group, @Nullable Runnable target) {
        super(group, target);
    }

    public JThread(@NonNull String name) {
        super(name);
    }

    public JThread(@Nullable ThreadGroup group, @NonNull String name) {
        super(group, name);
    }

    public JThread(@Nullable Runnable target, @NonNull String name) {
        super(target, name);
    }

    public JThread(@Nullable ThreadGroup group, @Nullable Runnable target, @NonNull String name) {
        super(group, target, name);
    }

    public JThread(@Nullable ThreadGroup group, @Nullable Runnable target, @NonNull String name, long stackSize) {
        super(group, target, name, stackSize);
    }

}
