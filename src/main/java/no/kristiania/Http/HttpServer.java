package no.kristiania.Http;

import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Answer;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class HttpServer {
    private ServerSocket serverSocket;
    private Path contentRoot;
    private QuestionDao questionDao;
    private CategoryDao categoryDao;
    private AnswerDao answerDao;
    private QuestionnaireDao questionnaireDao;

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

        String response;


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
        String responseText = "Hello " +yourName;
        write200OKResponse(responseText, "text/plain", clientSocket);

        } else {
            if(contentRoot !=  null && Files.exists(contentRoot.resolve(fileTarget.substring(1)))) {
                String responseText = Files.readString(contentRoot.resolve(fileTarget.substring(1)));
                String contentType;

                if (requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                } else if (requestTarget.endsWith(".css")) {
                    contentType = "text/css";
                } else {
                    contentType = "text/plain";
                }
                write200OKResponse(responseText, contentType, clientSocket);

            } else if (fileTarget.equals("/api/questions")) {
                String responseText = "";
                for (Question question : questionDao.listAll()) {

                    responseText +=
                            "<p>" + question.getQuestionTitle() +
                            "</p>" +
                            "<form method=\"\" action=\"\"><label>" + question.getLowLabel() +"</label>";

                            for (int i=0; i < question.getNumberOfValues(); i++){
                                responseText += "<input value=\"" + i + "\"" + "type=\"radio\" name=\"question" + question.getQuestionId() + "_answer\"></input>";
                            }

                    responseText +="<label>" + question.getHighLabel() + "</label>" +
                                    "</form>";
                }
                write200OKResponse(responseText, "text/html", clientSocket);


            } else if (fileTarget.equals("/api/listQuestions")) {
                String responseText = "";

                for (Question question : questionDao.listAll()) {
                    responseText += "<option value=\""+ question.getQuestionId() +"\">"+ question.getQuestionTitle() +"</option>";
                }
                write200OKResponse(responseText, "text/html", clientSocket);

            }  else if (fileTarget.equals("/api/deleteQuestion")){

                // retrieves question to be deleted
                Question question = questionDao.retrieve(Integer.parseInt((parseRequestParameters(httpReader.messageBody)).get("questions")));

                questionDao.delete(question.getQuestionId());

                write200OKResponse("Question deleted", "text/plain", clientSocket);

            } else if (fileTarget.equals("/api/editQuestion")){

                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);

                // retrieves the questionId to be edited
                Question question = questionDao.retrieve(Integer.parseInt(queryMap.get("questions")));

                // updates data using setters
                question.setQuestionTitle(queryMap.get("title"));
                question.setLowLabel(queryMap.get("low_label"));
                question.setHighLabel(queryMap.get("high_label"));
                int numberOfValues = Integer.parseInt(queryMap.get("values"));
                question.setNumberOfValues(numberOfValues);

                // sends updated question to edit method to deploy sql statement
                questionDao.edit(question);

                write200OKResponse("Edit complete", "text/plain", clientSocket);

            } else if (fileTarget.equals("/api/showQuestionnaireQuestions")){
                String responseTxt = "";
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);

                Category category = categoryDao.retrieve(Integer.parseInt(queryMap.get("questionnaires")));

                responseTxt = "<h1>" + category.getCategoryTitle() + "</h1>";
                int j = 0;
                for (Question question : questionDao.listAllWithParameter(category.getCategoryId())) {
                    responseTxt += "<p>" + question.getQuestionTitle() +
                            "</p>" +
                            "<form method=\"POST\" action=\"/api/answerQuestionnaire\"><label>" + question.getLowLabel() + "</label>";


                    for (int i = 1; i < question.getNumberOfValues(); i++) {
                        responseTxt += "<input value=\"" + question.getQuestionId() + "v" + i +"\"" + "type=\"radio\" name=\"question"+j+"\"></input>";
                    }
                    j++;
                    responseTxt += "<label>" + question.getHighLabel() + "</label><br>";

                    }

                responseTxt += "<button value=\"Send\">Send</button></form>";
                write200OKResponse(responseTxt, "text/html", clientSocket);



            } else if (fileTarget.equals("/api/answerQuestionnaire")) {
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Answer answer = new Answer();

                for (int i = 0; i < queryMap.size(); i++) {
                    if(queryMap.get("question"+i) != null) {
                        String buffer = queryMap.get("question" + i);
                        int valuePos = buffer.indexOf('v');
                        int questionId = Integer.parseInt(buffer.substring(0, valuePos));
                        int answerValue = Integer.parseInt(buffer.substring(valuePos+1));

                        answer.setQuestionId(questionId);
                        answer.setAnswerValue(answerValue);

                        answerDao.save(answer);
                    }
                }
                write200OKResponse("Thank You", "text/plain", clientSocket);



            } else if (fileTarget.equals("/api/listCategories")) {
                String responseText = "";
                for (Category category : categoryDao.listAll()) {
                    responseText += "<option value=\"" + category.getCategoryId() + "\">" + category.getCategoryTitle() + "</option>";
                }
                write200OKResponse(responseText, "text/html", clientSocket);
            } else if (fileTarget.equals("api/listQuestionnaires")) {



            } else if (fileTarget.equals("/api/newQuestion")) {
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Question question = new Question();

                int categoryId = Integer.parseInt(queryMap.get("categories"));
                question.setCategoryId(categoryId);
                question.setQuestionTitle(queryMap.get("title"));
                question.setLowLabel(queryMap.get("low_label"));
                question.setHighLabel(queryMap.get("high_label"));
                int numberOfValues = Integer.parseInt(queryMap.get("values"));
                question.setNumberOfValues(numberOfValues);

                questionDao.save(question);
                write200OKResponse("Question added", "text/plain", clientSocket);

            }
            else if (fileTarget.equals("/api/newCategory")){
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Category category = new Category();
                int questionnaireID = Integer.parseInt(queryMap.get("questionnaire"));
                category.setQuestionnaireId(questionnaireID);
                category.setCategoryTitle(queryMap.get("title"));
                category.setCategoryText(queryMap.get("text"));
                categoryDao.save(category);
                write200OKResponse("Category created", "text/plain", clientSocket);

            } else if (fileTarget.equals("api/newQuestionnaire")) {
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setQuestionnaireTitle(queryMap.get("title"));
                questionnaire.setQuestionnaireText(queryMap.get("text"));
                questionnaireDao.save(questionnaire);
                write200OKResponse("Questionnaire created", "text/plain", clientSocket);

            } else {
                String responseText = "File not found: " + requestTarget;
                response = "HTTP/1.1 404 File not found\r\n" +
                            "Content-Length: " + responseText.getBytes().length + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Connection: close\r\n" +
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
            String parameterName  = queryParameter.substring(0, equalPos);
            String parameterValue = URLDecoder.decode(queryParameter.substring(equalPos+1), StandardCharsets.UTF_8);
            queryMap.put(parameterName,parameterValue);
        }
        return queryMap;

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

    public void setContentRoot(Path contentRoot) {
        this.contentRoot = contentRoot;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public void setAnswerDao(AnswerDao answerDao) {
        this.answerDao = answerDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public void setQuestionDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public void setQuestionnaireDao(QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
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

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10001);
        server.setContentRoot(Paths.get("src/main/resources"));
        server.setCategoryDao(new CategoryDao(createDataSource()));
        server.setQuestionDao(new QuestionDao(createDataSource()));
        server.setAnswerDao(new AnswerDao(createDataSource()));

    }


}
