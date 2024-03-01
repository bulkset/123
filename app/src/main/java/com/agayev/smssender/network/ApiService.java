package com.agayev.smssender.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("sendMessage")
    Call<ResponseBody> sendMessage(@Query("chat_id") String chatId, @Query("text") String text);
}
