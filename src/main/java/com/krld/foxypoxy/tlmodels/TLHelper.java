package com.krld.foxypoxy.tlmodels;

import com.krld.foxypoxy.game.models.Buttons;
import com.krld.foxypoxy.tlmodels.buttons.InlineKeyboardButton;

import java.util.ArrayList;

public class TLHelper {
    public static InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.inlineKeyboard = new ArrayList<>();

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬆️⬆️⬆️", Buttons.TOP));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton("⬅⬅⬅️️", Buttons.LEFT));
        buttons.add(new InlineKeyboardButton("Refresh", Buttons.REFRESH));
        buttons.add(new InlineKeyboardButton("➡➡➡️", Buttons.RIGHT));
        keyboard.inlineKeyboard.add(buttons);

        buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton(" "));
        buttons.add(new InlineKeyboardButton("⬇⬇️⬇️️", Buttons.BOT));
        buttons.add(new InlineKeyboardButton(" "));
        keyboard.inlineKeyboard.add(buttons);
        return keyboard;
    }
}
