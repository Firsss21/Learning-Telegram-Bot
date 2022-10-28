package ru.firsov.study.Java.Telegram.Bot;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(initializers = {UserRepositoryTCIntegrationTest.Initializer.class})
public class TestContainerTest {
//public class TestContainerTest extends UserRepositoryCommonIntegrationTests {

    @Rule
    public GenericContainer redis = new GenericContainer("redis:3.0.6")
            .withExposedPorts(6379);

    @Rule
    public PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6.2")
            .withUsername("admin")
            .withPassword("admin");
    @Test
    public void initialized() {}

    @Test
    public void testPsql() throws Exception {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        Connection conn = DriverManager
                .getConnection(jdbcUrl, username, password);
        ResultSet resultSet =
                conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();
        int result = resultSet.getInt(1);

        assertEquals(1, result);
    }
//    @ClassRule
//    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
//            .withDatabaseName("integration-tests-db")
//            .withUsername("sa")
//            .withPassword("sa");

//    static class Initializer
//            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues.of(
//                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
//                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
//                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
//            ).applyTo(configurableApplicationContext.getEnvironment());
//        }
//    }










//public class MockServerContainer extends BaseContainer<MockServerContainer> {
//    MockServerClient client;
//
//    public MockServerContainer() {
//        super("jamesdbloom/mockserver:latest");
//        withCommand("/opt/mockserver/run_mockserver.sh -logLevel INFO -serverPort 80");
//        addExposedPorts(80);
//    }
//
//    @Override
//    protected void containerIsStarted(InspectContainerResponse containerInfo) {
//        client = new MockServerClient(getContainerIpAddress(), getMappedPort(80));
//    }
}