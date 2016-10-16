package com.krld.foxypoxy.tlmodels;

public class SendMessageParams {
    public Integer messageId;
    public Integer chatId;
    public String text;
    public InlineKeyboardMarkup replyMarkup;
    public String parseMode = "Markdown";//HTML

    public SendMessageParams(Integer chatId, String text, InlineKeyboardMarkup replyMarkup) {
        this.chatId = chatId;
        this.text = text;
        this.replyMarkup = replyMarkup;
    }

    public SendMessageParams(Integer chatId, Integer messageId, String text, InlineKeyboardMarkup replyMarkup) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.text = text;
        this.replyMarkup = replyMarkup;
    }
}
