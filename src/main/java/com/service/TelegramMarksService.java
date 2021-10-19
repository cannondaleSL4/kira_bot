package com.service;

import com.marks.GettingMarks;
import com.telegram.ExtendsTelegramBot;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.hk2.annotations.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class TelegramMarksService {
    Long groupChatId;

    GettingMarks gettingMarks;

    ExtendsTelegramBot bot;

    // every 2 hours
    @Scheduled(fixedDelay = 7200000)
    public void sendMarks(){
        Map<String, List<String>> result =  gettingMarks.makeMarksRequest();
        StringBuilder strB = new StringBuilder("We have some updates\n");
        if (!result.isEmpty()) {
            result.entrySet().stream().forEach( e-> {
                strB.append(e.getKey() + " : " + StringUtils.join(e.getValue(), ',') + "\n");
            });
            bot.textToChat(groupChatId,strB.toString());
        }

    }
}
