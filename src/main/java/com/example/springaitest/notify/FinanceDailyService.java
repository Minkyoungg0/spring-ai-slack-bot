package com.example.springaitest.notify;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class FinanceDailyService {

    private final ChatClient.Builder chatClientBuilder;

    @Value("${app.prompts.finance}")
    private String financePrompt; // prompt 주입

    public String composeMessage() {
        var options = GoogleGenAiChatOptions.builder().temperature(0.8).build();

        return chatClientBuilder.build()
                .prompt(financePrompt)
                .options(options)
                .call()
                .content();
    }
}
