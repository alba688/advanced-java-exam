package no.kristiania.Http;


import no.kristiania.Controller.*;
import no.kristiania.Dao.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.FileReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;


public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private ServerSocket serverSocket;
    private HashMap<String, HttpController> controllers = new HashMap<>();


    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

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
            logger.warn("No Connection for socket");
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {

        Socket clientSocket = serverSocket.accept();
        HttpReader httpReader = new HttpReader(clientSocket);
        String[] requestLine = httpReader.startLine.split(" ");
        String fileTarget = requestLine[1];

        if (controllers.containsKey(fileTarget)) {
            try {
             HttpReader readerResponse = controllers.get(fileTarget).handle(httpReader);
             readerResponse.write(clientSocket);
            } catch (SQLException sql) {
                logger.warn("SQL is missing or invalid");
                write500Code(clientSocket);
            }
        } else {
            try {
                HttpReader readerResponse = new FileController().handle(httpReader);
                readerResponse.write(clientSocket);
            } catch (IOException ioe) {
                logger.warn("Error reading file from server");
                write500Code(clientSocket);
            }
        }
        return;
    }

    private void write500Code(Socket clientSocket) throws IOException {
        String response = "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Length: 20\r\n" +
                "Connection: close\r\n\r\n"
                + "Internal Server Error";
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
        PersonDao personDao = new PersonDao(dataSource);
        HttpServer server = new HttpServer(10001);
        server.addController("/api/answerQuestionnaire", new AnswerQuestionnaireController(questionnaireDao, answerDao));
        server.addController("/api/deleteQuestion", new DeleteQuestionController(questionDao));
        server.addController("/api/editQuestion", new EditQuestionController(questionDao));
        server.addController("/api/listQuestions", new ListQuestionController(questionDao));
        server.addController("/api/listQuestionnaires", new ListQuestionnairesController(questionnaireDao));
        server.addController("/api/listCategories", new ListCategoriesController(categoryDao));
        server.addController("/api/newCategory", new NewCategoryController(categoryDao));
        server.addController("/api/newQuestion", new NewQuestionController(questionDao));
        server.addController("/api/newQuestionnaire", new NewQuestionnaireController(questionnaireDao));
        server.addController("/api/showQuestionnaireQuestions", new ShowQuestionnaireQuestionsController(questionnaireDao, categoryDao, questionDao));
        server.addController("/api/savePerson", new SavePersonController(personDao));
        server.addController("/api/userInput", new UserInputController(personDao));
        server.addController("/api/showAnswers", new ShowAnswersController(questionnaireDao, categoryDao, questionDao, answerDao));
        logger.info("Starting http://localhost:{}/index.html", server.getPort());

    }


}
