package no.kristiania.Controller;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;
import java.util.Map;

public class NewCategoryController implements HttpController {
    private final CategoryDao categoryDao;

    public NewCategoryController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Category category = new Category();
        category.setCategoryTitle(queryMap.get("title"));
        category.setCategoryText(queryMap.get("text"));
        int questionnaireId = Integer.parseInt(queryMap.get("questionnaire"));
        category.setQuestionnaireId(questionnaireId);
        categoryDao.save(category);

        return new HttpReader("HTTP/1.1 303 See other", "Category created", "Location: /addCategory.html");
    }
}
