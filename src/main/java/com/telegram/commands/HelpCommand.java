package com.telegram.commands;

import com.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Command "Help"
 */
public class HelpCommand extends ServiceCommand {
    private Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = Utils.getUserName(user);

        logger.info(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "I'am bot. My duty is checking updates in Kira's  school marks every 2 hours and send updates to telegram chat.\n\n" +
                        "❗*Список команд*\n" +
                        "/plus - сложение\n" +
                        "/minus - вычитание\n" +
                        "/plusminus - сложение и вычитание\n" +
                        "/multiply - умножение\n" +
                        "/divide - деление\n" +
                        "/multdivide - умножение и деление\n" +
                        "/all - все четыре арифметических действия\n" +
                        "/settings - settings\n" +
                        "/help - help\n \uD83D\uDE42");
        logger.info(String.format("User %s. Ended execution of command %s", userName,
                this.getCommandIdentifier()));
    }
}