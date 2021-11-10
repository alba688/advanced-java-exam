package no.kristiania.Http;

import no.kristiania.Controller.HttpController;
import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.QuestionDao; //eventually remove after Controller in place
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Answer;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path; // eventually remove after Controller in place
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


public class HttpServer {
    private ServerSocket serverSocket;
    // private Path contentRoot; // eventually remove after Controller in place
    //private QuestionDao questionDao; // eventually remove after Controller in place
    //private QuestionnaireDao questionnaireDao;
    //private AnswerDao answerDao;
    private HashMap<String, HttpController> controllers = new HashMap<>();

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
        } catch (SQLException sqlException) {
            System.out.println("No SQL connection");
            sqlException.printStackTrace();
        }
    }

    private void handleClient() throws IOException, SQLException {
        Socket clientSocket = serverSocket.accept();

        HttpReader httpReader = new HttpReader(clientSocket);

        String[] requestLine = httpReader.statusLine.split(" ");
        String requestTarget = requestLine[1];

            if(requestTarget.equals("/")) {
                requestTarget = "/index.html";
            }

        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        String query = null;

        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos+1);
        } else {
            fileTarget = requestTarget;
        }

        if (controllers.containsKey(fileTarget)) {
            HttpReader readerResponse = controllers.get(fileTarget).handle(httpReader);
            readerResponse.write(clientSocket);
            return;
        }

        if (fileTarget.equals("/hello")) {
            String yourName = "world";

            if (query != null) {
                Map<String, String> queryMap = HttpReader.parseRequestParameters(query);
                yourName = queryMap.get("firstName") + " " + queryMap.get("lastName");
            }
        String responseText = "Hello " +yourName;
        write200OKResponse(responseText, "text/plain", clientSocket);

        } else {
            InputStream fileResource = getClass().getResourceAsStream(fileTarget);
            if(fileResource !=  null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                fileResource.transferTo(buffer);
                String responseText = buffer.toString();

                String contentType;
                if (requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                } else if (requestTarget.endsWith(".css")) {
                    contentType = "text/css";
                } else {
                    contentType = "text/plain";
                }
                write200OKResponse(responseText, contentType, clientSocket);
                return;
            }

            String responseText = "File not found: " + requestTarget;

            String response = "HTTP/1.1 404 File not found\r\n" +
                            "Content-Length: " + responseText.getBytes().length + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            responseText;
            clientSocket.getOutputStream().write(response.getBytes());
        }
    }

    private void write200OKResponse(String responseText, String contentType, Socket clientSocket) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n"+
                "Content-Length: "+ responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close"+ "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url", "jdbc:postgresql://localhost:5432/questionnaire_db"));
        dataSource.setUser(properties.getProperty("dataSource.user", "questionnaire_dbuser"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10001);
        new QuestionDao(createDataSource());
        new QuestionnaireDao(createDataSource());
        new AnswerDao(createDataSource());
    }


}
