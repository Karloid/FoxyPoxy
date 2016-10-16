package com.krld.foxypoxy.game.models;

import com.krld.foxypoxy.tlmodels.Message;
import com.krld.foxypoxy.util.TextUtils;

import java.awt.*;

public class Player {

    private int chatId;
    private int messageId;
    private int id;
    private String name;
    private Point pos;

    public Player(Message msg, Point pos) {
        this.id = msg.from.id;
        if (!TextUtils.isEmpty(msg.from.firstName)) {
            this.name = msg.from.firstName;
            if (!TextUtils.isEmpty(msg.from.lastName)) {
                name = name + " " + msg.from.lastName;
            }
        } else if (!TextUtils.isEmpty(msg.from.username)) {
            name = msg.from.username;
        } else {
            name = "Unknown";
        }
        messageId = msg.messageId;
        chatId = msg.chat.id;
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

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public int getId() {
        return id;
    }

    public int getChatId() {
        return chatId;
    }

    public int getMessageId() {
        return messageId;
    }
}
