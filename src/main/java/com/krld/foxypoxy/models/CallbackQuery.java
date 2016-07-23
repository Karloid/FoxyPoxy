package com.krld.foxypoxy.models;

public class CallbackQuery {
    public static final String DATA_FIELD = "data";
    public String id; ///< Unique identifier for this query
    public User from; ///< Sender
    /**
     * Optional.
     * Message with the callback button that originated the query.
     *
     * @note The message content and message date will not be available if the message is too old
     */
    public Message message;
    public String inlineMessageId; ///< Optional. Identifier of the message sent via the bot in inline mode, that originated the query
    /**
     * Data associated with the callback button.
     * @note Be aware that a bad client can send arbitrary data in this field
     */
    public String data;
}
