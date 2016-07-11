package com.krld.foxypoxy;

import com.krld.foxypoxy.api.TelegramApi;
import com.krld.foxypoxy.models.Updates;
import com.krld.foxypoxy.network.LoggingInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TLClient {

    public void run() {
        TelegramApi telegramApi = setupTelegramApi();

        while (true) {
            System.out.println("Hello");
            try {
                Updates updates = telegramApi.getUpdates().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(new LoggingInterceptor()).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telegram.org/bot" + token + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(TelegramApi.class);
    }

}
