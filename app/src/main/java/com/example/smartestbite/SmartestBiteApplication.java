package com.example.smartestbite;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class SmartestBiteApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
