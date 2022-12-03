package ru.firsov.study.Java.Telegram.Bot.common.state;

import lombok.Value;

@Value
public class RemainingSomethingLearnedToday implements LearnedToday {

    int limit;
    int learned;

    @Override
    public String getDescription() {
        return "Вы прошли " + learned + " вопросов! Осталось всего " + (limit - learned) + "!";
    }
}
