package ru.firsov.study.Java.Telegram.Bot.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;
import ru.firsov.study.Java.Telegram.Bot.common.cache.UserCache;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.UserRepo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional
public class UserService {

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private final UserRepo userRepo;
    @Value("#{${user.millisForNewVisit}}")
    private Long timeForNewVisit;

    /**
     * Если такой чат уже существует, то true, иначе false
     *
     * @param chatId
     * @return
     */
    public boolean isChatInit(Long chatId) {
        return getUser(chatId) != null;
    }

    /**
     * Сохраняем в бд новую запись о чате
     *
     * @param chatId
     */
    public void initChat(Long chatId, String name) {
        userRepo.save(new User(chatId, BotState.DEFAULT, name));
    }

    /**
     * Удаляем из бд запись по chatId
     *
     * @param chatId
     */
//    @CacheEvict(value = UserCache.NAME, key = "")
    public void deleteChat(Long chatId) {
        userRepo.deleteByChatId(chatId);
    }

    /**
     * Устанавливает новый стейт для чата
     *
     * @param chatId
     * @param botState
     */
    public void setBotState(Long chatId, BotState botState) {
        User chat = getUser(chatId);
        chat.setBotState(botState);
        userRepo.save(chat);
    }

    public void setBotStateVariable(Long chatId, String var) {
        User chat = getUser(chatId);
        chat.setBotStateVariable(var);
        userRepo.save(chat);
    }

    public void save(User user) {
        userRepo.save(user);
    }

    /**
     * Получает стейт чата
     *
     * @param chatId
     */
    public BotState getBotState(Long chatId) {
        return getUser(chatId).getBotState();
    }

//    @Cacheable(value = UserCache.NAME, sync = true)
    public User getUser(Long chatId) {
        return userRepo.findAllByChatId(chatId);
    }

    public String getStatistic(Long chatId) {
        User user = getUser(chatId);
        StringBuilder builder = new StringBuilder();
        builder
                .append(":briefcase: Статистика\n")
                .append(":bust_in_silhouette: Имя: ")
                .append(user.getName())
                .append("\n:pencil2: Количество запомненных вопросов: ")
                .append(user.getSolvedQuestions().size())
                .append("\n:mag: Просмотрено вопросов:")
                .append(user.getQuestionViewed())
        ;

        return builder.toString();
    }

    public List<Long> getAllChatIds() {
        return userRepo.getAllChatIds();
    }

    public void resetStatistic(Long chatId) {
        User byId = getUser(chatId);
        byId.getSolvedQuestions().clear();
        userRepo.save(byId);
    }

    public List<User> getUsersNotActiveFrom(Long time) {
        return userRepo.getUsersNotActiveFrom(time);
    }

    public void processUpdateUser(User user) {
        Long now = System.currentTimeMillis();

        if (now - user.getLastActivity() > timeForNewVisit) {
            user.incrementVisitsCount();
            if (newDay(now, user.getLastActivity())) {
                user.incrementDaysEntered();
            }
        }

        user.incrementActionsCount();
        user.setLastActivity(now);
        this.save(user);
    }

    private boolean newDay(Long oldTime, Long newTime) {
        OffsetDateTime odt = OffsetDateTime.now();
        ZoneOffset zoneOffset = odt.getOffset();

        LocalDateTime oldDate = LocalDateTime.ofEpochSecond(oldTime / 1000, 0, zoneOffset);
        LocalDateTime newDate = LocalDateTime.ofEpochSecond(newTime / 1000, 0, zoneOffset);

        if (
                oldDate.getDayOfMonth() < newDate.getDayOfMonth() ||
                        oldDate.getMonthValue() < newDate.getMonthValue() ||
                        oldDate.getYear() < newDate.getYear()
        ) {
            return true;
        }
        return false;
    }

    public int getOnlineFrom(Long time) {
        return userRepo.getOnlineFrom(time);
    }

    public int getTotalQuestionViewed() {
        return userRepo.getQuestionViewedCount();
    }

    public int getActionsCount() {
        return userRepo.getTotalActions();
    }

    public List<User> getTop10ByActions() {
        return userRepo.findTop10ByOrderByActionsCountDesc();
    }
}
