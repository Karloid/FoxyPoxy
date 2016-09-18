package com.krld.foxypoxy.util;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VertxUtils {
    public static HttpClient getHttpClient(Vertx vertx) {
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true)
                .setTrustAll(true)
                .setVerifyHost(false)
                .setDefaultPort(443)
                .setDefaultHost("api.telegram.org");
        return vertx.createHttpClient(options);
    }

    public static String getToken() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File("secret.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("token");
    }
}
