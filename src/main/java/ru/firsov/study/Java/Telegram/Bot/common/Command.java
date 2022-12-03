package ru.firsov.study.Java.Telegram.Bot.common;

import lombok.Value;

import java.util.Locale;

public enum Command {
    START("начать"),
    HELP("список команд"),
    CANCEL("отмена"),
    INFO("информация"),
    DONATE("поддержать проект")
    ;


    private final String description;

    public final String getDescription() {
        return "/" + this.name().toLowerCase(Locale.ROOT) + " - " + description;
    }

    Command(String description) {
        this.description = description;
    }
}
