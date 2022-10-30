package ru.firsov.study.Java.Telegram.Bot;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = JavaTelegramBotApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(ResourceUtils.CLASSPATH_URL_PREFIX + "application-test.properties")
@ActiveProfiles("test")
@Testcontainers
@Sql({ResourceUtils.CLASSPATH_URL_PREFIX + "populateData.sql"})
@Transactional
public abstract class AbstractIntegrationTest {

}