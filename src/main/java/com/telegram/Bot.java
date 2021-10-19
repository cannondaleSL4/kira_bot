package com.telegram;

import com.Utils;
import com.telegram.commands.*;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Map;

/**
 * Bot
 */
public final class Bot extends ExtendsTelegramBot {

    private Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    @Getter
    private static final Settings defaultSettings = new Settings(1, 15,1);
    private final NonCommand nonCommand;

    /**
     *  Settings of different user. Key - unique id.
     */
    @Getter
    private static Map<Long, Settings> userSettings;

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        logger.debug("name and token exist.");

        this.nonCommand = new NonCommand();
        logger.debug("Class processing of the message, not command.");

        register(new StartCommand("start", "Start"));
        logger.debug("Command start created");

        register(new HelpCommand("help","Help"));
        logger.debug("Command help created");

        register(new SettingsCommand("settings", "My settings."));
        logger.debug("Command settings created");

//        register(new MultiplicationCommand("multiply", "Умножение"));
//        logger.debug("Команда multiply создана");

        userSettings = new HashMap<>();
        logger.info("Bot created!");
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = Utils.getUserName(msg);

        String answer = nonCommand.nonCommandExecute(chatId, userName, msg.getText());
        setAnswer(chatId, userName, answer);
    }

    public void textToChat(Long chatId, String text){
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error("error: " + e);
            e.printStackTrace();
        }
    }

    public static Settings getUserSettings(Long chatId) {
        Map<Long, Settings> userSettings = Bot.getUserSettings();
        Settings settings = userSettings.get(chatId);
        if (settings == null) {
            return defaultSettings;
        }
        return settings;
    }

    /**
     * Sending answer
     * @param chatId id chat
     * @param userName user name
     * @param text answer text
     */
    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Error %s. Message - not command. User: %s", e.getMessage(),
                    userName));
            e.printStackTrace();
        }
    }
}