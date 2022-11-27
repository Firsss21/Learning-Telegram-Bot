package ru.firsov.study.Java.Telegram.Bot.common.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.firsov.study.Java.Telegram.Bot.telegram.TelegramBot;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @SneakyThrows
    public void sendMessage(Update update, InputFile file) {
        SendDocument.SendDocumentBuilder builder = SendDocument.builder().chatId(getChatId(update)).caption("kek").document(file);
        telegramBot.execute(builder.build());
    }

//    public String getFilePath(InputFile file) {
//        Objects.requireNonNull(photo);
//
//        if (photo.getFilePath().hasFilePath()) { // If the file_path is already present, we are done!
//            return photo.getFilePath();
//        } else { // If not, let find it
//            // We create a GetFile method and set the file_id from the photo
//            GetFile getFileMethod = new GetFile();
//            getFileMethod.setFileId(photo.getFileId());
//            try {
//                // We execute the method using AbsSender::execute method.
//                File file = execute(getFileMethod);
//                // We now have the file_path
//                return file.getFilePath();
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null; // Just in case
//    }

    public java.io.File downloadFile(String filePath) {
        try {
            return telegramBot.downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
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

    @SneakyThrows
    private File getFile(Update update) {
        if (update.getMessage().getDocument() == null) return null;

        Document document = update.getMessage().getDocument();
        GetFile uploadedFile = new GetFile(document.getFileId());
        System.out.println(uploadedFile.getMethod());
        org.telegram.telegrambots.meta.api.objects.File file1 = uploadedFile.deserializeResponse("123");
        System.out.println(file1);
//        String uploadedFilePath = getFile(uploadedFile).getFilePath();
//        File file = new File();

//        InputStream is = new URL("https://api.telegram.org/file/bot"+ telegramBot.getBotToken() +"/"+ uploadedFilePath).openStream();
//        FileUtils.copyInputStreamToFile(is, localFile);
        return null;
    }

    private String getStringDataFromFile(Update update) {
        File file = getFile(update);
        if (file.canRead()) {

        }

        return null;
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

    private Long getChatId(Update update) {
        Long chatId = null;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        return chatId;
    }

    @SneakyThrows
    public String getDataFromDocument(Document document) {
        GetFile getFile = new GetFile(document.getFileId());
        org.telegram.telegrambots.meta.api.objects.File execute = telegramBot.execute(getFile);
        File file = this.downloadFile(execute.getFilePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
