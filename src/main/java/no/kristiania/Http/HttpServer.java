package no.kristiania.Http;

import no.kristiania.Controller.*;
import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;


public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private ServerSocket serverSocket;

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

        String[] requestLine = httpReader.startLine.split(" ");
        String requestTarget = requestLine[1];

        if (requestTarget.equals("/")) {
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
            if (fileResource != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                fileResource.transferTo(buffer);
                String responseText = buffer.toString();

                String contentType = "text/plain";
                if (requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                } else if (requestTarget.endsWith(".css")) {
                    contentType = "text/css";
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
        DataSource dataSource = createDataSource();
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(dataSource);
        CategoryDao categoryDao = new CategoryDao(dataSource);
        QuestionDao questionDao = new QuestionDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        HttpServer server = new HttpServer(10001);

        server.addController("/api/answerQuestionnaire", new AnswerQuestionnaireController(questionnaireDao, answerDao));
        server.addController("/api/deleteQuestion", new DeleteQuestionController(questionDao));
        server.addController("/api/editQuestion", new EditQuestionController(questionDao));
        server.addController("/api/listQuestions", new ListQuestionController(questionDao));
        server.addController("/api/listQuestionnaires", new ListQuestionnairesController(questionnaireDao));
        server.addController("/api/listCategories", new ListCategoriesController(categoryDao));
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));
        server.addController("/api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));
        server.addController("/api/showQuestionnaireQuestions", new ShowQuestionnaireQuestionsController(questionnaireDao, categoryDao, questionDao));
        logger.info("Starting http://localhost:{}/index.html", server.getPort());

    }


}
