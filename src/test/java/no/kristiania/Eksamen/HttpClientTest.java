package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpClientTest {
    HttpClient client = new HttpClient("httpbin.org", 80, "/html");

    public HttpClientTest() throws IOException {
    }

    @Test
    void shouldReturn200StatusCode() throws IOException {
        assertEquals(200, new HttpClient("httpbin.org", 80, "/html").getStatusCode());
    }

    @Test
    void shouldReadResponseHeaders() {

        assertEquals("text/html; charset=utf-8", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReadContentLength()  {
        assertEquals(3741, client.getContentLength());
    }
    @Test
    void shouldReadMessageBody()  {
        assertTrue(client.getMessageBody().startsWith("<!DOCTYPE html>\n<html>"),
                "expected <"+client.getMessageBody()+" to be html"
        );
    }
}