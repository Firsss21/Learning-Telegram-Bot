package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.entity.InfoMessage;
import ru.firsov.study.Java.Telegram.Bot.common.entity.MessageType;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Rule;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.InfoMessageRepo;

@Service
@RequiredArgsConstructor
public class RebootNotifyService implements NotifyService {

    private final UserService userService;
    private final MessageService messageService;
    private final InfoMessageRepo infoMessageRepo;

    @EventListener(ApplicationReadyEvent.class)
    public void notifyUsers() {
        userService.getUsersWithRights(Rule.ADMIN).forEach(user -> {
            sendTemplatedMessage(MessageType.REBOOTED, user);
        });
    }

    private void saveSendedMessage(MessageType type, User user) {
        InfoMessage infoMessage = new InfoMessage(user, type);
        infoMessageRepo.save(infoMessage);
        user.getMessages().add(infoMessage);
        userService.save(user);
    }
    private void sendTemplatedMessage(MessageType type, User user) {
        messageService.sendMessage(user.getChatId(), type.getMessage());
        saveSendedMessage(type, user);
    }
    private void sendMessage(User user, String message) {
        messageService.sendMessage(user.getChatId(), message);
    }
}
