package com.krld.foxypoxy.models.buttons;


public class InlineKeyboardButton {

    public String text; ///< Label text on the button
    public String url; ///< Optional. HTTP url to be opened when button is pressed
    public String callbackData; ///< Optional. Data to be sent in a callback query to the bot when button is pressed
    public String switchInlineQuery;

    public InlineKeyboardButton() {
    }

    public InlineKeyboardButton(String text) {
        this.text = text;
        callbackData = "test";
    }
}