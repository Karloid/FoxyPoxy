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

import java.util.ArrayList;

public class FoxyPoxyBotVerticle extends AbstractVerticle implements BotDelegate {
    private static final String LOG_TAG = "FoxyPoxyBotVerticle";
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
        String ourMessage = "Hello " + message.from.firstName + " " +
                message.from.lastName + (Math.random() > 0.5f ? ". You are cool :>" : ". Sorry, you not cool");
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
