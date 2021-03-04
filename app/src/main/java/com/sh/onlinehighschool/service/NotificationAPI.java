package com.sh.onlinehighschool.service;

import androidx.annotation.Nullable;

import com.sh.onlinehighschool.model.PushNotification;
import com.sh.onlinehighschool.model.Response;
import com.sh.onlinehighschool.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {

    @Headers({"Authorization: key=" + Constants.SERVER_KEY, "Content-Type:" + Constants.CONTENT_TYPE})
    @POST("fcm/send")
    @Nullable
    Call<Response> postNotification(@Body PushNotification data);

}
