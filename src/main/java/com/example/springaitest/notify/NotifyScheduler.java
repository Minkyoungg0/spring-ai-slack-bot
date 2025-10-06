package com.example.springaitest.notify;

import com.example.springaitest.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotifyScheduler {

    private final FinanceDailyService financeDailyService;
    private final SlackClient slack;

    @Scheduled(cron = "${app.notify.cron}", zone = "Asia/Seoul")
    public void sendDaily() {
        String message = financeDailyService.composeMessage();
        slack.sendText(message);
    }
}
