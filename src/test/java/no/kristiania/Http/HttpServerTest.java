package no.kristiania.Http;

import no.kristiania.Controller.*;
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
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);
    CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
    QuestionDao questionDao = new QuestionDao(TestData.testDataSource());

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
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        Category category = new Category();
        category.setCategoryTitle("Title");
        category.setQuestionnaireId(1);
        categoryDao.save(category);

        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        Question question = new Question();
        question.setQuestionTitle("What is your name?");
        question.setNumberOfValues(5);
        question.setCategoryId(1);
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));


        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "categories=1&title=What+is+your+name%3F&values=5"
        );
        assertEquals(200, postClient.getStatusCode());
        assertEquals(1 ,question.getCategoryId());
        assertEquals("What is your name?", question.getQuestionTitle());
    }

    @Test
    void shouldCreateNewCategory() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire exampleQuestionnaire = new Questionnaire();
        exampleQuestionnaire.setQuestionnaireTitle("questionnaireTitle");
        exampleQuestionnaire.setQuestionnaireText("questionnaireText");

        questionnaireDao.save(exampleQuestionnaire);

        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());

        Category category = new Category();
        category.setCategoryTitle("categoryTitle");
        category.setCategoryText("categoryText");
        category.setQuestionnaireId(1);
        categoryDao.save(category);

        server.addController("/api/newCategory", new NewCategoryController(categoryDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newCategory",
                "questionnaire=1&title=categoryTitle&text=categoryText"
        );
        assertEquals(200, postClient.getStatusCode());
        assertEquals("categoryTitle", category.getCategoryTitle());
        assertEquals("categoryText", category.getCategoryText());
    }

    @Test
    void shouldCreateNewQuestionnaire() throws SQLException, IOException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("questionnaireTitle");
        questionnaire.setQuestionnaireText("questionnaireText");
        questionnaireDao.save(questionnaire);

        server.addController("/api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost", server.getPort(), "/api/newQuestionnaire", "title=questionnaireTitle&text=questionnaireText"
        );
        assertEquals(200, postClient.getStatusCode());
        assertEquals("questionnaireTitle", questionnaire.getQuestionnaireTitle());
        assertEquals("questionnaireText", questionnaire.getQuestionnaireText());
    }

    @Test
    void shouldShowQuestionnaires() throws SQLException, IOException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());



        Questionnaire firstQuestionnaire = new Questionnaire();
        firstQuestionnaire.setQuestionnaireTitle("Classes");
        Questionnaire secondQuestionnaire = new Questionnaire();
        secondQuestionnaire.setQuestionnaireTitle("Food");
        questionnaireDao.save(firstQuestionnaire);
        questionnaireDao.save(secondQuestionnaire);

        server.addController("/api/listQuestionnaires", new ListQuestionnairesController(questionnaireDao));
        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
            "/api/listQuestionnaires");
        assertEquals("<option value=\"1\">Classes</option><option value=\"2\">Food</option>",
                client.getMessageBody());

    }

    @Test
    void shouldShowQuestionCategories() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);


        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());

        Category category = new Category();
        category.setCategoryTitle("Matvaner");
        category.setCategoryText("text");
        category.setQuestionnaireId(1);
        categoryDao.save(category);

        Category category2 = new Category();
        category2.setCategoryTitle("Sosiale Vaner");
        category2.setCategoryText("text");
        category2.setQuestionnaireId(1);
        categoryDao.save(category2);


        server.addController("/api/listCategories", new ListCategoriesController(categoryDao));

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/listCategories");
        assertEquals("<option value=\"1\">Matvaner</option><option value=\"2\">Sosiale Vaner</option>",
                client.getMessageBody());
    }

    @Test
    void shouldShowQuestionsWithSpecificQuestionnaire() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("title");
        questionnaire.setQuestionnaireText("text");
        questionnaireDao.save(questionnaire);

        Category category = new Category();
        category.setCategoryTitle("Chosen questionnaire");
        category.setCategoryId(1);
        category.setQuestionnaireId(1);
        categoryDao.save(category);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setLowLabel("Low");
        question.setHighLabel("High");
        question.setNumberOfValues(5);
        question.setCategoryId(category.getCategoryId());

        questionDao.save(question);
        server.addController("/api/showQuestionnaireQuestions", new ShowQuestionnaireQuestionsController(questionnaireDao, categoryDao, questionDao));

        HttpPostClient postClient = new HttpPostClient("localhost", server.getPort(), "/api/showQuestionnaireQuestions", "questionnaires=1");
        assertEquals("<h1>title</h1><p>text</p><h2>Chosen questionnaire</h2><p>null</p><p>Question Title</p><form method=\"POST\" action=\"/api/answerQuestionnaire\"><label>Low</label><input value=\"1v1\"type=\"radio\" name=\"question0\"></input><input value=\"1v2\"type=\"radio\" name=\"question0\"></input><input value=\"1v3\"type=\"radio\" name=\"question0\"></input><input value=\"1v4\"type=\"radio\" name=\"question0\"></input><label>High</label><br><button value=\"Send\">Send</button></form>", postClient.getMessageBody());

    }

    @Test
    void shouldCreateAnswer() throws IOException, SQLException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Chosen questionnaire");
        questionnaireDao.save(questionnaire);

        Category category = new Category();
        category.setCategoryTitle("Chosen category");
        category.setQuestionnaireId(1);

        categoryDao.save(category);

        Question question = new Question();
        question.setQuestionTitle("Question Title");
        question.setNumberOfValues(10);
        question.setCategoryId(1);

        questionDao.save(question);

        Answer answer = new Answer();
        answer.setAnswerValue(1);
        answer.setQuestionId(1);
        server.addController("/api/answerQuestionnaire", new AnswerQuestionnaireController(questionnaireDao, answerDao));
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
