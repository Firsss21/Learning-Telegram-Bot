package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.repository.CrudRepository;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;

import java.util.List;

public interface QuestionRepo extends CrudRepository<Question, Long> {
    List<Question> findAllByChapter(Chapter chapter);
    List<Question> findAllByChapterId(Long id);
}
