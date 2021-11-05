package no.kristiania.Http;

import java.io.IOException;
import java.net.Socket;


public class HttpClient {
    private HttpReader httpReader;
    private int statusCode;


    public HttpClient(String host, int port, String requestTarget ) throws IOException {
        Socket socket = new Socket(host, port);
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
        socket.getOutputStream().write(request.getBytes());

        httpReader = new HttpReader(socket);

        String[] statusLine = httpReader.statusLine.split(" ");
        this.statusCode = Integer.parseInt(statusLine[1]);
    }

    public int getStatusCode () {
        return statusCode;
    }

    public String getResponseHeader (String s){
        return httpReader.headerFields.get(s);
    }

    public int getContentLength() {
        return Integer.parseInt(getResponseHeader("Content-Length"));
    }

    public String getMessageBody() {
        return httpReader.messageBody;
    }
}