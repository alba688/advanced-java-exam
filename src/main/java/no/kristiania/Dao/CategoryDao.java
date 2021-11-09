package no.kristiania.Dao;

import no.kristiania.Objects.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.*;
import java.util.List;

public class CategoryDao extends AbstractDao<Category>{

    public CategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Category category) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into category (category_title, category_text) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, category.getCategoryTitle());
                statement.setString(2, category.getCategoryText());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    category.setCategory_id(rs.getInt("category_id"));
                }
            }
        }
    }

    public Category retrieve(int id) throws SQLException {
        return super.retrieve("select * from category where category_id = (?)", id);
    }

    public List<Category> listAll() throws SQLException {
        return super.listAll("select * from category");
    }

    @Override
    protected Category mapFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategory_id(rs.getInt("category_id"));
        category.setCategoryTitle(rs.getString("category_title"));
        category.setCategoryText(rs.getString("category_text"));
        return category;
    }
}
