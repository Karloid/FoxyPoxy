package com.krld.foxypoxy.api;

import com.krld.foxypoxy.models.Updates;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TelegramApi {
    @GET("getUpdates")
    Call<Updates> getUpdates();
}
