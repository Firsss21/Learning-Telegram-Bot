package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageType {
    AWAY_3DAY(
            "Привет! Это я, твой бот-помощник, ты не забыл про меня? Не видел тебя уже 3 дня, может все-таки подкачаешь свои навыки?"
    ),
    AWAY_7DAY(
            "Эй, ну ты как там? 🥺\nНе видел тебя уже 7 дней, а еще столько не изученного!"
    ),
    AWAY_30DAY(
            "Привет.. Целый месяц тебя уже не видно, все, забил? Думал может все-таки поизучаем что-нибудь :point_right: :point_left:"
    ),
    ;

    @Getter
    String message;

}

