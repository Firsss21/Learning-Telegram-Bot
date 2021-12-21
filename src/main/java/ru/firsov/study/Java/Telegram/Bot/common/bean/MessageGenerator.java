package ru.firsov.study.Java.Telegram.Bot.common.bean;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.Command;

@Service
public class MessageGenerator {

    public String generateStartMessage(String name) {
        return "Привет " + name + "! " +
                "С помощью этого бота ты можешь подтянуть и проверить свои знания о программировании, подготовиться к собеседованию или узнать что-то новое. Все разложено по темам - специально для тебя! :sweat_smile:";
    }

    public String generateInfoMessage(){
        return "*Вопрос:* Как пользоваться этим ботом? \n" +
                "*Ответ:* Нужно перейти во вкладку меню \"Тестирование\" или \"Изучение\", после чего нужно выбрать главу и после тему. \n" +
                "\n*Вопрос:* Кому сообщить о баге, вопросе, предложении или прочему?\n" +
                "*Ответ:* Написать @grishaF";
    }

    public String generateHelpMessage() {
        String message =  ":sunny: Вот мои доступные команды :sunny:\n\n";

        for (Command value : Command.values()) {
            message = message + value.getDescription() + "\n";
        }
        return EmojiParser.parseToUnicode(message);
    }

    public String generateSuccessCancel() {
        return EmojiParser.parseToUnicode(":white_check_mark: Активная команда успешно отклонена");
    }

}
