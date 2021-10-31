package no.kristiania.Eksamen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class HttpServer {
    private ServerSocket serverSocket;

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClient).start();
    }

    private void handleClient() {
        try {
            Socket clientSocket = serverSocket.accept();

            String [] requestLine = HttpReader.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];

            String responseText = "File not found: " + requestTarget;

            String response = "HTTP/1.1 404 File not found\r\n" +
                    "Content-Length: "+ responseText.getBytes().length +"\r\n" +
                    "\r\n" +
                    responseText;

            if (requestTarget.equals("/hello")) {
                response = "HTTP/1.1 200 File OK\r\n" +
                        "Content-Length: 0\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n";
            } else {
                response = "HTTP/1.1 404 File not found\r\n" +
                        "Content-Length: "+ responseText.getBytes().length +"\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n" +
                        responseText;
            }

            clientSocket.getOutputStream().write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContentRoot(Path contentRoot) {
    }

}
