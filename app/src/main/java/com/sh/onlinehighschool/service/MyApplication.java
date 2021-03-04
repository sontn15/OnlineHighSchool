package com.sh.onlinehighschool.service;

import android.app.Application;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sh.onlinehighschool.utils.Pref;

public class MyApplication extends Application {

    private static final String LOG_TAG = "SONTN7";

    @Override
    public void onCreate() {
        super.onCreate();
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        Pref pref = new Pref(getApplicationContext());
        if (pref.getDataInt(Pref.KHOI) != -1) {
            String topic = "khoi" + pref.getDataInt(Pref.KHOI);
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "subscribeToTopic " + topic + " successfully!"));
        }
    }
}
