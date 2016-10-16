package com.krld.foxypoxy.tlmodels.buttons;


public class InlineKeyboardButton {

    public String text; ///< Label text on the button
    public String url; ///< Optional. HTTP url to be opened when button is pressed
    public String callbackData; ///< Optional. Data to be sent in a callback query to the bot when button is pressed
    public String switchInlineQuery;

    public InlineKeyboardButton(String text, String callbackData) {
        this.text = text;
        this.callbackData = callbackData;
    }

    public InlineKeyboardButton(String text) {
        this.text = text;
        callbackData = "_";
    }
}