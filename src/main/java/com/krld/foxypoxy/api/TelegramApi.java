package com.krld.foxypoxy.api;

import com.krld.foxypoxy.models.Update;
import com.krld.foxypoxy.responses.ResponseGetUpdates;
import retrofit2.Call;
import retrofit2.http.*;

public interface TelegramApi {
    @GET("getUpdates")
    Call<ResponseGetUpdates> getUpdates(@Query("offset") String offset, @Query("limit") String limit, @Query("timeout") String timeout);

    @FormUrlEncoded
    @POST("sendMessage")
    Call<Void> sendMessage(@Field("chat_id") Integer chatId, @Field("text") String text);
}
