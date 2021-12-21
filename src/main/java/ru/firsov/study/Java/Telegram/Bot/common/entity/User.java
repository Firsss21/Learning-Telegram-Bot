package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private Long chatId;

    private BotState botState;

    private BotState backBotState;

    private String botStateVariable;

    private String name;

    private Long lastNotification;

    private Integer actionsCount;

    private Long regTime;

    private Long lastActivity;

    private Integer donatedSum;

    private Integer visitCounts;

    private Integer questionViewed;

    private Integer daysEntered;

    private Long selectedPartId;

    private Long selectedChapterId;

    private Long selectedQuestionId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<InfoMessage> messages;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Rule> rights;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Long> solvedQuestions;

    public User(Long chatId, BotState aDefault, String name) {
        this.name = name;
        this.chatId = chatId;
        this.botState = aDefault;
        this.donatedSum = 0;
        this.lastActivity = System.currentTimeMillis();
        this.regTime = System.currentTimeMillis();
        this.actionsCount = 1;
        this.lastNotification = System.currentTimeMillis();
        this.solvedQuestions = new ArrayList<>();
        this.botStateVariable = "";
        this.visitCounts = 1;
        this.daysEntered = 1;
        this.questionViewed = 0;
        this.messages = new ArrayList<>();
        this.rights = Set.of(Rule.USER);
        this.backBotState = BotState.DEFAULT;
    }

    public Collection<Long> getSolvedQuestions() {
        Hibernate.initialize(solvedQuestions);
        return solvedQuestions;
    }


    public List<InfoMessage> getMessages() {
        Hibernate.initialize(messages);
        return messages;
    }


    public Set<Rule> getRights() {
        Hibernate.initialize(rights);
        return rights;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isAdmin() {
        return getRights().contains(Rule.ADMIN);
    }

    public void incrementQuestionViewed() {
        this.questionViewed++;
    }

    public void incrementActionsCount() {
        this.actionsCount++;
    }

    public void incrementVisitsCount() {
        this.visitCounts++;
    }

    public void incrementDaysEntered() {
        this.daysEntered++;
    }
}
