package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;

import java.util.List;

public interface QuestionRepo extends CrudRepository<Question, Long> {
    List<Question> findAllByChapter(Chapter chapter);
    List<Question> findAllByChapterId(Long id);

    @Query(value = "SELECT * FROM question q " +
            "LEFT JOIN user_solved_question usq ON usq.solved_questions = q.id AND usq.user_id = :userId" +
            "WHERE usq.id IS NULL" +
            "ORDER BY rand()" +
            "LIMIT 1 ", nativeQuery = true)
    Question findRandomNotSolvedQuestion(@Param("userId") Long userId);
}
