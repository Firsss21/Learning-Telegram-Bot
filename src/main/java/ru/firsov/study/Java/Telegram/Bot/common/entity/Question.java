package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition="TEXT")
    private String question;

    @Column(columnDefinition="TEXT")
    private String answer;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    public Question(String question, String answer, Chapter chapter) {
        this.question = question;
        this.answer = answer;
        this.chapter = chapter;
    }
    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
