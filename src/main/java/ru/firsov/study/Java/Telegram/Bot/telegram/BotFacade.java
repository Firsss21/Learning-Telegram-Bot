package ru.firsov.study.Java.Telegram.Bot.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface BotFacade {
    void handleUpdate(Update update) throws IOException, InterruptedException;
}
