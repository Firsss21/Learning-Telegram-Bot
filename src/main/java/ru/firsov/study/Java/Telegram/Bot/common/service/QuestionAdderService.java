package ru.firsov.study.Java.Telegram.Bot.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;

@Service
public class QuestionAdderService {

    @Autowired
    private QuestionService questionService;

    private Question tempQuestion = null;

    public void setTempQuestion(String question, String answer, long chapterId) {
        Chapter chapterByName = questionService.findChapterById(chapterId).get();
        this.tempQuestion = new Question(question, answer, chapterByName);
    }

    public void saveQuestion() {
        if (this.tempQuestion != null && (tempQuestion.getId() == null || tempQuestion.getId() == 0)) {
            System.out.println("saved");
            this.tempQuestion = questionService.saveQuestion(tempQuestion);
        } else {
            System.out.println(tempQuestion.getId());
        }
    }
}
