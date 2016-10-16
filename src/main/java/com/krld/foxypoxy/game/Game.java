package com.krld.foxypoxy.game;

import com.krld.foxypoxy.Build;
import com.krld.foxypoxy.game.models.Player;
import com.krld.foxypoxy.game.models.Symbols;
import com.krld.foxypoxy.game.models.Tile;
import com.krld.foxypoxy.tlmodels.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class Game {
    public static final int WORLD_WIDTH = 100;
    public static final int WORLD_HEIGHT = 100;

    public static final int FOV_WIDTH = 27;
    public static final int FOV_HEIGHT = 12;

    private Map<Integer, Player> players = new HashMap<>();
    private int[] groundMap;

    public Player getPlayer(Message message) {
        Integer usrId = message.from.id;
        Player player = players.get(usrId);
        if (player == null) {
            player = new Player(message, new Point(FOV_WIDTH / 2, FOV_HEIGHT / 2));
            if (Build.DEBUG) {
                players.put(-1, new Player(-1, "Debug", new Point(2, 3)));
                players.put(-2, new Player(-2, "Debug", new Point(4, 9)));
            }
            players.put(usrId, player);
            return player;
        }
        player.setMessageId(message.messageId);
        player.setChatId(message.chat.id);
        return player;

    }

    public int[] getGroundMap() {
        if (groundMap == null) {
            groundMap = new int[WORLD_WIDTH * WORLD_HEIGHT];
            for (int y = 0; y < WORLD_WIDTH; y++) {
                for (int x = 0; x < WORLD_HEIGHT; x++) {
                    double r = Math.random();
                    int tile;
                    if (r < 0.25) {
                        tile = Tile.GRASS_1;
                    } else if (r < 0.5) {
                        tile = Tile.GRASS_2;
                    } else {
                        tile = Tile.DIRT_1;
                    }
                    groundMap[y * WORLD_WIDTH + x] = tile;
                }
            }
        }
        return groundMap;
    }

    public String render(Player curPlayer) {
        String out = "";
        int[] map = getGroundMap();

        java.util.List<Player> localPlayers = new ArrayList<>(players.values());
        Point playerPos = curPlayer.getPos();
        //calculate field of view
        int wX = Math.min(Math.max(playerPos.x - FOV_WIDTH / 2, 0), WORLD_WIDTH - FOV_WIDTH / 2);//window x
        int wY = Math.min(Math.max(playerPos.y - FOV_HEIGHT / 2, 0), WORLD_HEIGHT - FOV_HEIGHT / 2);//window y

        StringBuilder sb = new StringBuilder();
        for (int y = wY; y < wY + FOV_HEIGHT; y++) {
            for (int x = wX; x < wX + FOV_WIDTH; x++) {
                if (x == playerPos.x && y == playerPos.y) {
                    sb.append(Symbols.PLAYER_CURRENT);
                    continue;
                }

                boolean continue_ = false;
                Player player = null;
                for (int i = 0; i < localPlayers.size(); i++) {
                    player = localPlayers.get(i);
                    if (x == player.getPos().x && y == player.getPos().y) {
                        sb.append(player == curPlayer ? Symbols.PLAYER_CURRENT : Symbols.PLAYER_OTHER);
                        continue_ = true;
                        break;
                    } else {
                        player = null;
                    }
                }
                if (player != null) {
                    localPlayers.remove(player);
                }

                if (continue_) {
                    continue;
                }
                int currentTile = map[y * WORLD_WIDTH + x];
                String strTile;
                switch (currentTile) {
                    case Tile.GRASS_1:
                        strTile = Symbols.GRASS_1;
                        break;
                    case Tile.GRASS_2:
                        strTile = Symbols.GRASS_2;
                        break;
                    case Tile.DIRT_1:
                        strTile = Symbols.DIRT_1;
                        break;
                    default:
                        strTile = Symbols.DIRT_1;
                }
                sb.append(strTile);
            }
            sb.append('\n');

        }
        sb.append('\n');
        for (int i = 0; i < FOV_WIDTH - 1; i++) {
            sb.append('-');
        }
        if (Math.random() > 0.5) {
            sb.append(' ');
        }
        out = "`" + sb.toString() + "`"; //simple lang
        return out;
    }

    public java.util.List<Player> getPlayersAsList() {
        return new ArrayList<>(players.values());
    }

    public Player getPlayerById(int id) {
        return players.get(id);
    }
}
