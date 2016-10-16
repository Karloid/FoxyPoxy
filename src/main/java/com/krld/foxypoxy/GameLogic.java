package com.krld.foxypoxy;

import com.google.gson.Gson;
import com.krld.foxypoxy.gmodels.Buttons;
import com.krld.foxypoxy.gmodels.Game;
import com.krld.foxypoxy.gmodels.Player;
import com.krld.foxypoxy.tlmodels.*;
import com.krld.foxypoxy.tlmodels.buttons.InlineKeyboardButton;
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

import static com.krld.foxypoxy.gmodels.Game.FIELD_HEIGHT;
import static com.krld.foxypoxy.gmodels.Game.FIELD_WIDTH;

public class GameLogic extends AbstractVerticle implements BotDelegate {
    private static final String LOG_TAG = "GameLogic";

    private TLClientVerticle tlClientVerticle;
    private HttpClient httpClient;
    private Gson gson;
    private HttpDecorator httpDecorator;

    private Game game = new Game();


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
            onCallbackQuery(update.callbackQuery);
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message == null) {
            return;
        }
        System.out.println(message.from.firstName + " " + message.from.lastName + ": " + message.text);
        Player player = game.getPlayer(message.chat.id);
        String ourMessage = render(player);
        InlineKeyboardMarkup keyboard = getKeyboard();
        sendMessage(message.chat.id, ourMessage, keyboard);
    }

    private void onCallbackQuery(CallbackQuery query) {
        if (query == null) {
            return;
        }
        FLog.d(LOG_TAG, "onCallbackQuery");
        Player player = game.getPlayer(query.message.chat.id);
        int x = player.getPos().x, y = player.getPos().y;
        switch (query.data) {
            case Buttons.TOP:
                y--;
                break;
            case Buttons.RIGHT:
                x++;
                break;
            case Buttons.LEFT:
                x--;
                break;
            case Buttons.BOT:
                y++;
                break;
            default:
                return;

        }
        player.getPos().setLocation(x, y);
        editMessage(query.message.chat.id, query.message.messageId, render(player), getKeyboard());
        //TODO parse action
        //TODO get new field
        //TODO send edit message
        //TODO render last action
    }

    private String render(Player player) {
        String out = "";
        Point playerPos = player.getPos();
        for (int y = 0; y <= FIELD_HEIGHT; y++) {
            for (int x = 0; x <= FIELD_WIDTH; x++) {
                double r = Math.random();
                if (x == playerPos.x && y == playerPos.y) {
                    out += "@";
                } else if (r < 0.25) {
                    out += ",";
                } else if (r < 0.5) {
                    out += "_";
                } else {
                    out += ".";
                }
            }
            out += "\n";
        }
        out = "```java\n" + out + "```"; //simple lang
        return out;
    }

    private void sendMessage(Integer id, String ourMessage, InlineKeyboardMarkup keyboard) {
        SendMessageParams body = new SendMessageParams(id, ourMessage, keyboard);
        httpDecorator.post("/sendMessage",
                body,
                value -> FLog.d(LOG_TAG, "done send message "),
                e -> FLog.e(LOG_TAG, "error " + e), Object.class);
    }

    private void editMessage(Integer id, Integer messageId, String ourMessage, InlineKeyboardMarkup keyboard) {
        SendMessageParams body = new SendMessageParams(id, messageId, ourMessage, keyboard);
        httpDecorator.post("/editMessageText",
                body,
                value -> FLog.d(LOG_TAG, "done edit message "),
                e -> FLog.e(LOG_TAG, "error " + e), Object.class);
    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.inlineKeyboard = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬆️⬆️⬆️", Buttons.TOP));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton("⬅⬅⬅️️", Buttons.LEFT));
        buttons.add(new InlineKeyboardButton("Refresh", Buttons.REFRESH));
        buttons.add(new InlineKeyboardButton("➡➡➡️", Buttons.RIGHT));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬇⬇️⬇️️", Buttons.BOT));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);
        return keyboard;
    }
}
