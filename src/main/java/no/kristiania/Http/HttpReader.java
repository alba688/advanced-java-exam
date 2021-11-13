package no.kristiania.Http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpReader {
    public String startLine;
    public final Map<String, String> headerFields = new HashMap<>();
    public String messageBody;
    public byte[] messageBodyInBytes;


    public HttpReader(Socket socket) throws IOException {
        startLine = HttpReader.readLine(socket);
        readHeader(socket);

        if (headerFields.containsKey("Content-Length")) {
            messageBody = HttpReader.readBytes(socket, getContentLength());
        }
    }

   public HttpReader(String startLine, String messageBody) {
        this.startLine = startLine;
        this.messageBody = messageBody;
    }
    public HttpReader(String startLine, byte[] messageBody, String... headers) {
        this.startLine = startLine;
        this.messageBodyInBytes = messageBody;
        for (String headerfield : headers) {
            int colonPos = headerfield.indexOf(':');
            String headerKey = headerfield.substring(0, colonPos);
            String headerValue = headerfield.substring(colonPos+1).trim();
            headerFields.put(headerKey, headerValue);
        }
    }

    public HttpReader(String startLine, String messageBody, String... headers) {
        this.startLine = startLine;
        this.messageBody = messageBody;
        for (String headerfield : headers) {
            int colonPos = headerfield.indexOf(':');
            String headerKey = headerfield.substring(0, colonPos);
            String headerValue = headerfield.substring(colonPos+1).trim();
            headerFields.put(headerKey, headerValue);
        }
    }

    static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            buffer.append((char)c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return buffer.toString();
    }

    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            line.append((char)socket.getInputStream().read());
        }
        return line.toString();
    }


    private void readHeader(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = HttpReader.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();
            headerFields.put(headerField, headerValue);
        }
    }

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0, equalsPos);
            String parameterValue = URLDecoder.decode(queryParameter.substring(equalsPos+1), StandardCharsets.UTF_8);
            queryMap.put(parameterName, parameterValue);
        }
        return queryMap;
    }

    public String getResponseHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getResponseHeader("Content-Length"));
    }

    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n";
            if (messageBody == null) {
                response += "Content-Length: " + messageBodyInBytes.length + "\r\n";
            } else {
                response += "Content-Length: " + messageBody.length() + "\r\n";
            }

            for (Map.Entry <String,String> header : headerFields.entrySet()) {
                response += header.getKey() + ":" + header.getValue() + "\r\n";
            }

            response += "Connection: close\r\n\r\n";
            if(messageBody == null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(response.getBytes());
                outputStream.write(messageBodyInBytes);
                socket.getOutputStream().write(outputStream.toByteArray());
            } else {
                response += messageBody;
                socket.getOutputStream().write(response.getBytes());
            }

    }
}
