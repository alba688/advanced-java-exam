package no.kristiania.Eksamen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

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
            String fileTarget;
            fileTarget = requestTarget;

            if (requestTarget.equals("/hello")) {
                response = "HTTP/1.1 200 File OK\r\n" +
                        "Content-Length: 0\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n";
            } else {
                if(contentRoot !=  null && Files.exists(contentRoot.resolve(fileTarget.substring(1)))) {
                    responseText = Files.readString(contentRoot.resolve(fileTarget.substring(1)));

                    String contentType = "text/plain";
                    if (requestTarget.endsWith(".html")) {
                        contentType = "text/html";
                    }

                    response = "HTTP/1.1 200 OK\r\n"+
                            "Content-Length: "+responseText.getBytes().length + "\r\n" +
                            "Content-Type: " +contentType + "\r\n\r\n" +
                            responseText;
                    clientSocket.getOutputStream().write(response.getBytes());
                } else {
                    response = "HTTP/1.1 404 File not found\r\n" +
                            "Content-Length: " + responseText.getBytes().length + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "\r\n" +
                            responseText;
                }
            }

            clientSocket.getOutputStream().write(response.getBytes());

    }

    public void setContentRoot(Path contentRoot) {
        this.contentRoot = contentRoot;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
