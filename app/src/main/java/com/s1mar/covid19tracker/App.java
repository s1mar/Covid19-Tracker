package com.s1mar.covid19tracker;


import androidx.multidex.MultiDexApplication;

import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.utils.PlayerPrefs;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onTerminate() {
        PlayerPrefs.clear(this);
        super.onTerminate();
    }
}
