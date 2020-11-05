package com.mayank.punjabidelight;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQB_T6UI:APA91bEuz8ho4e8kDnLNUtKFuIz-xVaMc4TZ1jOkJ7t2KxzIwdz-1EV2yV58tP5gM_XSqIDkmtUI6o3qoloLBOPVzNNMMk6BgQCgEk_KiFXZqd-u9GG5O2ZzkYsqZHQzn6iTXdIAiH-c"//
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
