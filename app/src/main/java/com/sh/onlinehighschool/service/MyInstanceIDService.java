package com.sh.onlinehighschool.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + refreshedToken);
    }
}
