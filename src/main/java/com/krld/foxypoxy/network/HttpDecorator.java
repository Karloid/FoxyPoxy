package com.krld.foxypoxy.network;

import com.google.gson.Gson;
import com.krld.foxypoxy.util.Action1;
import com.krld.foxypoxy.util.VertxUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;

public class HttpDecorator {
    private final String token;
    private HttpClient httpClient;
    private Gson gson;

    public HttpDecorator(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;

        token = VertxUtils.getToken();
    }

    public <T> void post(String method, Object body, Action1<T> success, Action1<MatrixError> fail, Class<T> respClass) { //TODO calculate request time
        try {
            long startTime = System.currentTimeMillis();
            httpClient.post(method, event -> handleResponse(success, fail, respClass, event, startTime))
                    .exceptionHandler(getExceptionHandler(fail))
                    .putHeader("Content-Type", "application/json")
                    .end(gson.toJson(body));
        } catch (IllegalStateException ignore) {
        }
    }

    public <T> void put(String method, Object body, Action1<T> success, Action1<MatrixError> fail, Class<T> respClass) {
        try {
            long startTime = System.currentTimeMillis();
            httpClient.put(method, event -> handleResponse(success, fail, respClass, event, startTime))
                    .exceptionHandler(getExceptionHandler(fail))
                    .putHeader("Content-Type", "application/json")
                    .end(gson.toJson(body));
        } catch (IllegalStateException ignore) {
        }
    }

    public <T> void get(String method, Action1<T> success, Action1<MatrixError> fail, Class<T> respClass) {
        try {
            long startTime = System.currentTimeMillis();
            httpClient.get(method, event -> handleResponse(success, fail, respClass, event, startTime))
                    .exceptionHandler(getExceptionHandler(fail))
                    .end();
        } catch (IllegalStateException ignore) {

        }
    }

    public void getString(String method, Action1<String> success, Action1<MatrixError> fail) {
        try {
            long startTime = System.currentTimeMillis();//TODO refactor
            httpClient.get(method, event -> handleResponse(success, fail, String.class, event, startTime))
                    .exceptionHandler(getExceptionHandler(fail))
                    .end();
        } catch (IllegalStateException ignore) {

        }
    }

    private Handler<Throwable> getExceptionHandler(Action1<MatrixError> fail) {
        return event -> {
            if (fail != null) {
                fail.call(new MatrixError(event));
            }
        };
    }

    private <T> HttpClientResponse handleResponse(Action1<T> success, Action1<MatrixError> fail, Class<T> respClass, HttpClientResponse event, long startTime) {
        return event.bodyHandler(buffer -> {
            //FLog.d("x", "requested processed in " + (System.currentTimeMillis() - startTime) + "ms");
            String response = buffer.getString(0, buffer.length());
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
                MatrixError error;
                try {
                    error = gson.fromJson(response, MatrixError.class);
                } catch (Exception e) {
                    error = new MatrixError();
                }
                fail.call(error);
            }
        });
    }

    public String getToken() {
        return token;
    }
}
