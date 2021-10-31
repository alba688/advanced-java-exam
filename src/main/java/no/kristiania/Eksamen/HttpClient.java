package no.kristiania.Eksamen;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private final int statusCode;

    public HttpClient(String host, int port, String requestTarget ) throws IOException {

            Socket socket = new Socket(host, port);
            String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n\r\n";
            socket.getOutputStream().write(request.getBytes());

            String statusLine = readLine(socket);
            this.statusCode = Integer.parseInt(statusLine.split(" ")[1]);
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
                return " ";
            }
        };