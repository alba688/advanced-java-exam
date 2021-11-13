package no.kristiania.Dao;

import no.kristiania.Objects.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.*;
import java.util.List;

public class CategoryDao extends AbstractDao<Category>{

    public CategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    public void save(Category category) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into category (category_title, category_text, questionnaire_id) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, category.getCategoryTitle());
                statement.setString(2, category.getCategoryText());
                statement.setInt(3, category.getQuestionnaireId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    category.setCategoryId(rs.getInt("category_id"));
                }
            }
        }
        logger.info("Category saved to questionnaire " + category.getQuestionnaireId());
    }

    public Category retrieve(int id) throws SQLException {
        logger.info("Category "+id +"retrieved");
        return super.retrieve("select * from category where category_id = (?)", id);
    }

    public List<Category> listAll() throws SQLException {
        logger.info("Categories listed");
        return super.listAll("select * from category");
    }
    public List<Category> listAllWithParameter(int id) throws SQLException {
        logger.info("Categories belonging to questionnaire " + id + "listed");
        return super.listAllWithParameter("select * from category where questionnaire_id = (?)", id);
    }

    @Override
    protected Category mapFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryTitle(rs.getString("category_title"));
        category.setCategoryText(rs.getString("category_text"));
        category.setQuestionnaireId(rs.getInt("questionnaire_id"));
        return category;
    }


}
