package com.festivaltime.festivaltimeproject;

import android.os.Bundle;

public class DataHolder {
    private static final DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private Bundle bundle;

    private DataHolder() {
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
