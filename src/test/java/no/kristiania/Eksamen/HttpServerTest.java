package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReturn404UnknownRequestTarget() throws IOException {
        HttpServer server = new HttpServer(10000);
        HttpClient client = new HttpClient("localhost", 10000, "/unknown");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldEchoRequestTarget() throws IOException {
        HttpServer server = new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/unknown");
        assertEquals(404, client.getStatusCode());
        assertEquals("File not found: /unknown", client.getMessageBody());
    }

    @Test
    void shouldReturn200Response() throws IOException {
        HttpServer server = new HttpServer(10002);
        HttpClient client = new HttpClient("localhost", 10002, "/hello");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnContentType() throws IOException {
        HttpServer server = new HttpServer(10003);
        HttpClient client = new HttpClient("localhost", 10003, "/hello");
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldRespondWithFileOnDisk() throws IOException {
        Path contentRoot = Paths.get("target/test-classes");
        String fileContent = "Content created at " + LocalTime.now();
        Files.writeString(contentRoot.resolve("file.txt"), fileContent);

        HttpServer server = new HttpServer(10004);
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", 10004, "/file.txt");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturnContentTypeBasedOnFileEnding() throws IOException {
        Path contentRoot = Paths.get("target/test-classes");

        String fileContent = "<!DOCTYPE html><html><h1>Hello</h1></html>";

        Files.writeString(contentRoot.resolve("file.html"), fileContent);

        HttpServer server = new HttpServer(10005);
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", 10005, "/file.html");
        assertEquals(fileContent, client.getMessageBody());
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }
}
