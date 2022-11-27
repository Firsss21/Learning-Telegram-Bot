package ru.firsov.study.Java.Telegram.Bot.common.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Part;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.firsov.study.Java.Telegram.Bot.telegram.Text.*;

@Service
@AllArgsConstructor
public class KeyboardService {

    private final UserService userService;
    private final QuestionService questionService;

    public ReplyKeyboardMarkup getKeyBoardByState(Long chatId) {
        User user = userService.getUser(chatId);
        switch (user.getBotState()) {
            case ADMIN_ADD_QUESTION_SELECT_PART:
            case ADMIN_ADD_CHAPTER_SELECT_PART:
            case SELECTING_PART : {
                return getChapters();
            }
            case ADMIN_ADD_QUESTION_SELECT_CHAPTER:
            case SELECTING_CHAPTER : {
                return getQuestions(user.getSelectedPartId());
            }
            case TESTING : {
                if (user.isAdmin()) {
                    return getKeyBoard(new String[][]{
                            {KNOW_BTN.getText(), DONT_KNOW_BTN.getText()},
                            {BACK_BTN.getText(), ADM_EDIT.getText()}
                    });
                }
                return getKeyBoard(new String[][]{
                        {KNOW_BTN.getText(), DONT_KNOW_BTN.getText()},
                        {BACK_BTN.getText()}
                });
            }
            case LEARNING : {
                if (user.isAdmin()) {
                    return getKeyBoard(new String[][]{
                            {NEXT_BTN.getText(), BACK_BTN.getText()},
                            {ADM_EDIT.getText()}
                    });
                }
                return getKeyBoard(new String[][]{
                        {NEXT_BTN.getText(), BACK_BTN.getText()},
                });
            }
            case ADMIN_PAGE : {
                return getKeyBoard(new String[][]{
                        {ADM_STATS_BTN.getText(), ADM_MSG_TO_ALL_BTN.getText()},
                        {ADM_CACHE_EVICT.getText(), ADM_ADD_CHAPTER.getText()},
                        {ADM_ADD_QUESTION.getText(), ADM_ADD_QUESTIONS.getText()},
                        {BACK_BTN.getText()}
                });
            }
            case ADMIN_EDIT : {
                return getKeyBoard(new String[][]{
                        {ADM_EDIT_A.getText(), ADM_EDIT_Q.getText()},
                        {ADM_DELETE.getText()},
                        {BACK_BTN.getText()}
                });
            }
            case ADMIN_ADD_QUESTION_ENTER:{
                return getKeyBoard(new String[][] {
                        {SAVE_AND_CONTINUE_BTN.getText(), BACK_BTN.getText()},
                });
            }
            case ADMIN_ADD_CHAPTER_ENTER:{
                return getKeyBoard(new String[][] {{BACK_BTN.getText()}});
            }
            case DEFAULT : {
                if (!user.isAdmin())
                    return getDefaultKeyboard();
                else
                    return getAdminKeyBoard();
            }
            default : {
                return getKeyBoard(new String[][]{
                        {BACK_BTN.getText()},
                });
            }
        }
    }

    private ReplyKeyboardMarkup getQuestions(long partId) {
        List<Chapter> allChapters = questionService.findAllChaptersByPartId(partId, false);
        List<String> chapters = allChapters.stream().map(Chapter::getName).collect(Collectors.toList());
        chapters.add(BACK_BTN.getText());
        List<List<String>> lists = transformListToListOfLists(chapters, 2);
        return getKeyBoard(lists);
    }

    private ReplyKeyboardMarkup getChapters() {
        List<Part> allParts = questionService.findAllParts(false);
        List<String> parts = allParts.stream().map(Part::getName).collect(Collectors.toList());
        parts.add(BACK_BTN.getText());
        List<List<String>> lists = transformListToListOfLists(parts, 2);
        return getKeyBoard(lists);
    }

    private ReplyKeyboardMarkup getAdminKeyBoard() {
            return getKeyBoard(new String[][]{
                    {LEARN_BTN.getText(), TEST_BTN.getText()},
                    {STATS_BTN.getText(), RESET_STATS_BTN.getText()},
//                    {INFO_BTN.getText(), DONATE_BTN.getText()},
                    {INFO_BTN.getText()},
                    {ADM_ENTER.getText()}
            });
    }

    private InlineKeyboardMarkup getUnderButtonKeyboard(String[][] strings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (String s : ss) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(s);
                inlineKeyboardButton.setCallbackData(s);
                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getKeyBoard(String[][] strings) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        for (String[] ss : strings) {

            for (int i = 0; i < ss.length; i++)
                ss[i] = EmojiParser.parseToUnicode(ss[i]);

            KeyboardRow row = new KeyboardRow();
            row.addAll(Arrays.asList(ss));
            rowList.add(row);
        }

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup getKeyBoard(List<List<String>> strings) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> rowList = new ArrayList<>();

        for (List<String> row : strings) {
            KeyboardRow krow = new KeyboardRow();
            krow.addAll(row);
            rowList.add(krow);
        }

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup getDefaultKeyboard() {
        return getKeyBoard(new String[][]{
                {LEARN_BTN.getText(), TEST_BTN.getText()},
                {STATS_BTN.getText(), RESET_STATS_BTN.getText()},
                {INFO_BTN.getText()},
//                {INFO_BTN.getText(), DONATE_BTN.getText()},
        });
    }

    public <T> List<List<T>> transformListToListOfLists(List<T> list, int rowLength) {

        if (list.size() == 0) {
            return new ArrayList<>();
        }

        int rows = (int) Math.round(((double) list.size() / rowLength));

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            result.add(i, new ArrayList<>());
        }
        for (int i = 0; i < list.size(); i++) {
            result.get(i / rowLength).add(i % rowLength, list.get(i));
        }
        return result;
    }
}
