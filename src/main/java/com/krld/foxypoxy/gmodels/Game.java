package com.krld.foxypoxy.gmodels;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Game {
    public static final int FIELD_WIDTH = 28;
    public static final int FIELD_HEIGHT = 12;

    private Map<Integer, Player> players = new HashMap<>();

    public Player getPlayer(Integer id) {
        Player player = players.get(id);
        if (player == null) {
            player = new Player(id, new Point(FIELD_WIDTH / 2, FIELD_HEIGHT / 2));
            players.put(id, player);
        }
        return player;
    }
}
