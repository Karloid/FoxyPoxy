package com.krld.foxypoxy;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krld.foxypoxy.api.TelegramApi;
import com.krld.foxypoxy.models.Message;
import com.krld.foxypoxy.models.Update;
import com.krld.foxypoxy.network.LoggingInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TLClient {

    private Integer offset;
    private TelegramApi telegramApi;

    public void run() {
        telegramApi = setupTelegramApi();
        offset = 0;
        while (true) {
            try {
                List<Update> updates = telegramApi.getUpdates(offset + "", 100 + "", 15 + "").execute().body().result;
                for (Update update : updates) {
                    System.out.println(update.message);
                    offset = update.updateId + 1;
                    processUpdate(update);
                }
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processUpdate(Update update) {
        Message message = update.message;
        if (message != null) {
            System.out.println(message.from.firstName + " " + message.from.lastName + ": " + message.text);
            String ourMessage = "Hello " + message.from.firstName + " " +
                    message.from.lastName + (Math.random() > 0.5f ? ". You are cool :>" : ". Sorry, you not cool");
            telegramApi.sendMessage(message.chat.id, ourMessage)
                    .enqueue(new VoidCallback());
        }
    }

    private TelegramApi setupTelegramApi() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File("secret.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = prop.getProperty("token");

        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS).addInterceptor(new LoggingInterceptor()).build();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telegram.org/bot" + token + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        return retrofit.create(TelegramApi.class);
    }

    private static class VoidCallback implements Callback<Void> {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {

        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {

        }
    }
}
