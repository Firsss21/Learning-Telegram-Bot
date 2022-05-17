package ru.firsov.study.Java.Telegram.Bot.common.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.firsov.study.Java.Telegram.Bot.telegram.TelegramBot;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Service
public class MessageService {

    @Value("#{${message.maxLength}}")
    private int msgMaxLength;

    public MessageService(KeyboardService keyboardService, UserService userService) {
        this.keyboardService = keyboardService;
        this.userService = userService;
    }

    private final KeyboardService keyboardService;
    private final UserService userService;

    private TelegramBot telegramBot;

    @Autowired
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Отправляет сообщение выбранному chatId пользователю (т.е. любому).
     * @param chatId
     * @param messageText
     */
    public void sendMessage(Long chatId, String messageText) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        String msg = EmojiParser.parseToUnicode(messageText);
        messageBuilder.chatId(String.valueOf(chatId));
        messageBuilder.parseMode("Markdown");
        messageBuilder.replyMarkup(keyboardService.getKeyBoardByState(chatId));

        int msgCount = (int) Math.ceil(msg.length() / (float)msgMaxLength);

        for (int i = 0; i < msgCount; i++) {
            int from = i * msgMaxLength;
            int to = Math.min((i + 1) * msgMaxLength, msg.length());
            messageBuilder.text(msg.substring(from, to));
            try {
                telegramBot.execute(messageBuilder.build());
            } catch (TelegramApiException telegramApiException) {
                try {
                    messageBuilder.parseMode("");
                    telegramBot.execute(messageBuilder.build());
                } catch (TelegramApiException e) {
                    System.out.println("Message length: " + msg.length() + "\n" +
                            "Message text:\n" + msg);
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessageToAll(String messageText) {
        List<Long> allChatIds = userService.getAllChatIds();
        for (Long chatId : allChatIds) {
            sendMessage(chatId, messageText);
        }
    }

    public void sendMessageWOParse(Update update, String msg) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        Long chatId = setChatIdToMessageBuilder(update, messageBuilder);

        int msgCount = (int) Math.ceil(msg.length() / (float)msgMaxLength);

        for (int i = 0; i < msgCount; i++) {
            int from = i * msgMaxLength;
            int to = Math.min((i + 1) * msgMaxLength, msg.length());
            messageBuilder.text(msg.substring(from, to));
            try {
                telegramBot.execute(messageBuilder.build());
            } catch (TelegramApiException telegramApiException) {
                try {
                    messageBuilder.parseMode("");
                    telegramBot.execute(messageBuilder.build());
                } catch (TelegramApiException e) {
                    System.out.println("Message length: " + msg.length() + "\n" +
                            "Message text:\n" + msg);
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(Update update, String messageText) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        Long chatId = setChatIdToMessageBuilder(update, messageBuilder);
        String msg = EmojiParser.parseToUnicode(messageText);
        messageBuilder.parseMode("Markdown");
        messageBuilder.replyMarkup(keyboardService.getKeyBoardByState(chatId));
        parsePhoto(update, messageText);
        int msgCount = (int) Math.ceil(msg.length() / (float)msgMaxLength);
        for (int i = 0; i < msgCount; i++) {
            int from = i * msgMaxLength;
            int to = Math.min((i + 1) * msgMaxLength, msg.length());
            messageBuilder.text(msg.substring(from, to));
            try {
                telegramBot.execute(messageBuilder.build());
            } catch (TelegramApiException telegramApiException) {
                try {
                    messageBuilder.parseMode("");
                    telegramBot.execute(messageBuilder.build());
                } catch (TelegramApiException e) {
                    System.out.println("Message length: " + msg.length() + "\n" +
                            "Message text:\n" + msg);
                    e.printStackTrace();
                }
            }
        }
    }

    private void parsePhoto(Update update, String messageText) {
//        if (messageText)
//        SendPhoto.SendPhotoBuilder sendPhotoBuilder = SendPhoto.builder();
//        sendPhotoBuilder.photo(new InputFile(new File()))
    }

    private String getMessage(Update update) {
        if (update.hasMessage() && update.getMessage().getText() != null) {
            return update.getMessage().getText();
        } else if (update.hasChannelPost() && update.getChannelPost().getText() != null) {
            return update.getChannelPost().getText();
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            return update.getCallbackQuery().getData();
        }
        return "";
    }

    private Long setChatIdToMessageBuilder(Update update, SendMessage.SendMessageBuilder messageBuilder) {
        Long chatId = null;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            messageBuilder.chatId(update.getMessage().getChatId().toString());
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            messageBuilder.chatId(update.getChannelPost().getChatId().toString());
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageBuilder.chatId(update.getCallbackQuery().getMessage().getChatId().toString());
        }
        return chatId;
    }
}
