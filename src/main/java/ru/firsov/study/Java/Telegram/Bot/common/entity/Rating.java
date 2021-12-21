package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.Value;

@Value
public class Rating {
    long id;
    String name;
    int place;
    int rating;
}
