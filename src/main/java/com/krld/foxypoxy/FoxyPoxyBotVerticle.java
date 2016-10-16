package com.krld.foxypoxy;

import com.google.gson.Gson;
import com.krld.foxypoxy.models.InlineKeyboardMarkup;
import com.krld.foxypoxy.models.Message;
import com.krld.foxypoxy.models.SendMessageParams;
import com.krld.foxypoxy.models.Update;
import com.krld.foxypoxy.models.buttons.InlineKeyboardButton;
import com.krld.foxypoxy.network.HttpDecorator;
import com.krld.foxypoxy.responses.ResponseGetUpdates;
import com.krld.foxypoxy.util.Addresses;
import com.krld.foxypoxy.util.FLog;
import com.krld.foxypoxy.util.JsonUtils;
import com.krld.foxypoxy.util.VertxUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;

import java.awt.*;
import java.util.ArrayList;

public class FoxyPoxyBotVerticle extends AbstractVerticle implements BotDelegate {
    private static final String LOG_TAG = "FoxyPoxyBotVerticle";

    private static final int FIELD_HEIGHT = 12;
    private static final int FIELD_WIDTH = 28;

    private TLClientVerticle tlClientVerticle;
    private HttpClient httpClient;
    private Gson gson;
    private HttpDecorator httpDecorator;


    @Override
    public void start() throws Exception {
        httpClient = VertxUtils.getHttpClient(vertx);
        gson = JsonUtils.getGson(false);
        httpDecorator = new HttpDecorator(httpClient, gson);

        vertx.eventBus().<String>consumer(Addresses.NEW_UPDATE, event -> processUpdate(event.body()));
    }

    private void processUpdate(String body) {
        ResponseGetUpdates updates = gson.fromJson(body, ResponseGetUpdates.class);
        for (Update update : updates.result) {
            onMessage(update.message);
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message == null) {
            return;
        }
        System.out.println(message.from.firstName + " " + message.from.lastName + ": " + message.text);
        String ourMessage = "";
        Point center = new Point(FIELD_WIDTH / 2, FIELD_HEIGHT / 2);
        for (int y = 0; y <= FIELD_HEIGHT; y++) {
            for (int x = 0; x <= FIELD_WIDTH; x++) {
                double r = Math.random();
                if (x == center.x && y == center.y) {
                    ourMessage += "@";
                } else if (r < 0.25) {
                    ourMessage += ",";
                } else if (r < 0.5) {
                    ourMessage += "_";
                } else {
                    ourMessage += ".";
                }
            }
            ourMessage += "\n";
        }
        ourMessage = "```java\n" + ourMessage + "```"; //simple lang

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.inlineKeyboard = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬆️⬆️⬆️"));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton("⬅⬅⬅️️"));
        buttons.add(new InlineKeyboardButton("Refresh"));
        buttons.add(new InlineKeyboardButton("➡➡➡️"));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬇⬇️⬇️️"));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);


        sendMessage(message.chat.id, ourMessage, keyboard);
    }

    private void sendMessage(Integer id, String ourMessage, InlineKeyboardMarkup keyboard) {
        SendMessageParams body = new SendMessageParams(id, ourMessage, keyboard);
        httpDecorator.post("/bot" + httpDecorator.getToken() + "/sendMessage",
                body,
                value -> FLog.d(LOG_TAG, "done send message "),
                e -> {
                    FLog.e(LOG_TAG, "error " + e);
                }, Object.class);
    }
}
