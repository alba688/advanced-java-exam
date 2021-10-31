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
}
