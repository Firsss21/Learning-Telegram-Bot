package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Part;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.ChapterRepo;
import ru.firsov.study.Java.Telegram.Bot.common.repository.PartRepo;
import ru.firsov.study.Java.Telegram.Bot.common.repository.QuestionRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.firsov.study.Java.Telegram.Bot.common.BotState.RANDOM;

@Service
@AllArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepo questionRepo;
    private final PartRepo partRepo;
    private final ChapterRepo chapterRepo;

    public Question saveQuestion(Question question) {
        return questionRepo.save(question);
    }

    public List<Part> findAllParts(boolean withHidden) {
        if (withHidden)
            return partRepo.findAll();
        else
            return partRepo.findAllByHiddenIsFalse();
    }

    public Part findPartByPartName(String partName) {
        return partRepo.findByName(partName);
    }


    public Chapter findChapterByName(String messageText) {
        return chapterRepo.findByName(messageText);
    }

    public Optional<Chapter> findChapterById(long id) {
        return chapterRepo.findById(id);
    }

    public List<Chapter> findAllChaptersByPartId(Long selectedPartId, boolean withHidden) {
        if (withHidden) {
            return chapterRepo.findAllByPartId(selectedPartId);
        }  else {
            return chapterRepo.findAllByPartIdAndHiddenIsFalse(selectedPartId);
        }
    }

    @Cacheable(value = "chapter_questions")
    public List<Question> findAllQuestsByChapterId(Long id) {
        List<Question> allByChapterId = questionRepo.findAllByChapterId(id);
        if (allByChapterId == null) {
            return new ArrayList<>();
        } else {
            return allByChapterId;
        }
    }

    @Cacheable(value = "all_questions_count")
    public long getQuestionsCount() {
        return questionRepo.count();
    }

    @Cacheable(value = "all_questions")
    public List<Question> findAllQuestions() {
        List<Question> questions = new ArrayList<>();
        questionRepo.findAll().forEach(questions::add);
        return questions;
    }
    @Cacheable(value = "all_chapters")
    public List<Chapter> findAllChapters() {
        List<Chapter> chapters = new ArrayList<>();
        chapterRepo.findAll().forEach(chapters::add);
        return chapters;
    }
    @CacheEvict(allEntries = true, value = {"chapter_questions", "question", "all_questions", "all_chapters", "all_questions_count"})
    public void evictAllQuestions() {
    }

    @Cacheable(value = "question")
    public Question getQuestionById(Long id) {
        Optional<Question> byId = questionRepo.findById(id);
        return byId.orElse(null);
    }


    public Question getNextQuestion(User user) {

        if (user.getBotStateVariable().equals(RANDOM.name())) {
            if (user.getSelectedPartId() != 0) {
                return questionRepo.findRandomNotSolvedQuestionByPart(user.getId(), user.getSelectedPartId());
            }
            return questionRepo.findRandomNotSolvedQuestion(user.getId());
        }
        Question question = null;

        List<Question> allQuestsByChapterId = findAllQuestsByChapterId(user.getSelectedChapterId());
        Collections.shuffle(allQuestsByChapterId);

        if (user.getBotState() == BotState.LEARNING) {
            question = allQuestsByChapterId.size() > 0 ? allQuestsByChapterId.get(0) : null;
        } else {
            List<Question> availableQuestsForTesting = allQuestsByChapterId.stream().filter(e -> !user.getSolvedQuestions().contains(e.getId()) && !user.getSelectedQuestionId().equals(e.getId())).collect(Collectors.toList());
            question = availableQuestsForTesting.size() > 0 ? availableQuestsForTesting.get(0) : null;
        }

        return question;
    }

    public void removeQuestionById(Long id) {
        Question q = getQuestionById(id);
        if (q != null) {
           questionRepo.delete(q);
           evictAllQuestions();
        }
     }

    public void addChapter(String messageText, Long partId) {
        partRepo.findById(partId).ifPresent(part -> chapterRepo.save(new Chapter(messageText, part)));
    }
}
