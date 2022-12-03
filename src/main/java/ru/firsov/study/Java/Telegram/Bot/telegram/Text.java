package ru.firsov.study.Java.Telegram.Bot.telegram;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

public enum Text {
    KNOW_BTN(":white_check_mark: Знаю"),
    DONT_KNOW_BTN(":x: Не знаю"),
    BACK_BTN(":arrow_left: Назад"),
    NEXT_BTN(":arrow_right: Следующий"),
    LEARN_BTN(":mag: Изучение"),
    TEST_BTN(":pencil2: Тестирование"),
    STATS_BTN(":chart_with_downwards_trend: Статистика"),
    RESET_STATS_BTN(":recycle: Сбросить статистику"),
    INFO_BTN(":sound: Инфо"),
    DONATE_BTN(":dollar: Поддержать проект"),
    CANCEL_BTN(":leftwards_arrow_with_hook: Отменить"),
    ADM_MSG_TO_ALL_BTN(":speaker: Отправить всем сообщение"),
    ADM_STATS_BTN(":chart_with_downwards_trend: Общая статистика"),
    ADM_EDIT(":pencil2: Редактировать"),
    ADM_DELETE(":o: Удалить"),
    ADM_ENTER(":name_badge: Админка"),
    ADM_EDIT_A("Редактировать ответ"),
    ADM_EDIT_Q("Редактировать вопрос"),
    ADM_CACHE_EVICT("Сбросить кеш"),
    ADM_ADD_QUESTION(":memo: Добавить вопрос"),
    ADM_ADD_QUESTIONS(":memo: Добавить несколько вопросов"),
    ADM_ADD_CHAPTER("Добавить главу"),
    SAVE_BTN("Сохранить"),
    SAVE_AND_CONTINUE_BTN("Сохранить и продолжить"),
    SET_COUNTER_BTN(":white_check_mark: Поставить цель"),
    SETTINGS_BTN(":wrench: Настройки"),

    ;


    private final String text;

    public String getTextWOEmoji(){
        return text;
    }

    public String getText() {
        return EmojiParser.parseToUnicode(text);
    }

    Text(String text) {
        this.text = text;
    }
}
