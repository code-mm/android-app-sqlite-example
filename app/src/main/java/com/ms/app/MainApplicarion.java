package com.ms.app;

import android.app.Application;

public class MainApplicarion extends Application {

    private static MainApplicarion instance;

    public static MainApplicarion getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
