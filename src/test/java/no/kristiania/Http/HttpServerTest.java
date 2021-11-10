package no.kristiania.Http;

import no.kristiania.Controller.NewQuestionController;
import no.kristiania.Controller.NewQuestionnaireController;
import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.DaoTest.TestData;
import no.kristiania.Objects.Answer;
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
        String fileContent = "Content created at " + LocalTime.now();
        Files.write(Paths.get("target/test-classes/file.txt"), fileContent.getBytes());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/file.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnContentTypeBasedOnFileEnding() throws IOException {

        String fileContent = "<!DOCTYPE html><html><h1>Hello</h1></html>";
        Files.write(Paths.get("target/test-classes/file.html"), fileContent.getBytes());

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
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaire_id(1);
        questionnaire.setQuestionnaireTitle("Title");
        questionnaire.setQuestionnaireText("Text");
        questionnaireDao.save(questionnaire);

        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        // server.setQuestionDao(questionDao);
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "questionnaires=1&title=What+is+your+name%3F&values=5"
        );
        assertEquals(200, postClient.getStatusCode());
        Question question = questionDao.retrieve(1);
        assertEquals(1 ,question.getQuestionnaireId());
        assertEquals("What is your name?", question.getQuestionTitle());
    }

    @Test
    void shouldCreateNewQuestionnaire() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        server.addController("api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));

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


        //server.setQuestionnaireDao(questionnaireDao);

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/listQuestionnaires");
        assertEquals("<option value=\"1\">Matvaner</option><option value=\"2\">Sosiale Vaner</option>",
                client.getMessageBody());
    }

    @Test
    void shouldShowQuestionWithText() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaire.setQuestionnaireText("Text");

        questionnaireDao.save(questionnaire);


        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));

        Question question = new Question();
        question.setQuestionTitle("Do you like pizza?");
        question.setLowLabel("Not at all");
        question.setHighLabel("Love it");
        question.setNumberOfValues(5);
        question.setQuestionnaireId(1);
        questionDao.save(question);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals("<p>Do you like pizza?</p><form method=\"\" action=\"\"><label>Not at all</label><input value=\"0\"type=\"radio\" name=\"question1_answer\"></input><input value=\"1\"type=\"radio\" name=\"question1_answer\"></input><input value=\"2\"type=\"radio\" name=\"question1_answer\"></input><input value=\"3\"type=\"radio\" name=\"question1_answer\"></input><input value=\"4\"type=\"radio\" name=\"question1_answer\"></input><label>Love it</label></form>", client.getMessageBody());

    }

    @Test
    void shouldShowQuestionsWithSpecificQuestionnaire() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.addController("/api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Chosen questionnaire");
        questionnaire.setQuestionnaire_id(1);
        questionnaireDao.save(questionnaire);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setLowLabel("Low");
        question.setHighLabel("High");
        question.setNumberOfValues(5);
        question.setQuestionnaireId(questionnaire.getQuestionnaire_id());

        questionDao.save(question);


        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/showQuestionnaireQuestions", "questionnaires=1");
        assertEquals("<h1>Chosen questionnaire</h1><p>Question Title</p><form method=\"POST\" action=\"/api/answerQuestionnaire\"><label>Low</label><input value=\"1v1\"type=\"radio\" name=\"question0\"></input><input value=\"1v2\"type=\"radio\" name=\"question0\"></input><input value=\"1v3\"type=\"radio\" name=\"question0\"></input><input value=\"1v4\"type=\"radio\" name=\"question0\"></input><label>High</label><br><button value=\"Send\">Send</button></form>", postClient.getMessageBody());

    }

    @Test
    void shouldCreateAnswer() throws IOException, SQLException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Chosen questionnaire");

        server.addController("/api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));
        questionnaireDao.save(questionnaire);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setNumberOfValues(10);
        question.setQuestionnaireId(1);
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));
        questionDao.save(question);


        Answer answer = new Answer();

        answer.setAnswerValue(1);
        answer.setQuestionId(1);
        answerDao.save(answer);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/answerQuestionnaire",
                "value=1&name=1"
        );
        assertEquals(200, postClient.getStatusCode());
        assertEquals("Thank You", postClient.getMessageBody());


    }
}
