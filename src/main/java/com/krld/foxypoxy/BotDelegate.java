package com.krld.foxypoxy;

import com.krld.foxypoxy.models.Message;

public interface BotDelegate {
    void onMessage(Message message);
}
