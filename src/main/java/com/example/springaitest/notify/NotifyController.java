package com.example.springaitest.notify;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springaitest.slack.SlackClient;

@RestController
@RequiredArgsConstructor
public class NotifyController {

    private final FinanceDailyService service;
    private final SlackClient slack;

    @GetMapping("/notify/test")
    public String test() {
        String msg = service.composeMessage();
        slack.sendText(msg);
        return msg;
    }
}
