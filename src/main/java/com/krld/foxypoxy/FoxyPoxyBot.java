package com.krld.foxypoxy;

import com.krld.foxypoxy.models.Message;
import com.krld.foxypoxy.network.TLClient;

public class FoxyPoxyBot implements BotDelegate {
    private TLClient tlClient;

    @Override
    public void setClient(TLClient tlClient) {
        this.tlClient = tlClient;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println(message.from.firstName + " " + message.from.lastName + ": " + message.text);
        String ourMessage = "Hello " + message.from.firstName + " " +
                message.from.lastName + (Math.random() > 0.5f ? ". You are cool :>" : ". Sorry, you not cool");
        tlClient.sendMessage(message.chat.id, ourMessage);
    }
}
