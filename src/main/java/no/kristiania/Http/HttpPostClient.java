package no.kristiania.Http;

import java.io.IOException;
import java.net.Socket;

public class HttpPostClient {
    private final HttpReader httpReader;
    private final int statusCode;

    public HttpPostClient(String host, int port, String requestTarget, String contentBody) throws IOException {
        Socket socket = new Socket(host, port);

        String request = "POST " + requestTarget + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Length: " + contentBody.length() +"\r\n" +
                    "\r\n" +
                    contentBody;

        socket.getOutputStream().write(request.getBytes());

        httpReader = new HttpReader(socket);
        String[] statusLine = httpReader.statusLine.split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessageBody() {
        return httpReader.messageBody;
    }

}
