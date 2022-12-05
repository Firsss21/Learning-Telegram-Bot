package ru.firsov.study.Java.Telegram.Bot.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class QuestionsCounter {

    public QuestionsCounter(User user, Integer limit) {
        this.user = user;
        this.limit = limit;
        this.solved = 0;
        this.dropTime = getEndOfTheDayTime();
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer solved;

    public Integer getSolved() {
        updateSolved();
        return solved;
    }

    private void updateSolved() {
        if (dropTime == null) dropTime = getEndOfTheDayTime();
        if (LocalDateTime.now().isAfter(dropTime)) {
            this.solved = 0;
            this.dropTime = getEndOfTheDayTime();
        }
    }

    @Column(name="limit_value")
    private Integer limit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ", timezone = "UTC")
    private LocalDateTime dropTime;

    public void increaseCounter() {
        updateSolved();
        this.solved++;
    }

    private LocalDateTime getEndOfTheDayTime() {
        return LocalDate.now().atTime(23, 59, 59);
    }
}
