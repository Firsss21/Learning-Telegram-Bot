package ru.firsov.study.Java.Telegram.Bot.common.service;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import ru.firsov.study.Java.Telegram.Bot.AbstractIntegrationTest;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionAdderServiceTest extends AbstractIntegrationTest {

    @Autowired
    QuestionAdderService questionAdderService;

    @Test
    void parseManyQuestions_badInputShouldEmpty() {
        List<Question> questions = questionAdderService.parseManyQuestions("123");
        Assert.assertTrue(questions.isEmpty());
    }

    @Test
    void parseManyQuestions_goodWhenOneQuestion() {
        String question = "foo";
        String answer = "bar";

        String input = ":na:" + question + ":an:" + answer;

        List<Question> questions = questionAdderService.parseManyQuestions(input);
        Assert.assertFalse(questions.isEmpty());

        Question q = questions.get(0);
        Assert.assertEquals(question, q.getQuestion());
        Assert.assertEquals(answer, q.getAnswer());

    }

    @Test
    void parseManyQuestions_goodWhenManyQuestions() {
        String question = "foo";
        String answer = "bar";

        String input = ":na:" + question + ":an:" + answer;

        input += input + input;

        List<Question> questions = questionAdderService.parseManyQuestions(input);

        Assert.assertEquals(3, questions.size());
        for (Question q : questions) {
            Assert.assertEquals(question, q.getQuestion());
            Assert.assertEquals(answer, q.getAnswer());
        }
    }

    @Test
    void parseManyQuestions_emptyWhenOneOfQuestionsIsBad() {
        String question = "foo";
        String answer = "bar";

        String input = ":na:" + question + ":an:" + answer;

        input += input + input + ":na:";

        List<Question> questions = questionAdderService.parseManyQuestions(input);
        Assert.assertTrue(questions.isEmpty());
    }

    @Test
    void parseQuestion_goodInput() {
        String question = "foo";
        String answer = "bar";

        String input = question + ":an:" + answer;

        Question questionObj = questionAdderService.parseQuestion(input, true);

        Assert.assertNotNull(questionObj);
        Assert.assertEquals(question, questionObj.getQuestion());
        Assert.assertEquals(answer, questionObj.getAnswer());
    }

    @Test
    void parseQuestion_badInput() {
        String input = "foobar";

        Question questionObj = questionAdderService.parseQuestion(input, true);
        Assert.assertNull(questionObj);
    }
}