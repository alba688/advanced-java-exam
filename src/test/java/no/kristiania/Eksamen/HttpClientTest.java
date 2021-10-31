package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpClientTest {

    @Test
    void shouldReturn200StatusCode() throws IOException {
        assertEquals(200, new HttpClient("httpbin.org", 80, "/html").getStatusCode());
    }

    @Test
    void shouldReadResponseHeaders() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertEquals("text/html; charset=utf-8", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReadContentLength() throws IOException {
        HttpClient client = new HttpClient ("httpbin.org", 80, "/html");
        assertEquals(3741, client.getContentLength());
    }
    @Test
    void shouldReadMessageBody() throws IOException {
        HttpClient client = new HttpClient("httpbin.org", 80, "/html");
        assertTrue(client.getMessageBody().startsWith("<!DOCTYPE html>\n<html>"),
                "expected <"+client.getMessageBody()+" to be html"
        );
    }
}