package com.s1mar.covid19tracker;


import androidx.multidex.MultiDexApplication;

import com.google.firebase.firestore.FirebaseFirestore;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }
}
