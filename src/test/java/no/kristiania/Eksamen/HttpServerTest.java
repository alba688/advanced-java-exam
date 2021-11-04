package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
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
    void shouldShowQuestionOptions() throws IOException {
        server.setListOfQuestionnaires(List.of("Matvaner", "Sosiale vaner"));

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/listQuestionnaires");
        assertEquals("<option value=1>Matvaner</option><option value=2>Sosiale vaner</option>",
                client.getMessageBody());
    }

    @Test
    void shouldCreateNewQuestion() throws IOException {
        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestion",
                "title=What+is+your+name%3F&"
        );
        assertEquals(200, postClient.getStatusCode());
        Question question = server.getQuestion().get(0);
        assertEquals("What is your name?", question.getQuestionTitle());

    }

    @Test
    void shouldShowQuestionWithText() throws IOException {
        Question question = new Question();
        question.setQuestionTitle("Do you like pizza?");
        question.setQuestionText("Choose between 1 and 5");
        question.setLowLabel("Not at all");
        question.setHighLabel("Love it");
        server.getQuestion().add(question);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertEquals("<p>Do you like pizza? Choose between 1 and 5</p><form><label>Not at all<input type=\"radio\" name=\"question_answer\"></input></label><input type=\"radio\" name=\"question_answer\"></input><input type=\"radio\" name=\"question_answer\"></input><input type=\"radio\" name=\"question_answer\"></input><input type=\"radio\"name=\"question_answer\"></input><label>Love it</label></form>", client.getMessageBody());

    }
}
