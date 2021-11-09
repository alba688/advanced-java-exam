package no.kristiania.Http;

import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.DaoTest.TestData;
import no.kristiania.Objects.Answer;
import no.kristiania.Objects.Category;
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
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");
        categoryDao.save(category);

        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);
        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "categories=1&title=What+is+your+name%3F&values=5"
        );
        assertEquals(200, postClient.getStatusCode());
        Question question = server.getQuestionDao().retrieve(1);
        assertEquals(1 ,question.getCategoryId());
        assertEquals("What is your name?", question.getQuestionTitle());

    }

    @Test
    void shouldCreateNewCategory() throws IOException, SQLException {
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        server.setCategoryDao(categoryDao);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newCategory",
                "questionnaire=1&title=categoryTitle&text=categoryText"
        );
        assertEquals(200, postClient.getStatusCode());

        Category category = categoryDao.retrieve(1);
        assertEquals("categoryTitle", category.getCategoryTitle());
        assertEquals("categoryText", category.getCategoryText());
    }

    @Test
    void shouldCreateNewQuestionnaire() throws SQLException, IOException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        server.setQuestionnaireDao(questionnaireDao);

        HttpPostClient postClient = new HttpPostClient(
                "localhost", server.getPort(), "api/newQuestionnaire", "title=questionnaireTitle&text=questionnaireText"
        );
        assertEquals(200, postClient.getStatusCode());

        Questionnaire questionnaire = questionnaireDao.retrieve(1);
        assertEquals("questionnaireTitle", questionnaire.getQuestionnaireTitle());
        assertEquals("questionnaireText", questionnaire.getQuestionnaireText());
    }

    @Test
    void shouldShowQuestionCategories() throws IOException, SQLException {
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        Category firstCategory = new Category();
        firstCategory.setCategoryTitle("Matvaner");
        Category secondCategory = new Category();
        secondCategory.setCategoryTitle("Sosiale Vaner");
        categoryDao.save(firstCategory);
        categoryDao.save(secondCategory);


        server.setCategoryDao(categoryDao);

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/listCategories");
        assertEquals("<option value=\"1\">Matvaner</option><option value=\"2\">Sosiale Vaner</option>",
                client.getMessageBody());
    }

    @Test
    void shouldShowQuestionWithText() throws IOException, SQLException {
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        Category category = new Category();
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");

        categoryDao.save(category);


        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setQuestionDao(questionDao);

        Question question = new Question();
        question.setQuestionTitle("Do you like pizza?");
        question.setLowLabel("Not at all");
        question.setHighLabel("Love it");
        question.setNumberOfValues(5);
        question.setCategoryId(1);
        server.getQuestionDao().save(question);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals("<p>Do you like pizza?</p><form method=\"\" action=\"\"><label>Not at all</label><input value=\"0\"type=\"radio\" name=\"question1_answer\"></input><input value=\"1\"type=\"radio\" name=\"question1_answer\"></input><input value=\"2\"type=\"radio\" name=\"question1_answer\"></input><input value=\"3\"type=\"radio\" name=\"question1_answer\"></input><input value=\"4\"type=\"radio\" name=\"question1_answer\"></input><label>Love it</label></form>", client.getMessageBody());

    }

    @Test
    void shouldShowQuestionsWithSpesificCategory() throws IOException, SQLException {
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.setCategoryDao(categoryDao);
        server.setQuestionDao(questionDao);
        Category category = new Category();
        category.setCategoryTitle("Chosen questionnaire");
        category.setCategoryId(1);
        categoryDao.save(category);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setLowLabel("Low");
        question.setHighLabel("High");
        question.setNumberOfValues(5);
        question.setCategoryId(category.getCategoryId());

        questionDao.save(question);


        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/showQuestionnaireQuestions", "questionnaires=1");
        assertEquals("<h1>Chosen questionnaire</h1><p>Question Title</p><form method=\"POST\" action=\"/api/answerQuestionnaire\"><label>Low</label><input value=\"1v1\"type=\"radio\" name=\"question0\"></input><input value=\"1v2\"type=\"radio\" name=\"question0\"></input><input value=\"1v3\"type=\"radio\" name=\"question0\"></input><input value=\"1v4\"type=\"radio\" name=\"question0\"></input><label>High</label><br><button value=\"Send\">Send</button></form>", postClient.getMessageBody());

    }

    @Test
    void shouldCreateAnswer() throws IOException, SQLException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());

        Category category = new Category();
        category.setCategoryTitle("Chosen questionnaire");

        server.setCategoryDao(categoryDao);
        categoryDao.save(category);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setNumberOfValues(10);
        question.setCategoryId(1);
        server.setQuestionDao(questionDao);
        questionDao.save(question);

        server.setAnswerDao(answerDao);
        Answer answer = new Answer();

        answer.setAnswerValue(1);
        answer.setQuestionId(1);

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
