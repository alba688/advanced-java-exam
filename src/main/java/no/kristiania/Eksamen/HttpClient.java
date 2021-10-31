package no.kristiania.Eksamen;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {
    private int statusCode;
    private final String messagebody;
    private final HashMap<String, String> headerFields = new HashMap<>();

    public HttpClient(String host, int port, String requestTarget ) throws IOException {

            Socket socket = new Socket(host, port);
            String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";
            socket.getOutputStream().write(request.getBytes());

            String statusLine = readLine(socket);
            this.statusCode = Integer.parseInt(statusLine.split(" ")[1]);

            String headerLine;

            while (!(headerLine = readLine(socket)).isBlank()) {
                int colonPos = headerLine.indexOf(':');
                String key = headerLine.substring(0, colonPos);
                String value = headerLine.substring(colonPos + 1).trim();
                headerFields.put(key, value);
            }

            this.messagebody = readBytes(socket.getInputStream(),getContentLength());
        }

    private String readBytes(InputStream in, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < contentLength ; i++) {
            result.append((char)in.read());
        }
        return result.toString();
    }

    private String readLine (Socket socket) throws IOException {
            StringBuilder line = new StringBuilder();
            int c;
            while ((c = socket.getInputStream().read()) != -1 && c != '\r') {
                line.append((char) c);
            }
            socket.getInputStream().read();
            return line.toString();
        }

            public int getStatusCode () {
                return statusCode;
            }

            public String getResponseHeader (String s){
                return headerFields.get(s);
            }

            public int getContentLength() {
                return Integer.parseInt(getResponseHeader("Content-Length"));
            }

            public String getMessageBody() {
            return messagebody;
            }
        }