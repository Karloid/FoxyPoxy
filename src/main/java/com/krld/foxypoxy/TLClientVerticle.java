package com.krld.foxypoxy;

import com.google.gson.Gson;
import com.krld.foxypoxy.network.HttpDecorator;
import com.krld.foxypoxy.util.Addresses;
import com.krld.foxypoxy.util.FLog;
import com.krld.foxypoxy.models.Update;
import com.krld.foxypoxy.responses.ResponseGetUpdates;
import com.krld.foxypoxy.util.JsonUtils;
import com.krld.foxypoxy.util.VertxUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;

public class TLClientVerticle extends AbstractVerticle {

    private static final String LOG_TAG = "TLClientVerticle";
    private Integer offset;
    private HttpClient httpClient;
    private Gson gson;
    private HttpDecorator httpDecorator;
    private String token;

    public TLClientVerticle() {
    }

    @Override
    public void start() throws Exception {

        httpClient = VertxUtils.getHttpClient(vertx);
        gson = JsonUtils.getGson(false);
        httpDecorator = new HttpDecorator(httpClient, gson);

        token = VertxUtils.getToken();
        offset = 0;

        getUpdates();
    }

    private void getUpdates() {
        httpDecorator.getString("/bot" + token + String.format("/getUpdates?offset=%d&limit=%d&timeout=%d", offset, 100, 15), //TODO move token to somewhere
                origString -> {
                    FLog.d(LOG_TAG, "get response from server " + origString);
                    sendEventBus(Addresses.NEW_UPDATE, origString);
                    ResponseGetUpdates getUpdates = gson.fromJson(origString, ResponseGetUpdates.class);
                    for (Update update : getUpdates.result) {
                        offset = update.updateId + 1;
                    }
                    getUpdates();
                },
                e -> {
                    FLog.d(LOG_TAG, "!error received" + e);
                    vertx.setTimer(1000, event -> getUpdates());

                });
    }

    private EventBus sendEventBus(String address, Object message) {
        return vertx.eventBus().send(address, message);
    }


}
