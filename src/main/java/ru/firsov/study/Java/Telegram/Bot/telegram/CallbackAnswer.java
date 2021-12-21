package ru.firsov.study.Java.Telegram.Bot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CallbackAnswer {
    @Autowired
    private BotConfig botConfig;

    public void callbackAnswer(String callbackId) throws IOException, InterruptedException {
        HttpClient telegramApiClient = HttpClient.newHttpClient();
        HttpRequest telegramCallbackAnswerReq =
                HttpRequest.newBuilder(URI
                        .create(botConfig
                                .getTelegramCallbackAnswerTemp()
                                .replace("{token}", botConfig.getBotAccessToken())
                                .replace("{id}", callbackId)))
                        .GET().build();

        telegramApiClient.send(telegramCallbackAnswerReq, HttpResponse.BodyHandlers.ofString());
    }
}