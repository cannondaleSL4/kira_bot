package com.telegram;

import com.marks.GettingMarks;
import com.service.TelegramMarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableScheduling
public class TgBotConfig {

    @Value("${TELEBOT_BOT_TOKEN}")
    private String token;

    @Value("${USERNAME}")
    private String username;

    @Value("${PASSWORD}")
    private String password;

    @Value("${MARKS_CHAT_ID}")
    Long groupChatId;

    @Bean
    public GettingMarks gettingMarks() {
        return new GettingMarks(username, password);
    }

    @Bean
    public ExtendsTelegramBot getExtendsTelegramBot(){
        return new Bot("Kira_Bot", token);
    }

    @Bean
    public TelegramBotsApi getTgBot(@Autowired ExtendsTelegramBot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }

    @Bean
    public TelegramMarksService getTelegramMarksService(@Autowired ExtendsTelegramBot bot, @Autowired GettingMarks gettingMarks){
        return new TelegramMarksService(groupChatId, gettingMarks, bot);
    }

    // every 2 hours
//    @Scheduled(fixedDelay = 7200000)
//    public void scheduleFixedDelayTask() {
//        gettingMarks.initCoockies();
//    }
}
