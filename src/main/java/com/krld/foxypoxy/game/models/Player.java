package com.krld.foxypoxy.game.models;

import com.krld.foxypoxy.tlmodels.User;
import com.krld.foxypoxy.util.TextUtils;

import java.awt.*;

public class Player {

    private int id;
    private String name;
    private Point pos;

    public Player(User user, Point pos) {
        this.id = user.id;
        if (!TextUtils.isEmpty(user.firstName)) {
            this.name = user.firstName;
            if (!TextUtils.isEmpty(user.lastName)) {
                name = name + " " + user.lastName;
            }
        } else if (!TextUtils.isEmpty(user.username)) {
            name = user.username;
        } else {
            name = "Unknown";
        }
        this.pos = pos;
    }

    public Player(Integer id, String name, Point pos) {
        this.id = id;
        this.name = name;
        this.pos = pos;
    }

    public Point getPos() {
        return pos;
    }
}
