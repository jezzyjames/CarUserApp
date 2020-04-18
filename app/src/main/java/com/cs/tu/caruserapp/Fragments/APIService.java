package com.cs.tu.caruserapp.Fragments;

import com.cs.tu.caruserapp.Model.Sender;
import com.cs.tu.caruserapp.Notification.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA4sSZLgI:APA91bETdQQWK-YecRNe-GwYypY42wJ6QrvN7WdMvYqlqz74CmsSOP1NGKgeh7_2LX7k8_DQudxZR0JgiCDVwcLjxOFdz9wJ2i6oaDQSlu3lZIHw3LT6hrOl8Dq2XggaDjHEsk23z9Op"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
