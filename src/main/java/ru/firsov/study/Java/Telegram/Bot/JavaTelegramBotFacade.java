package ru.firsov.study.Java.Telegram.Bot;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;
import ru.firsov.study.Java.Telegram.Bot.common.Command;
import ru.firsov.study.Java.Telegram.Bot.common.bean.MessageGenerator;
import ru.firsov.study.Java.Telegram.Bot.common.entity.*;
import ru.firsov.study.Java.Telegram.Bot.common.service.MessageService;
import ru.firsov.study.Java.Telegram.Bot.common.service.QuestionService;
import ru.firsov.study.Java.Telegram.Bot.common.service.StatisticService;
import ru.firsov.study.Java.Telegram.Bot.common.service.UserService;
import ru.firsov.study.Java.Telegram.Bot.telegram.BotFacade;
import ru.firsov.study.Java.Telegram.Bot.telegram.CallbackAnswer;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import static ru.firsov.study.Java.Telegram.Bot.common.BotState.*;
import static ru.firsov.study.Java.Telegram.Bot.common.Command.*;
import static ru.firsov.study.Java.Telegram.Bot.telegram.Text.*;

@Service
public class JavaTelegramBotFacade implements BotFacade {

    private final MessageGenerator messageGenerator;
    private final UserService userService;
    private final CallbackAnswer callbackAnswer;
    private final QuestionService questionService;
    private final StatisticService statisticService;
    private MessageService messageService;

    public JavaTelegramBotFacade(MessageGenerator messageGenerator, UserService userService, CallbackAnswer callbackAnswer, QuestionService questionService, StatisticService statisticService) {
        this.messageGenerator = messageGenerator;
        this.userService = userService;
        this.callbackAnswer = callbackAnswer;
        this.questionService = questionService;
        this.statisticService = statisticService;
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
            messageText = update.getMessage().getText() == null ? "" : update.getMessage().getText().replace("/", "");
            userFirstName = update.getMessage().getChat().getFirstName();
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            messageText = update.getChannelPost().getText() == null ? "" : update.getChannelPost().getText().replace("/", "");

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

        if (!userService.isChatInit(chatId)) {
            userService.initChat(chatId, userFirstName);
            sendMessage(update, messageGenerator.generateStartMessage(userFirstName));
        } else {
            if (!handleCommand(messageText, update, chatId))
                handleBotState(update, chatId, messageText);
        }
    }

    /**
     * ???????????????????? ???????????????? ?????????????????? ????????????????????????
     *
     * @param update
     * @param messageText
     */
    private void sendMessage(Update update, String messageText) {
        messageService.sendMessage(update, messageText);
    }

    private boolean handleCommand(String messageText, Update update, Long chatId) {
        User user = userService.getUser(chatId);
        userService.processUpdateUser(user);

        if (messageText.toUpperCase(Locale.ROOT).equals(HELP.name())) {
            sendMessage(update, messageGenerator.generateHelpMessage());
            return true;
        }

        if (messageText.equals(INFO_BTN.getText()) || messageText.toUpperCase(Locale.ROOT).equals(INFO.name())) {
            sendMessage(update, messageGenerator.generateInfoMessage());
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(START.name())) {
            userService.setBotState(chatId, DEFAULT);
            sendMessage(update, messageGenerator.generateStartMessage(update.getMessage().getChat().getFirstName()));
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(Command.CANCEL.name()) || messageText.equals(CANCEL_BTN.getText())) {
            if (user.getBotState() == DEFAULT) {
                sendMessage(update, "?????? ???????????????? ?????????????? ?????? ????????????????????");
            } else {
                userService.setBotState(chatId, DEFAULT);
                sendMessage(update, messageGenerator.generateSuccessCancel());
            }
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(STATISTIC.name()) || messageText.equals(STATS_BTN.getText())) {
            sendMessage(update, userService.getStatistic(chatId));
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(DONATE.name()) || messageText.equals(DONATE_BTN.getText())) {
            sendMessage(update, "??????????");
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals(RESET.name()) || messageText.equals(RESET_STATS_BTN.getText())) {
            userService.resetStatistic(chatId);
            sendMessage(update, "???????????????????? ???????????????? :sunny:");
            return true;
        }

        if (messageText.toUpperCase(Locale.ROOT).equals("ADMIN") || messageText.equals(ADM_ENTER.getText())) {
            if (!user.isAdmin()) {
                String token = UUID.randomUUID().toString();
                messageService.sendMessage(adminId, ":key: ?????????? ?????? ?????????????????????? ????????????????????????????: \"" + token + "\"");
                user.setBotState(ADMIN_LOGIN);
                user.setBotStateVariable(token);
                userService.save(user);
                sendMessage(update, ":key: ?????????????? ??????");
                return true;
            } else {
                user.setBotState(ADMIN_PAGE);
                userService.save(user);
                sendMessage(update, ":atom_symbol: ???????????????? ?????????? ????????");
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
                    sendMessage(update, "???? ???????????????????????? ?????? ??????????????????????????");
                } else {
                    sendMessage(update, "?????? ????????????????");
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
                    sendMessage(update, ":pencil2: ?????????????? ??????????????????, ?????????????? ???????????? ?????????????????? ???????? ?????????????????????????? ");
                    break;
                }
                if (messageText.equals(ADM_CACHE_EVICT.getText())){
                    questionService.evictAllQuestions();
                    sendMessage(update, "?????? ??????????????");
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
                    sendMessage(update, "???????????????? ??????????");
                }
                break;
            }
            case SELECTING_PART: {
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(DEFAULT);
                    userService.save(user);
                    sendMessage(update, "???? ?????????????????? ?? ???????????? ??????????");
                    break;
                }
                Part partByPartName = questionService.findPartByPartName(messageText);
                if (partByPartName != null) {
                    user.setBotState(SELECTING_CHAPTER);
                    user.setSelectedPartId(partByPartName.getId());
                    userService.save(user);
                    sendMessage(update, "???????????????? ????????:");
                } else {
                    sendMessage(update, "?????????? ?? ?????????? ?????????????????? ???? ??????????????");
                }
                break;
            }
            case SELECTING_CHAPTER: {
                if (messageText.equals(BACK_BTN.getText())) {
                    user.setBotState(SELECTING_PART);
                    userService.save(user);
                    sendMessage(update, "???? ?????????????????? ?? ???????????? ??????????");
                    break;
                }

                Chapter chapter = questionService.findChapterByName(messageText);
                if (chapter != null) {
                    user.setSelectedChapterId(chapter.getId());

                    if (user.getBotStateVariable().equals(TESTING.name())) {
                        user.setBotState(TESTING);
                    }
                    if (user.getBotStateVariable().equals(LEARNING.name())) {
                        user.setBotState(LEARNING);
                    }
                    userService.save(user);
                    sendMessage(update, "?????????????????? ????????: " + chapter.getName());
                    processNextQuestion(user, update);
                } else {
                    sendMessage(update, "???????? ?? ?????????? ?????????????????? ???? ??????????????");
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
                sendMessage(update, "???????????? ?????? ?????????????? ??????????????");
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
                    sendMessage(update, "???????????????????????????? ?? ?????????????????? ?? ?????????? ???????????????????????? ?????????????? ??????????????");
                    messageService.sendMessageWOParse(update, questionById.getAnswer());
                } else if (messageText.equals(ADM_EDIT_Q.getText())) {
                    user.setBotState(ADMIN_EDIT_QUESTION);
                    userService.save(user);
                    sendMessage(update, "???????????????????????????? ?? ?????????????????? ?? ?????????? ???????????????????????? ?????????????? ????????????");
                    messageService.sendMessageWOParse(update, questionById.getQuestion());
                } else if (messageText.equals(ADM_DELETE.getText())) {
                    user.setBotState(user.getBackBotState());
                    userService.save(user);
                    questionService.removeQuestionById(user.getSelectedQuestionId());
                    sendMessage(update, "???????????? (id: " + user.getSelectedQuestionId() +") ?????? ????????????");
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
                    sendMessage(update, "???? ?????????????????? ?? ???????????? ????????");
                }
                if (messageText.equals(ADM_EDIT.getText()) && user.isAdmin()) {
                    user.setBackBotState(user.getBotState());
                    user.setBotState(ADMIN_EDIT);
                    userService.save(user);
                    sendMessage(update, "???????????????? ?????????? ?????????? ?????????????? ??????????????????????????");
                }
                break;
            }
            case TESTING: {
                if (messageText.equals(KNOW_BTN.getText())) {
                    user.getSolvedQuestions().add(user.getSelectedQuestionId());
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
                    sendMessage(update, "???? ?????????????????? ?? ???????????? ????????");
                }
                if (messageText.equals(ADM_EDIT.getText()) && user.isAdmin()) {
                    user.setBackBotState(user.getBotState());
                    user.setBotState(ADMIN_EDIT);
                    userService.save(user);
                    sendMessage(update, "???????????????? ?????????? ?????????? ?????????????? ??????????????????????????");
                }
                break;
            }
        }
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
            sendMessage(update, "?????????????? ??????????????????????, ?????????????? ?????????? ??????????! :white_check_mark:");
            userService.save(user);
            return;
        }

        user.setSelectedQuestionId(question.getId());

        if (user.getBotState() == LEARNING) {
            user.incrementQuestionViewed();
        }

        userService.save(user);

        showQuestion(update, user);
    }

    public boolean handleBackButton(User user, String messageText, Update update, BotState backState) {
        if (messageText.equals(BACK_BTN.getText())) {
            user.setBotState(backState);
            userService.save(user);
            sendMessage(update, "???? ?????????????????? ??????????????");
            return true;
        }
        return false;
    }
}
