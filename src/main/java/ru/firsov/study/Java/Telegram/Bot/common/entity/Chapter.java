package ru.firsov.study.Java.Telegram.Bot.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;

@Entity @Data
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chapter")
    private List<Question> question;

    public List<Question> getQuestion() {
        Hibernate.initialize(question);
        return question;
    }
}
