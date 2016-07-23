package com.krld.foxypoxy;

import com.krld.foxypoxy.models.Message;
import com.krld.foxypoxy.network.TLClient;

public interface BotDelegate {
    void setClient(TLClient tlClient);

    void onMessage(Message message);
}
