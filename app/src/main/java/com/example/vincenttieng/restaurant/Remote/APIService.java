package com.example.vincenttieng.restaurant.Remote;

import com.example.vincenttieng.restaurant.Model.MyResponse;
import com.example.vincenttieng.restaurant.Model.Sender;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Header;

import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfww-I9c:APA91bE-yqZKp7R0RViMBXlVscAefb9DqfpdXqdEu66r-La6KDv_HgspqI6J4bmmowEEUC_1GqEIegLpmONfxxLm4RTpXm8WSIkIXr25Z91IRXmINPBKpg3NdlGWdrGwvs0tP4vRHvGcKIb4svh5D_terZ47jK78pg"
            }

    )

    @POST("fcm/send")
    retrofit2.Call<MyResponse> sendNotification(@Body Sender body);
}
