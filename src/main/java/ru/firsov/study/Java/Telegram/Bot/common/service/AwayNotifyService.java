package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.entity.InfoMessage;
import ru.firsov.study.Java.Telegram.Bot.common.entity.MessageType;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.InfoMessageRepo;

import java.util.List;

import static ru.firsov.study.Java.Telegram.Bot.common.entity.MessageType.*;

@Service
@AllArgsConstructor
@Transactional
@EnableScheduling
public class AwayNotifyService implements NotifyService {

    private final UserService userService;
    private final MessageService messageService;
    private final InfoMessageRepo infoMessageRepo;

    @Scheduled(fixedDelayString = "${user.notifyCheck}")
    public void notifyUsers() {
        sendToAway(60L * 60 * 1000 * 24 * 3, AWAY_3DAY);
        sendToAway(60L * 60 * 1000 * 24 * 7, AWAY_7DAY);
        sendToAway(60L * 60 * 1000 * 24 * 30, AWAY_30DAY);

    }

    public void sendToAway(long timeAway, MessageType type) {
        long awayFrom = System.currentTimeMillis() - timeAway;
        List<User> notActiveUsers = userService.getUsersNotActiveFrom(awayFrom);
        for (User notActiveUser : notActiveUsers) {
            List<InfoMessage> messages = notActiveUser.getMessages();
            if (messages.stream().noneMatch(e -> e.getMessageType().equals(type)) ||
                    messages.stream().filter(e -> e.getMessageType().equals(type)).allMatch(e -> e.getDate().toInstant().toEpochMilli() < notActiveUser.getLastActivity())
            ) {
                sendTemplatedMessage(type, notActiveUser);
            }
        }
    }

    private void sendTemplatedMessage(MessageType type, User user) {
        messageService.sendMessage(user.getChatId(), type.getMessage());
        InfoMessage infoMessage = new InfoMessage(user, type);
        infoMessageRepo.save(infoMessage);
        user.getMessages().add(infoMessage);
        userService.save(user);
    }

}
