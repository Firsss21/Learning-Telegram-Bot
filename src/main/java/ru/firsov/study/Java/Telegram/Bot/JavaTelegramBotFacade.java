package ru.firsov.study.Java.Telegram.Bot;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;
import ru.firsov.study.Java.Telegram.Bot.common.Command;
import ru.firsov.study.Java.Telegram.Bot.common.bean.MessageGenerator;
import ru.firsov.study.Java.Telegram.Bot.common.entity.*;
import ru.firsov.study.Java.Telegram.Bot.common.service.*;
import ru.firsov.study.Java.Telegram.Bot.common.state.LearnedToday;
import ru.firsov.study.Java.Telegram.Bot.telegram.BotConfig;
import ru.firsov.study.Java.Telegram.Bot.telegram.BotFacade;
import ru.firsov.study.Java.Telegram.Bot.telegram.CallbackAnswer;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.firsov.study.Java.Telegram.Bot.common.BotState.*;
import static ru.firsov.study.Java.Telegram.Bot.common.Command.*;
import static ru.firsov.study.Java.Telegram.Bot.telegram.Text.*;

@Service
public class JavaTelegramBotFacade implements BotFacade {

    private final MessageGenerator messageGenerator;
    private final UserService userService;
    private final CallbackAnswer callbackAnswer;
    private final QuestionService questionService;
    private final QuestionAdderService questionAdderService;
    private final StatisticService statisticService;
    private final QuestionsLearnedTodayCounterService questionsLearnedTodayCounterService;
    private MessageService messageService;

    public JavaTelegramBotFacade(MessageGenerator messageGenerator, UserService userService, CallbackAnswer callbackAnswer, QuestionService questionService, StatisticService statisticService, QuestionAdderService questionAdderService, QuestionsLearnedTodayCounterService questionsLearnedTodayCounterService) {
        this.messageGenerator = messageGenerator;
        this.userService = userService;
        this.callbackAnswer = callbackAnswer;
        this.questionService = questionService;
        this.statisticService = statisticService;
        this.questionAdderService = questionAdderService;
        this.questionsLearnedTodayCounterService = questionsLearnedTodayCounterService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Value("#{${admin.chatId}}")
    private long adminId;

    @Override
    public void handleUpdate(Update update) throws IOException, InterruptedException {
        String messageText;
        Long chatId;
        String userFirstName = "";

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
//            messageText = update.getMessage().getText() == null ? "" : update.getMessage().getText().replace("/", "");
            messageText = update.getMessage().getText() == null ? "" : update.getMessage().getText();
            userFirstName = update.getMessage().getChat().getFirstName();
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
//            messageText = update.getChannelPost().getText() == null ? "" : update.getChannelPost().getText().replace("/", "");
            messageText = update.getChannelPost().getText() == null ? "" : update.getChannelPost().getText();

            userFirstName = update.getChannelPost().getChat().getFirstName();
        } else if (update.hasCallbackQuery()) {
            callbackAnswer.callbackAnswer(update.getCallbackQuery().getId());

            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageText = update.getCallbackQuery().getData() == null ? "" : update.getCallbackQuery().getData();
            sendMessage(update, update.getCallbackQuery().getData());

        } else if (update.hasMyChatMember()) {
            if (update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
                userService.deleteChat(update.getMyChatMember().getChat().getId());
            }

            return;
        } else {

            return;
        }

        if (userService.isChatInitted(chatId)) {
            try {
                if (!handleCommand(messageText, update, chatId))
                    handleBotState(update, chatId, messageText);
            } catch (Exception e) {
                e.printStackTrace();
                User user = userService.getUser(chatId);
                user.setBotState(DEFAULT);
                userService.save(user);
                sendMessage(update, "Произошла ошибка. Вы вернулись на главную");
            }
        } else {
            userService.initChat(chatId, userFirstName);
            sendMessage(update, messageGenerator.generateStartMessage(userFirstName));
        }
    }

    /**
     * Отправляет ответное сообщение пользователю
     *
     * @param update
     * @param messageText
     */
    private void sendMessage(Update update, String messageText) {
        messageService.sendMessage(update, messageText);
    }

    private void sendMessage(Update update, InputFile file) {
        messageService.sendMessage(update, file);
    }

    private void downloadFile(String filepath) {
        File file = messageService.downloadFile(filepath);
    }

    private boolean handleCommand(String messageText, Update update, Long chatId) {
        User user = userService.getUser(chatId);
        userService.processUpdateUser(user);
        if (messageText.startsWith("/")) {
            messageText = messageText.replace("/", "");
        }
        if (messageText.toUpperCase(Locale.ROOT).equals(HELP.name())) {
            sendMessage(update, messageGenerator.generateHelpMessage());
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(START.name())) {
            userService.setBotState(chatId, DEFAULT);
            sendMessage(update, messageGenerator.generateStartMessage(update.getMessage().getChat().getFirstName()));
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(Command.CANCEL.name()) || messageText.equals(CANCEL_BTN.getText())) {
            if (user.getBotState() == DEFAULT) {
                sendMessage(update, "Нет активной команды для отклонения");
            } else {
                userService.setBotState(chatId, DEFAULT);
                sendMessage(update, messageGenerator.generateSuccessCancel());
            }
            return true;
        }

        if (messageText.equals(INFO_BTN.getText()) || messageText.toUpperCase(Locale.ROOT).equals(INFO.name())) {
            sendMessage(update, messageGenerator.generateInfoMessage());
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(DONATE.name()) || messageText.equals(DONATE_BTN.getText())) {
            sendMessage(update, "донат");
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals("ADMIN") || messageText.equals(ADM_ENTER.getText())) {
            if (!user.isAdmin()) {
                String token = UUID.randomUUID().toString();
                messageService.sendMessage(adminId, ":key: Токен для авторизации администратора: \"" + token + "\"");
                user.setBotState(ADMIN_LOGIN);
                user.setBotStateVariable(token);
                userService.save(user);
                sendMessage(update, ":key: Введите код");
                return true;
            } else {
                user.setBotState(ADMIN_PAGE);
                userService.save(user);
                sendMessage(update, ":atom_symbol: Выберите пункт меню");
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    private void handleBotState(Update update, Long chatId, String messageText) {
        User user = userService.getUser(chatId);

        switch (user.getBotState()) {
            case ADMIN_LOGIN: {
                if (handleBackButton(user, messageText, update, DEFAULT))
                    break;
                if (messageText.equals(user.getBotStateVariable())) {
                    user.getRights().add(Rule.ADMIN);
                    user.setBotState(ADMIN_PAGE);
                    userService.save(user);
                    sendMessage(update, "Вы авторизованы как администратор");
                } else {
                    sendMessage(update, "Код неверный");
                }
                break;
            }
            case ADMIN_WAIT_MSG: {
                if (handleBackButton(user, messageText, update, ADMIN_PAGE))
                    break;
                messageService.sendMessageToAll(messageText);
                break;
            }
            case ADMIN_PAGE: {
                if (handleBackButton(user, messageText, update, DEFAULT))
                    break;
                if (messageText.equals(ADM_STATS_BTN.getText())) {
                    sendMessage(update, statisticService.buildStatsMessage());
                    break;
                }
                if (messageText.equals(ADM_MSG_TO_ALL_BTN.getText())) {
                    user.setBotState(ADMIN_WAIT_MSG);
                    userService.save(user);
                    sendMessage(update, ":pencil2: Введите сообщение, которое хотите отправить всем пользователям ");
                    break;
                }
                if (messageText.equals(ADM_CACHE_EVICT.getText())){
                    questionService.evictAllQuestions();
                    sendMessage(update, "Кеш сброшен");
                    break;
                }
                if (messageText.equals(ADM_ADD_QUESTION.getText()) || messageText.equals(ADM_ADD_QUESTIONS.getText())){
                    user.setBotState(ADMIN_ADD_QUESTION_SELECT_PART);
                    user.setBotStateVariable(messageText.equals(ADM_ADD_QUESTIONS.getText()) ? "MANY" : "SINGLE");
                    userService.save(user);
                    sendMessage(update, "Выберите главу");
                    break;
                }
                if (messageText.equals(ADM_ADD_CHAPTER.getText())){
                    user.setBotState(ADMIN_ADD_CHAPTER_SELECT_PART);
                    userService.save(user);
                    sendMessage(update, "Выберите главу");
                    break;
                }
            }
            case DEFAULT: {
                if (messageText.equals(LEARN_BTN.getText()) || messageText.equals(TEST_BTN.getText())) {
                    user.setBotState(SELECTING_PART);
                    if (messageText.equals(TEST_BTN.getText()))
                        user.setBotStateVariable(TESTING.name());
                    else
                        user.setBotStateVariable(LEARNING.name());
                    userService.save(user);
                    sendMessage(update, "Выберите главу");
                }
                if (messageText.equals(SETTINGS_BTN.getText()) || messageText.equals(SETTINGS_BTN.getText())) {
                    user.setBotState(SETTINGS);
                    userService.save(user);
                    sendMessage(update, "Ваши настройки:");
                }

                break;
            }
            case SELECTING_PART: {
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(DEFAULT);
                    userService.save(user);
                    sendMessage(update, "Вы вернулись к выбору главы");
                    break;
                }
                String partName = messageText.substring(0, messageText.lastIndexOf("(")).trim();
                Part partByPartName = questionService.findPartByPartName(partName);
                if (partByPartName != null) {
                    user.setBotState(SELECTING_CHAPTER);
                    user.setSelectedPartId(partByPartName.getId());
                    userService.save(user);
                    sendMessage(update, "Выберите тему:");
                } else {
                    sendMessage(update, "Главы с таким названием не найдено");
                }
                break;
            }
            case SELECTING_CHAPTER: {
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(SELECTING_PART);
                    userService.save(user);
                    sendMessage(update, "Вы вернулись к выбору главы");
                    break;
                }
                String chapterName = messageText.substring(0, messageText.lastIndexOf("(")).trim();
                Chapter chapter = questionService.findChapterByName(chapterName);
                if (chapter != null) {
                    user.setSelectedChapterId(chapter.getId());

                    if (user.getBotStateVariable().equals(TESTING.name())) {
                        user.setBotState(TESTING);
                    }
                    if (user.getBotStateVariable().equals(LEARNING.name())) {
                        user.setBotState(LEARNING);
                    }
                    userService.save(user);
                    sendMessage(update, "Выбранная тема: " + chapter.getName());
                    processNextQuestion(user, update);
                } else {
                    sendMessage(update, "Темы с таким названием не найдено");
                }
                break;
            }
            case ADMIN_EDIT_ANSWER:
            case ADMIN_EDIT_QUESTION: {
                if (handleBackButton(user, messageText, update, ADMIN_EDIT))
                    break;

                Question question = questionService.getQuestionById(user.getSelectedQuestionId());

                if (user.getBotState() == ADMIN_EDIT_QUESTION)
                    question.setQuestion(messageText);
                else
                    question.setAnswer(messageText);


                questionService.saveQuestion(question);
                user.setBotState(user.getBackBotState());
                userService.save(user);
                questionService.evictAllQuestions();
                sendMessage(update, "Вопрос был успешно изменен");
                showQuestion(update, user);
                break;
            }
            case ADMIN_EDIT: {
                if (handleBackButton(user, messageText, update, user.getBackBotState())){
                    showQuestion(update, user);
                    break;
                }
                Question questionById = questionService.getQuestionById(user.getSelectedQuestionId());
                if (messageText.equals(ADM_EDIT_A.getText())) {
                    user.setBotState(ADMIN_EDIT_ANSWER);
                    userService.save(user);
                    sendMessage(update, "Отредактируйте и отправьте в ответ исправленный вариант вопроса");
                    messageService.sendMessageWOParse(update, questionById.getAnswer());
                } else if (messageText.equals(ADM_EDIT_Q.getText())) {
                    user.setBotState(ADMIN_EDIT_QUESTION);
                    userService.save(user);
                    sendMessage(update, "Отредактируйте и отправьте в ответ исправленный вариант ответа");
                    messageService.sendMessageWOParse(update, questionById.getQuestion());
                } else if (messageText.equals(ADM_DELETE.getText())) {
                    user.setBotState(user.getBackBotState());
                    userService.save(user);
                    questionService.removeQuestionById(user.getSelectedQuestionId());
                    sendMessage(update, "Вопрос (id: " + user.getSelectedQuestionId() +") был удален");
                    processNextQuestion(user, update);
                }
                break;
            }
            case LEARNING: {
                if (messageText.equals(NEXT_BTN.getText())) {
                    processNextQuestion(user, update);
                }
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(SELECTING_CHAPTER);
                    userService.save(user);
                    sendMessage(update, "Вы вернулись к выбору темы");
                }
                if (messageText.equals(ADM_EDIT.getText()) && user.isAdmin()) {
                    user.setBackBotState(user.getBotState());
                    user.setBotState(ADMIN_EDIT);
                    userService.save(user);
                    sendMessage(update, "Выберите какую часть вопроса редактировать");
                }
                break;
            }
            case TESTING: {
                if (messageText.equals(KNOW_BTN.getText())) {
                    user.getSolvedQuestions().add(user.getSelectedQuestionId());
                    sendMessage(update, questionService.getQuestionById(user.getSelectedQuestionId()).getAnswer());
                    processNextQuestion(user, update);
                    userService.save(user);
                }
                if (messageText.equals(DONT_KNOW_BTN.getText())) {
                    sendMessage(update, questionService.getQuestionById(user.getSelectedQuestionId()).getAnswer());
                    processNextQuestion(user, update);
                }
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(SELECTING_CHAPTER);
                    userService.save(user);
                    sendMessage(update, "Вы вернулись к выбору темы");
                }
                if (messageText.equals(ADM_EDIT.getText()) && user.isAdmin()) {
                    user.setBackBotState(user.getBotState());
                    user.setBotState(ADMIN_EDIT);
                    userService.save(user);
                    sendMessage(update, "Выберите какую часть вопроса редактировать");
                }
                break;
            }
            case ADMIN_ADD_CHAPTER_SELECT_PART: {
                if (handleBackButton(user, messageText, update, ADMIN_PAGE))
                    break;
                String name = messageText.substring(0, messageText.lastIndexOf("(")).trim();
                Part partByPartName = questionService.findPartByPartName(name);
                if (partByPartName != null) {
                    user.setBotState(ADMIN_ADD_CHAPTER_ENTER);
                    user.setSelectedPartId(partByPartName.getId());
                    userService.save(user);
                    sendMessage(update, "Введите название главы более 3 символов:");
                } else {
                    sendMessage(update, "Главы с таким названием не найдено");
                }
                break;
            }
            case ADMIN_ADD_CHAPTER_ENTER: {
                if (handleBackButton(user, messageText, update, ADMIN_PAGE))
                    break;
                if (messageText.length() > 3) {
                    user.setBotState(ADMIN_PAGE);
                    userService.save(user);
                    questionService.addChapter(messageText, user.getSelectedPartId());
                    sendMessage(update, "Новая глава с названием: \"" + messageText + "\" была добавлена");
                } else {
                    sendMessage(update, "Введите название главы более 3 символов");
                }
                break;
            }
            case ADMIN_ADD_QUESTION_SELECT_PART: {
                if (handleBackButton(user, messageText, update, ADMIN_PAGE))
                    break;
                String name = messageText.substring(0, messageText.lastIndexOf("(")).trim();
                Part partByPartName = questionService.findPartByPartName(name);
                if (partByPartName != null) {
                    user.setBotState(ADMIN_ADD_QUESTION_SELECT_CHAPTER);
                    user.setSelectedPartId(partByPartName.getId());
                    userService.save(user);
                    sendMessage(update, "Выберите тему:");
                } else {
                    sendMessage(update, "Главы с таким названием не найдено");
                }
                break;
            }
            case ADMIN_ADD_QUESTION_SELECT_CHAPTER: {
                if (handleBackButton(user, messageText, update, ADMIN_ADD_QUESTION_SELECT_PART))
                    break;
                String name = messageText.substring(0, messageText.lastIndexOf("(")).trim();
                Chapter chapter = questionService.findChapterByName(name);
                if (chapter != null) {
                    user.setSelectedChapterId(chapter.getId());
                    user.setBotState(ADMIN_ADD_QUESTION_ENTER);
                    userService.save(user);
                    sendMessage(update, "Выбранная тема: " + chapter.getName());
                    if ("MANY".equals(user.getBotStateVariable()))
                        sendMessage(update, "Приложите файл в формате `:na:%QUESTION%$:an:%ANSWER%:na:%QUESTION%$:an:%ANSWER%`");
                    else
                        sendMessage(update, "Введите ответ и вопрос, в формате `%QUESTION%$:an:%ANSWER%`  и после выдели весь текст CTRL+SHIFT+M");
                } else {
                    sendMessage(update, "Темы с таким названием не найдено");
                }
                break;
            }
            case ADMIN_ADD_QUESTION_ENTER:{
                if (handleBackButton(user, messageText, update, ADMIN_ADD_QUESTION_SELECT_CHAPTER))
                    break;

                if (messageText.equals(SAVE_BTN.getText()) || messageText.equals(SAVE_AND_CONTINUE_BTN.getText())) {

                    if ("MANY".equals(user.getBotStateVariable())){
                        int cnt = questionAdderService.saveQuestions(user.getSelectedChapterId());
                        sendMessage(update, "Сохранено "  + cnt + " вопросов");
                    }
                    else {
                        boolean res = questionAdderService.saveQuestion();
                        sendMessage(update, res ? "Вопрос сохранен" : "Вопрос не сохранен");
                    }

                    if (messageText.equals(SAVE_BTN.getText())){
                        user.setBotState(ADMIN_ADD_QUESTION_SELECT_CHAPTER);
                        userService.save(user);
                        sendMessage(update, "Вы вернулись обратно");
                    }
                } else {

                    if ("MANY".equals(user.getBotStateVariable())) {
                        List<Question> questions = questionAdderService.parseManyQuestions(getDataFromDocument(update.getMessage().getDocument()));
                        if (questions.isEmpty()) {
                            sendMessage(update, "Приложите файл в формате `:na:%QUESTION%$:an:%ANSWER%:na:%QUESTION%$:an:%ANSWER%`");
                        } else {
                            for (Question question : questions) {
                                sendMessage(update, "Вопрос:\n" + question.getQuestion() + "\nОтвет:\n" + question.getAnswer());
                            }
                            sendMessage(update, questions.size() + " вопросов в списке");
                            questionAdderService.setTempQuestions(questions);
                        }
                    } else {
                        Question question = questionAdderService.parseQuestion(messageText, true);
                        if (question == null) {
                            sendMessage(update, "Введите ответ и вопрос, в формате `%QUESTION%$[an]%ANSWER%` и после выдели весь текст CTRL+SHIFT+M");
                        } else {
                            sendMessage(update, "Вопрос:\n" + question.getQuestion() + "\nОтвет:\n" + question.getAnswer());
                            questionAdderService.setTempQuestion(question.getQuestion(), question.getAnswer(), user.getSelectedChapterId());
                        }
                    }
                }
                break;
            }
            case SETTINGS: {
                if (handleBackButton(user, messageText, update, DEFAULT))
                    break;
                if (messageText.equals(STATS_BTN.getText())) {
                    sendMessage(update, userService.getStatistic(chatId));
                    break;
                }
                if (messageText.equals(RESET_STATS_BTN.getText())) {
                    userService.resetStatistic(chatId);
                    sendMessage(update, "Статистика сброшена :sunny:");
                    break;
                }
                if (messageText.equals(SET_COUNTER_BTN.getText())) {
                    user.setBotState(SETTING_COUNTER);
                    userService.save(user);
                    String msg = "";
                    QuestionsCounter cnt = user.getQuestionsCounter();
                    if (cnt != null) {
                        msg = "Ваш текущий лимит: " + cnt.getLimit() + ", сегодня пройдено: " + cnt.getSolved() + ".";
                    }
                    else {
                        msg = "У вас еще нет лимита.";
                    }
                    msg = msg + " Вы можете ввести новый лимит. От 0 до бесконечности.";
                    msg = msg + " Для сброса счетчика введите -1";
                    sendMessage(update, msg);
                    break;
                }
                break;
            }
            case SETTING_COUNTER: {
                if (handleBackButton(user, messageText, update, SETTINGS))
                    break;
                int num;
                try {
                    num = Integer.valueOf(messageText);
                    if (num > 0) {
                        questionsLearnedTodayCounterService.setNewCounter(user, num);
                        userService.setBotState(user, SETTINGS);
                        sendMessage(update, "Ваш новый счетчик пройденных вопросов в день: " + num);
                    } else {
                        if (num == -1) {
                            questionsLearnedTodayCounterService.dropCounter(user);
                            userService.setBotState(user, SETTINGS);
                            sendMessage(update, "Вы сбросили счетчик");
                        } else {
                            sendMessage(update, "Введите число больше 0");
                        }
                    }
                } catch (NumberFormatException e) {
                    sendMessage(update, "Введите корректное число");
                }
                break;
            }
        }
    }
    private String getDataFromDocument(Document document) {
        return this.messageService.getDataFromDocument(document);
    }

    private void showQuestion(Update update, User user) {
        Question question = questionService.getQuestionById(user.getSelectedQuestionId());
        StringBuilder builder = new StringBuilder();

        if (user.getBotState() == TESTING) {
            builder.append("*");
            builder.append(question.getQuestion());
            builder.append("*");

        }
        if (user.getBotState() == LEARNING) {
            builder.append("*");
            builder.append(question.getQuestion().replace("`", ""));
            builder.append("*\n");
            builder.append(question.getAnswer());
        }
        sendMessage(update, builder.toString());
    }

    private void processNextQuestion(User user, Update update) {
        Question question = questionService.getNextQuest(user);

        if (question == null) {
            user.setBotState(SELECTING_CHAPTER);
            sendMessage(update, "Вопросы закончились, начните новую главу! :white_check_mark:");
            userService.save(user);
            return;
        }

        user.setSelectedQuestionId(question.getId());


        if (user.getBotState() == LEARNING) {
            user.incrementQuestionViewed();
        }

        userService.save(user);

        LearnedToday learnedToday = questionsLearnedTodayCounterService.increaseLearnedToday(user);
        if (learnedToday.getDescription() != null)
            sendMessage(update, learnedToday.getDescription());

        showQuestion(update, user);
    }

    public boolean handleBackButton(User user, String messageText, Update update, BotState backState) {
        if (messageText.equals(BACK_BTN.getText())) {
            user.setBotState(backState);
            userService.save(user);
            sendMessage(update, "Вы вернулись обратно");
            return true;
        }
        return false;
    }
}
