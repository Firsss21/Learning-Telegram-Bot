package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Rating;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticService {

    private final UserService userService;

    private int getOnlineLast(Long hoursAgo) {
        long time = System.currentTimeMillis() - (hoursAgo * 60 * 60 * 1000);
        return userService.getOnlineFrom(time);
    }
    private int getCountPlayers() {
        return userService.getAllChatIds().size();
    }

    private int getTotalQuestionViewed() {
        return userService.getTotalQuestionViewed();
    }
    private int getActionsCount() {
        return userService.getActionsCount();
    }

    private List<Rating> getActionsRating() {
        List<User> ratingByTotalAction = userService.getTop10ByActions();
        List<Rating> ratings = new ArrayList<>();
        int place = 1;
        for (User user : ratingByTotalAction) {
            ratings.add(new Rating(user.getChatId(), user.getName(), place, user.getActionsCount()));
            place++;
        }
        return ratings;
    }

    public String buildStatsMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**Статистика**");
        builder.append("\n");
        builder.append(":white_small_square: Онлайн (24ч): " + getOnlineLast(24L));
        builder.append("\n");
        builder.append(":black_small_square: Онлайн (72ч): " + getOnlineLast(72L));
        builder.append("\n");
        builder.append(":white_small_square: Онлайн (168ч): " + getOnlineLast(168L));
        builder.append("\n");
        builder.append(":black_small_square: Онлайн (720ч): " + getOnlineLast(720L));
        builder.append("\n");
        builder.append(":white_small_square: Всего игроков: " + getCountPlayers());
        builder.append("\n");
        builder.append(":black_small_square: Всего просмотрено вопросов: " + getTotalQuestionViewed());
        builder.append("\n");
        builder.append(":white_small_square: Всего действий: " + getActionsCount());
        builder.append("\n");
        builder.append(":black_small_square: ТОП-10 по действиям:");
        builder.append("\n");
        for (Rating rating : getActionsRating()) {
            builder.append(rating.getPlace() + ": " + rating.getName() + "(" + rating.getId() + ") " + rating.getRating() + "\n");
        }
        return builder.toString();
    }
}
