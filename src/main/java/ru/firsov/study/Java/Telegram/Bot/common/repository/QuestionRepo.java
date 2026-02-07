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
            "LEFT JOIN user_solved_questions usq ON usq.solved_questions = q.id AND usq.user_id = :userId " +
            "WHERE usq.user_id IS NULL " +
            "ORDER BY RANDOM() " +
            "LIMIT 1 ", nativeQuery = true)
    Question findRandomNotSolvedQuestion(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM question q " +
            "LEFT JOIN user_solved_questions usq ON usq.solved_questions = q.id AND usq.user_id = :userId " +
            "JOIN chapter ch ON q.chapter_id = ch.id " +
            "WHERE usq.user_id IS NULL AND ch.part_id = :partId " +
            "ORDER BY RANDOM() " +
            "LIMIT 1 ", nativeQuery = true)
    Question findRandomNotSolvedQuestionByPart(@Param("userId") Long userId, @Param("partId") Long partId);

    @Query(value = "SELECT * FROM question q " +
            "LEFT JOIN user_solved_questions usq ON usq.solved_questions = q.id AND usq.user_id = :userId " +
            "JOIN chapter ch ON q.chapter_id = ch.id " +
            "WHERE usq.user_id IS NULL AND ch.part_id = :partId AND ch.id = :chapterId " +
            "ORDER BY RANDOM() " +
            "LIMIT 1 ", nativeQuery = true)
    Question findRandomNotSolvedQuestionByPartAndChapter(@Param("userId") Long userId, @Param("partId") Long partId, @Param("chapterId") Long chapterId);
}
