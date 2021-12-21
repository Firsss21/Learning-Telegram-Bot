package ru.firsov.study.Java.Telegram.Bot.telegram.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.telegram.BotConfig;

@Service
public class BotConfigService implements BotConfig {

    @Value("#{${bot.name}}")
    private String username;
    @Value("#{${bot.callback}}")
    private String telegramCallbackAnswer;
    @Value("#{${bot.accessToken}}")
    private String botAccessToken;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getTelegramCallbackAnswerTemp() {
        return telegramCallbackAnswer;
    }

    @Override
    public String getBotAccessToken() {
        return botAccessToken;
    }
}
