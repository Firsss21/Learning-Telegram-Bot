package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.entity.QuestionsCounter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.QuestionsCounterRepo;
import ru.firsov.study.Java.Telegram.Bot.common.state.FullyLearnedToday;
import ru.firsov.study.Java.Telegram.Bot.common.state.LearnedToday;
import ru.firsov.study.Java.Telegram.Bot.common.state.NothingHappendLearnedToday;
import ru.firsov.study.Java.Telegram.Bot.common.state.RemainingSomethingLearnedToday;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionsLearnedTodayCounterService {

    private final QuestionsCounterRepo repo;
    private final QuestionService questionService;

    public LearnedToday increaseLearnedToday(User user) {
        QuestionsCounter counter = user.getQuestionsCounter();
        if (counter == null) return new NothingHappendLearnedToday();

        int previous = counter.getSolved();
        counter.increaseCounter();
        int after = counter.getSolved();
        repo.save(counter);

        if (previous < counter.getLimit() && after >= counter.getLimit()) {
            return new FullyLearnedToday(counter.getLimit(), user.getSolvedQuestions().size(), (int) questionService.getQuestionsCount());
        } else if (after < counter.getLimit() && (counter.getLimit() - after) % 10 == 0){
            return new RemainingSomethingLearnedToday(counter.getLimit(), after);
        }
        return new NothingHappendLearnedToday();
    }

    public void setNewCounter(User user, int limit) {
        QuestionsCounter counter = user.getQuestionsCounter();

        if (counter == null) {
            counter = new QuestionsCounter(user, limit);
        } else {
            if (counter.getLimit() == limit)
                return;
            else {
                counter.setLimit(limit);
                counter.setSolved(0);
            }
        }
        repo.save(counter);
    }

    public void dropCounter(User user) {
        QuestionsCounter questionsCounter = user.getQuestionsCounter();
        if (questionsCounter == null) return;
        repo.delete(questionsCounter);
    }
}
