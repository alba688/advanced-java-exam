package no.kristiania.Controller;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.Http.HttpReader.parseRequestParameters;

public class ShowQuestionnaireQuestionsController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;
    private final CategoryDao categoryDao;


    public ShowQuestionnaireQuestionsController(QuestionnaireDao questionnaireDao, CategoryDao categoryDao, QuestionDao questionDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseTxt = "<html lang=\"no\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Show Questionnaire | Kristiania Questionnaire</title>\n" +
                "    <link rel=\"stylesheet\" href=\"../style.css\">\n" +
                "</head>\n" +
                "<body>";
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);

        Questionnaire questionnaire = questionnaireDao.retrieve(Integer.parseInt(queryMap.get("questionnaires")));

        responseTxt += "<h1>" + questionnaire.getQuestionnaireTitle() + "</h1><p>" + questionnaire.getQuestionnaireText() + "</p><form method=\"POST\" action=\"/api/answerQuestionnaire\">";

        int j = 0;
        for (Category category : categoryDao.listAllWithParameter(questionnaire.getQuestionnaireId())){
            responseTxt += "<div class=\"category\"><h2>"+ category.getCategoryTitle()+"</h2><p>"+ category.getCategoryText()+"</p>";

            for (Question question : questionDao.listAllWithParameter(category.getCategoryId())) {
                responseTxt += "<p>" + question.getQuestionTitle() +
                        "</p>" +
                        "<label>" + question.getLowLabel() + "</label>";


                for (int i = 1; i <= question.getNumberOfValues(); i++) {
                    responseTxt += "<input value=\"" + question.getQuestionId() + "v" + i +"\"" + "type=\"radio\" name=\"question"+j+"\"></input>";
                }
                j++;
                responseTxt += "<label>" + question.getHighLabel() + "</label><br>";
            }
                responseTxt += "</div>";
        }

        try {
            Integer.parseInt(request.parseRequestParameters(request.getResponseHeader("Cookie")).get("user"));
        responseTxt += "<button value=\"Send\">Send</button>";
        } catch (NullPointerException npe) {
            responseTxt+= "<p>You need to log in to answer questionnaire</p>";
        }

        responseTxt += "</form></body></html>";


        return new HttpReader("HTTP/1.1 200 OK", responseTxt);

    }
}
