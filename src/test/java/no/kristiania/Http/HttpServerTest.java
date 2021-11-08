package no.kristiania.Http;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.DaoTest.TestData;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
    }

    @BeforeEach
    void setup() {
        Flyway.configure().dataSource(TestData.testDataSource()).load().clean();
    }

    @Test
    void shouldReturn404UnknownRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/unknown");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldEchoRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/unknown");
        assertEquals(404, client.getStatusCode());
        assertEquals("File not found: /unknown", client.getMessageBody());
    }

    @Test
    void shouldReturn200Response() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnContentType() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldRespondWithFileOnDisk() throws IOException {
        Path contentRoot = Paths.get("target/test-classes");
        String fileContent = "Content created at " + LocalTime.now();
        Files.writeString(contentRoot.resolve("file.txt"), fileContent);

        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/file.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnContentTypeBasedOnFileEnding() throws IOException {
        Path contentRoot = Paths.get("target/test-classes");

        String fileContent = "<!DOCTYPE html><html><h1>Hello</h1></html>";

        Files.writeString(contentRoot.resolve("file.html"), fileContent);

        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/file.html");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldEchoQueryParameters() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/hello?firstName=Test&lastName=Tester");
        assertEquals("Hello Test Tester", client.getMessageBody());

    }


    @Test
    void shouldCreateNewQuestion() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);
        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "questionnaires=1&title=What+is+your+name%3F&values=5"
        );
        assertEquals(200, postClient.getStatusCode());
        Question question = server.getQuestionDao().retrieve(1);
        assertEquals(0 ,question.getQuestionnaireId());
        assertEquals("What is your name?", question.getQuestionTitle());

    }

    @Test
    void shouldCreateNewQuestionnaire() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        server.setQuestionnaireDao(questionnaireDao);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestionnaire",
                "title=questionnaireTitle&text=questionnaireText"
        );
        assertEquals(200, postClient.getStatusCode());

        Questionnaire questionnaire = questionnaireDao.retrieve(1);
        assertEquals("questionnaireTitle", questionnaire.getQuestionnaireTitle());
        assertEquals("questionnaireText", questionnaire.getQuestionnaireText());
    }

    @Test
    void shouldShowQuestionOptions() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire firstQuestionnaire = new Questionnaire();
        firstQuestionnaire.setQuestionnaireTitle("Matvaner");
        Questionnaire secondQuestionnaire = new Questionnaire();
        secondQuestionnaire.setQuestionnaireTitle("Sosiale Vaner");
        questionnaireDao.save(firstQuestionnaire);
        questionnaireDao.save(secondQuestionnaire);


        server.setQuestionnaireDao(questionnaireDao);

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/listQuestionnaires");
        assertEquals("<option value=\"1\">Matvaner</option><option value=\"2\">Sosiale Vaner</option>",
                client.getMessageBody());
    }

    @Test
    void shouldShowQuestionWithText() throws IOException, SQLException {
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);

        Question question = new Question();
        question.setQuestionTitle("Do you like pizza?");
        question.setLowLabel("Not at all");
        question.setHighLabel("Love it");
        question.setNumberOfValues(5);
        server.getQuestionDao().save(question);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals("<p>Do you like pizza?</p><form method=\"\" action=\"\"><label>Not at all</label><input value=\"0\"type=\"radio\" name=\"question1_answer\"></input><input value=\"1\"type=\"radio\" name=\"question1_answer\"></input><input value=\"2\"type=\"radio\" name=\"question1_answer\"></input><input value=\"3\"type=\"radio\" name=\"question1_answer\"></input><input value=\"4\"type=\"radio\" name=\"question1_answer\"></input><label>Love it</label></form>", client.getMessageBody());

    }

}
