package com.cocodev.TDUCManager;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Sudarshan on 07-06-2017.
 */

public class MyApplication extends Application {

    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
