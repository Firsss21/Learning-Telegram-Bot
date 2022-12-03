package ru.firsov.study.Java.Telegram.Bot.common.state;

import lombok.Value;

@Value
public class FullyLearnedToday implements LearnedToday {

    int limit;
    int questionsSolved;
    int questionsCountTotal;

    @Override
    public String getDescription() {
        return "Вы прошли все " + limit + " вопросов за сегодня! Всего пройдено вопросов: " + questionsSolved + "/" + questionsCountTotal;
    }
}
