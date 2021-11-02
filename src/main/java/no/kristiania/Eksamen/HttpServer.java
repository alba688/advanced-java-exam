package no.kristiania.Eksamen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {
    private ServerSocket serverSocket;
    private Path contentRoot;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try {
            while (true) {
                handleClient();
            }
        } catch (IOException e) {
            System.out.println("No Connection for socket");
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {
            Socket clientSocket = serverSocket.accept();

            String [] requestLine = HttpReader.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];

            String response;
            String responseText = "File not found: " + requestTarget;

            int questionPos = requestTarget.indexOf('?');
            String fileTarget;
            String query = null;

            if (questionPos != -1) {
                fileTarget = requestTarget.substring(0, questionPos);
                query = requestTarget.substring(questionPos+1);
            } else {
                fileTarget = requestTarget;
            }


            if (fileTarget.equals("/hello")) {
                String yourName = "world";

                if (query != null) {

                Map<String, String> queryMap = parseRequestParameters(query);
                yourName = queryMap.get("firstName") + " " + queryMap.get("lastName");
                }

                responseText = "Hello " +yourName;
                write200OKResponse(responseText, "text/plain", clientSocket);


            } else {
                if(contentRoot !=  null && Files.exists(contentRoot.resolve(fileTarget.substring(1)))) {
                    responseText = Files.readString(contentRoot.resolve(fileTarget.substring(1)));

                    String contentType;

                    if (requestTarget.endsWith(".html")) {
                        contentType = "text/html";
                    } else if (requestTarget.endsWith(".css")) {
                        contentType = "text/css";
                    } else {
                        contentType = "text/plain";
                    }

                    write200OKResponse(responseText, contentType, clientSocket);

                } else {
                    response = "HTTP/1.1 404 File not found\r\n" +
                            "Content-Length: " + responseText.getBytes().length + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "\r\n" +
                            responseText;
                    clientSocket.getOutputStream().write(response.getBytes());
                }
            }



    }

    private Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalPos = queryParameter.indexOf("=");
            String parameterName  =queryParameter.substring(0, equalPos);
            String parameterValue = queryParameter.substring(equalPos+1);
            queryMap.put(parameterName,parameterValue);
        }
        return queryMap;

    }

    private void write200OKResponse(String responseText, String contentType, Socket clientSocket) throws IOException {
        String response;
        response = "HTTP/1.1 200 OK\r\n"+
                "Content-Length: "+ responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());

    }

    public void setContentRoot(Path contentRoot) {
        this.contentRoot = contentRoot;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setQuestionOptions(List<String> options) {
    }
}
