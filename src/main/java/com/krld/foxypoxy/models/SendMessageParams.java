package com.krld.foxypoxy.models;

public class SendMessageParams {
    public Integer chatId;
    public String text;
    public InlineKeyboardMarkup replyMarkup;

    public SendMessageParams(Integer chatId, String text, InlineKeyboardMarkup replyMarkup) {
        this.chatId = chatId;
        this.text = text;
        this.replyMarkup = replyMarkup;
    }
}
