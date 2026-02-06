package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Rule;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findAllByChatId(Long id);

    void deleteByChatId(Long id);

    @Query("SELECT chatId FROM User")
    List<Long> getAllChatIds();

    @Query("SELECT count(chatId) FROM User where lastActivity > :time")
    Integer getOnlineFrom(long time);

    @Query("SELECT u FROM User u where u.lastActivity < :time")
    List<User> getUsersNotActiveFrom(long time);

    @Query("SELECT sum(questionViewed) FROM User")
    Integer getQuestionViewedCount();

    @Query("SELECT sum(actionsCount) FROM User")
    Integer getTotalActions();

    List<User> findTop10ByOrderByActionsCountDesc();

    List<User> findAllByQuestionsCounterNotNull();

    List<User> findAllByRightsContains(Rule rule);
}
