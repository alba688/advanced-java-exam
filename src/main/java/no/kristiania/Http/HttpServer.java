package no.kristiania.Http;

import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {
    private ServerSocket serverSocket;
    private Path contentRoot;
    private List<Questionnaire> questionnaires = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

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

        HttpReader httpReader = new HttpReader(clientSocket);

        String[] requestLine = httpReader.statusLine.split(" ");
        String requestTarget = requestLine[1];

        if(requestTarget.equals("/")) {
            requestTarget = "/index.html";
        }

        String response;
        String responseText = "File not found: " + requestTarget;

        // Handle queries inside of request target
        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        String query = null;

        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos+1);
        } else {
            fileTarget = requestTarget;
        }

        // tests specific request target
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

            } else if (fileTarget.equals("/api/questions")) {
                responseText = "";
                for (Question question : questions) {
                    responseText +=
                            "<p>" + question.getQuestionTitle() +
                            " " + question.getQuestionText() + "</p>" +
                    "<form><label>" + question.getLowLabel() + "<input type=\"radio\" name=\"question_answer\"></input></label>" +
                                    "<input type=\"radio\" name=\"question_answer\"></input>" +
                                    "<input type=\"radio\" name=\"question_answer\"></input>" +
                                    "<input type=\"radio\" name=\"question_answer\"></input>" +
                    "<input type=\"radio\"name=\"question_answer\"></input>" + "<label>" + question.getHighLabel() + "</label>" +
                                    "</form>";
                }
                write200OKResponse(responseText, "text/html", clientSocket);

            } else if (fileTarget.equals("/api/listQuestionnaires")) {
                responseText = "";
                int value = 1;
                for (Questionnaire questionnaire : questionnaires) {
                    responseText += "<option value=\""+(value++)+"\">"+ questionnaire.getQuestionnaireTitle() +"</option>";
                }
                write200OKResponse(responseText, "text/html", clientSocket);

            } else if (fileTarget.equals("/api/newQuestion")) {
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Question question = new Question();
                // should these be questionText and questionTitle ??

                int questionnaireID = Integer.parseInt(queryMap.get("questionnaires"));
                question.setQuestionnaireId(questionnaireID);
                question.setQuestionTitle(queryMap.get("title"));
                question.setQuestionText(queryMap.get("text"));
                question.setLowLabel(queryMap.get("low_label"));
                question.setHighLabel(queryMap.get("high_label"));
                questions.add(question);
                write200OKResponse("Question added","text/plain",clientSocket);

            } else if (fileTarget.equals("/api/newQuestionnaire")){
                Map<String, String> queryMap = parseRequestParameters(httpReader.messageBody);
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setQuestionnaireTitle(queryMap.get("title"));
                questionnaire.setQuestionnaireText(queryMap.get("text"));
                questionnaires.add(questionnaire);
                write200OKResponse("Questionnaire created", "text/plain", clientSocket);

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
            String parameterValue = URLDecoder.decode(queryParameter.substring(equalPos+1), StandardCharsets.UTF_8);
            queryMap.put(parameterName,parameterValue);
        }
        return queryMap;

    }

    private void write200OKResponse(String responseText, String contentType, Socket clientSocket) throws IOException {
        String response;
        response = "HTTP/1.1 200 OK\r\n"+
                "Content-Length: "+ responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close"+ "\r\n\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());

    }

    public void setContentRoot(Path contentRoot) {
        this.contentRoot = contentRoot;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setListOfQuestionnaires(List<Questionnaire> questionnaire) {
        this.questionnaires = questionnaire;
    }

    public List<Question> getQuestion() {
        return questions;
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(10001);


        // server.setListOfQuestionnaires(List.of("Yes", "No"));
        server.setContentRoot(Paths.get("src/main/resources"));
    }

    public List<Questionnaire> getQuestionnaire() {
        return questionnaires;
    }
}
