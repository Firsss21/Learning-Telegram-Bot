package ru.firsov.study.Java.Telegram.Bot.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionAdderService {

    @Autowired
    private QuestionService questionService;

    private Question tempQuestion = null;

    private List<Question> tempQuestions = null;

    public void setTempQuestion(String question, String answer, long chapterId) {
        Chapter chapterByName = questionService.findChapterById(chapterId).get();
        this.tempQuestion = new Question(question, answer, chapterByName);
    }

    public void setTempQuestions(List<Question> questions) {
        this.tempQuestions = questions;
    }

    public List<Question> parseManyQuestions(String input) {
        String[] split = input.split(":na:");
        if (split.length == 0) {
            return Collections.emptyList();
        }

        List<Question> result = new ArrayList<>();
        for (String s : split) {
            if (s.isEmpty() || s.isBlank()) continue;
            Question question = parseQuestion(s, true);

            if (question == null) {
                System.out.println(result);
                System.out.println("Error when result size: " + result.size());
                return Collections.emptyList();
            }

            result.add(question);
        }
        return result;
    }

    public Question parseQuestion(String input, boolean withReplace) {
        String[] split;
        if (withReplace) {
            String replace = input
                    .replace("[*]", "*")
                    .replace("[_]", "__")
                    .replace("[mw]", ":white_small_square:")
                    .replace("[mb]", ":black_small_square:")
                    .replace("[``]", "```");
            split = replace.split(":an:");
        } else {
            split = input.split(":an:");
        }
        if (split.length != 2) return null;

        return new Question(split[0].trim(), split[1].trim());
    }

    public int saveQuestions(long chapterId) {
        Chapter chapterByName = questionService.findChapterById(chapterId).get();
        if (this.tempQuestions == null) return 0;
        int counter = 0;
        for (Question q : tempQuestions) {
            if (q.getId() != null && q.getId() != 0) continue;
            q.setChapter(chapterByName);
            questionService.saveQuestion(q);
            counter++;
        }
        return counter;
    }

    public boolean saveQuestion() {
        if (this.tempQuestion != null && (tempQuestion.getId() == null || tempQuestion.getId() == 0)) {
            this.tempQuestion = questionService.saveQuestion(tempQuestion);
            return true;
        }
        return false;
    }

}
