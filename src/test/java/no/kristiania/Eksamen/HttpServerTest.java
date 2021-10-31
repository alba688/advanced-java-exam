package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        assertEquals("plain/text", client.getResponseHeader("Content-Type"));
    }
}
