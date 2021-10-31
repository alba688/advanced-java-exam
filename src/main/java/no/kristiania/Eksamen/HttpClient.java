package no.kristiania.Eksamen;

public class HttpClient {
    private String host;
    private int port;
    private String requestTarget;

    public HttpClient(String host, int port, String requestTarget ) {

        this.host = host;
        this.port = port;
        this.requestTarget = requestTarget;
    }

    public int getStatusCode() {
        return 200;
    }

    public String getResponseHeader(String s) {
        return " ";
    }
}