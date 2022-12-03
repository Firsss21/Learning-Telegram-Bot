package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.entity.QuestionsCounter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import java.util.List;

import static ru.firsov.study.Java.Telegram.Bot.common.entity.MessageType.*;

@Service
@AllArgsConstructor
@Transactional
@EnableScheduling
public class QuestionRemainingService implements NotifyService {

    private final MessageService messageService;

    private UserService userService;
    @Scheduled(cron = "0 18 * * 1-5")
    public void notifyUsers() {
        List<User> users = userService.getUsersWithCounterActive();
        String message = "Привет! Сегодня у тебя еще не пройдено [q] вопросов";
        for (User user : users) {
            QuestionsCounter counter = user.getQuestionsCounter();
            if (counter.getSolved() < counter.getLimit()) {
                messageService.sendMessage(user.getChatId(), message.replace("[q]", String.valueOf(counter.getLimit() - counter.getSolved())));
            }
        }
    }
}
