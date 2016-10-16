package com.krld.foxypoxy.network;

import com.google.gson.Gson;
import com.krld.foxypoxy.util.Action1;
import com.krld.foxypoxy.util.FLog;
import com.krld.foxypoxy.util.VertxUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;

public class HttpDecorator {
    private static final String LOG_TAG = "HttpDecorator";
    private final String token;
    private HttpClient httpClient;
    private Gson gson;

    public HttpDecorator(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;

        token = VertxUtils.getToken();
    }

    //TODO one method for all
    public <T> void post(String method, Object body, Action1<T> success, Action1<TlError> fail, Class<T> respClass) { //TODO calculate request time
        try {
            long startTime = System.currentTimeMillis();
            FLog.d(LOG_TAG, "--> " + method);
            httpClient.post(addBot(method), event -> handleResponse(success, fail, respClass, event, startTime, method))
                    .exceptionHandler(getExceptionHandler(fail))
                    .putHeader("Content-Type", "application/json")
                    .end(gson.toJson(body));
        } catch (IllegalStateException ignore) {
        }
    }

    public <T> void put(String method, Object body, Action1<T> success, Action1<TlError> fail, Class<T> respClass) {
        try {
            long startTime = System.currentTimeMillis();
            FLog.d(LOG_TAG, "--> " + method);
            httpClient.put(addBot(method), event -> handleResponse(success, fail, respClass, event, startTime, method))
                    .exceptionHandler(getExceptionHandler(fail))
                    .putHeader("Content-Type", "application/json")
                    .end(gson.toJson(body));
        } catch (IllegalStateException ignore) {
        }
    }

    public <T> void get(String method, Action1<T> success, Action1<TlError> fail, Class<T> respClass) {
        try {
            long startTime = System.currentTimeMillis();
            FLog.d(LOG_TAG, "--> " + method);
            httpClient.get(addBot(method), event -> handleResponse(success, fail, respClass, event, startTime, method))
                    .exceptionHandler(getExceptionHandler(fail))
                    .end();
        } catch (IllegalStateException ignore) {

        }
    }

    public void getString(String method, Action1<String> success, Action1<TlError> fail) {
        try {
            long startTime = System.currentTimeMillis();//TODO refactor
            httpClient.get(method, event -> handleResponse(success, fail, String.class, event, startTime, method))
                    .exceptionHandler(getExceptionHandler(fail))
                    .end();
        } catch (IllegalStateException ignore) {

        }
    }

    private Handler<Throwable> getExceptionHandler(Action1<TlError> fail) {
        return event -> {
            if (fail != null) {
                fail.call(new TlError(event));
            }
        };
    }

    private <T> HttpClientResponse handleResponse(Action1<T> success, Action1<TlError> fail, Class<T> respClass, HttpClientResponse event, long startTime, String method) {
        return event.bodyHandler(buffer -> {
            String response = buffer.getString(0, buffer.length());
            FLog.d(LOG_TAG, "<-- (" + event.statusCode() + ") " + method + " in " + (System.currentTimeMillis() - startTime) + "ms\n" + response);
            if (event.statusCode() == 200) {
                if (success == null) {
                    return;
                }
                if (respClass == String.class) {
                    success.call((T) response);
                } else {
                    @SuppressWarnings("unchecked") T objectResponse = (T) gson.fromJson(response, respClass);
                    success.call(objectResponse);
                }
            } else {
                //  log(event.statusCode() + " " + response);
                if (fail == null) {
                    return;
                }
                TlError error;
                try {
                    error = gson.fromJson(response, TlError.class);
                } catch (Exception e) {
                    error = new TlError();
                }
                fail.call(error);
            }
        });
    }

    public String getToken() {
        return token;
    }

    private String addBot(String method) {
        return "/bot" + getToken() + method;
    }
}
