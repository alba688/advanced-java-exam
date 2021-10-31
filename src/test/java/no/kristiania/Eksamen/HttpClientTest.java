package no.kristiania.Eksamen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpClientTest {

    @Test
    void shouldReturn200StatusCode() {
        assertEquals(200, new HttpClient("httpbin.org", 80, "/html").getStatusCode());
    }
}