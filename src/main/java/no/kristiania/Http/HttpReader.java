package no.kristiania.Http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpReader {
    public String statusLine;
    public String messageBody;
    public Map<String, String> headerFields = new HashMap<>();

    public HttpReader(Socket socket) throws IOException {
        statusLine = HttpReader.readLine(socket);
        readHeader(socket);
        if (headerFields.containsKey("Content-Length")) {
            messageBody = HttpReader.readBytes(socket, getContentLength());
        }
    }

    static String readLine(Socket socket) throws IOException {

        StringBuilder line = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            line.append((char) c);
        }
        int expectedNewLine = socket.getInputStream().read();
        assert expectedNewLine == '\n';
        return line.toString();
    }

    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < contentLength ; i++) {
            result.append((char)socket.getInputStream().read());
        }
        return result.toString();
    }

    private void readHeader(Socket socket) throws IOException {
        String headerLine;

        while (!(headerLine = HttpReader.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos);
            String value = headerLine.substring(colonPos + 1).trim();
            headerFields.put(key, value);
        }
    }

    public String getResponseHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getResponseHeader("Content-Length"));
    }
}
