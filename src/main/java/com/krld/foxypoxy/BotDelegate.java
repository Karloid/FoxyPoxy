package com.krld.foxypoxy;

import com.krld.foxypoxy.tlmodels.Message;

public interface BotDelegate {
    void onMessage(Message message);
}
