package no.kristiania.Controller;

import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Answer;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;
import java.util.Map;

import static no.kristiania.Http.HttpReader.parseRequestParameters;

public class ShowAnswersController implements HttpController{
    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;
    private final CategoryDao categoryDao;
    private final AnswerDao answerDao;

    public ShowAnswersController(QuestionnaireDao questionnaireDao, CategoryDao categoryDao, QuestionDao questionDao, AnswerDao answerDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
        this.categoryDao = categoryDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseTxt = "";
        Map<String, String> queryMap = parseRequestParameters(request.messageBody);

        Questionnaire questionnaire = questionnaireDao.retrieve(Integer.parseInt(queryMap.get("questionnaires")));

        responseTxt = "<h1>" + questionnaire.getQuestionnaireTitle() + "</h1><p>" + questionnaire.getQuestionnaireText() + "</p>";


        for (Category category : categoryDao.listAllWithParameter(questionnaire.getQuestionnaireId())){
            responseTxt += "<h2>"+ category.getCategoryTitle()+"</h2>";

            for (Question question : questionDao.listAllWithParameter(category.getCategoryId())) {
                responseTxt += "<p>" + question.getQuestionTitle() + "</p>";

                responseTxt += "<p> Average answer value: " + answerDao.getAverage(question.getQuestionId()) + "</p>";
            }
        }

        return new HttpReader("HTTP/1.1 200 OK", responseTxt);
    }
}
