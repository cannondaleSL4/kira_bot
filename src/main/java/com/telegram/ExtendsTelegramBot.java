package com.telegram;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;

public  abstract class ExtendsTelegramBot extends TelegramLongPollingCommandBot {

    public abstract void textToChat(Long groupChatId, String text);
}
