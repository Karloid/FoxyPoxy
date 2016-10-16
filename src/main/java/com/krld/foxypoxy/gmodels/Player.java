package com.krld.foxypoxy.gmodels;

import java.awt.*;

public class Player {
    private Integer id;
    private Point pos;

    public Player(Integer id, Point pos) {
        this.id = id;
        this.pos = pos;
    }

    public Point getPos() {
        return pos;
    }
}
