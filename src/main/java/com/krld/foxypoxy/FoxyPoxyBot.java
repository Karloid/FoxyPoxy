package com.krld.foxypoxy;

import com.krld.foxypoxy.models.InlineKeyboardMarkup;
import com.krld.foxypoxy.models.Message;
import com.krld.foxypoxy.models.buttons.InlineKeyboardButton;
import com.krld.foxypoxy.network.TLClient;

import java.util.ArrayList;

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
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.inlineKeyboard = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬆️⬆️⬆️"));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton("⬅⬅⬅️️"));
        buttons.add(new InlineKeyboardButton("Refresh"));
        buttons.add(new InlineKeyboardButton("➡➡➡️"));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬇⬇️⬇️️"));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);


        tlClient.sendMessage(message.chat.id, ourMessage, keyboard);
    }
}
