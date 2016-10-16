package com.krld.foxypoxy;

import com.google.gson.Gson;
import com.krld.foxypoxy.game.Game;
import com.krld.foxypoxy.game.models.Buttons;
import com.krld.foxypoxy.game.models.Player;
import com.krld.foxypoxy.network.HttpDecorator;
import com.krld.foxypoxy.responses.ResponseGetUpdates;
import com.krld.foxypoxy.tlmodels.*;
import com.krld.foxypoxy.util.Addresses;
import com.krld.foxypoxy.util.FLog;
import com.krld.foxypoxy.util.JsonUtils;
import com.krld.foxypoxy.util.VertxUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;

import java.awt.*;
import java.util.List;

import static com.krld.foxypoxy.game.Game.*;
import static com.krld.foxypoxy.tlmodels.TLHelper.getKeyboard;

@SuppressWarnings("ForLoopReplaceableByForEach")
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

        if (Build.DEBUG) {
            vertx.setPeriodic(700, event -> {
                Player player = game.getPlayerById(Math.random() > 0.5 ? -1 : -2);
                if (player == null) {
                    return;
                }
                double r = Math.random();
                if (r < 0.25) {
                    translate(player, 1, 0);
                } else if (r < 0.5) {
                    translate(player, 0, 1);
                } else if (r < 0.75) {
                    translate(player, -1, 0);
                } else {
                    translate(player, 0, -1);
                }
                broadcastRender();
            });
        }
    }

    private void translate(Player player, int x, int y) {
        Point pos = player.getPos();
        int wX = Math.min(Math.max(pos.x + x, 0), WORLD_WIDTH - 1);
        int wY = Math.min(Math.max(pos.y + y, 0), WORLD_HEIGHT - 1);
        pos.setLocation(wX, wY);
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
        Player player = game.getPlayer(message);
        sendMessage(message.chat.id, game.render(player), getKeyboard());
    }

    private void onCallbackQuery(CallbackQuery query) {
        if (query == null) {
            return;
        }
        FLog.d(LOG_TAG, "onCallbackQuery");
        Player curPlayer = game.getPlayer(query.message);
        if (handleUserQuery(query, curPlayer)) return;

        broadcastRender();
    }

    private void broadcastRender() {
        List<Player> players = game.getPlayersAsList();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getId() < 0) {
                continue;
            }
            editMessage(player.getChatId(), player.getMessageId(), game.render(player), getKeyboard());
        }
    }

    private boolean handleUserQuery(CallbackQuery query, Player player) {
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
                return true;
        }
        player.getPos().setLocation(x, y);
        return false;
    }

    private void sendMessage(Integer id, String ourMessage, InlineKeyboardMarkup keyboard) {
        SendMessageParams body = new SendMessageParams(id, ourMessage, keyboard);
        httpDecorator.post("/sendMessage",
                body,
                value -> FLog.d(LOG_TAG, "done send message "),
                e -> FLog.e(LOG_TAG, "error " + e), Object.class);
    }

    private void editMessage(Integer chatId, Integer messageId, String ourMessage, InlineKeyboardMarkup keyboard) {
        SendMessageParams body = new SendMessageParams(chatId, messageId, ourMessage, keyboard);
        httpDecorator.post("/editMessageText",
                body,
                value -> FLog.d(LOG_TAG, "done edit message "),
                e -> FLog.e(LOG_TAG, "error " + e), Object.class);
    }
}
