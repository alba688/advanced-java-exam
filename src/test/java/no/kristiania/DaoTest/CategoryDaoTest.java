package no.kristiania.DaoTest;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Objects.Category;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



public class CategoryDaoTest {
    private CategoryDao dao = new CategoryDao(TestData.testDataSource());



    @Test
    void shouldSaveAndRetrieveCategoryFromDatabase() throws SQLException {
        Category category = exampleCategory();
        dao.save(category);

        assertThat(dao.retrieve(category.getCategoryId()))
                .usingRecursiveComparison()
                .isEqualTo(category);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Category category = exampleCategory();
        dao.save(category);
        Category anotherCategory = exampleCategory();
        dao.save(anotherCategory);

        assertThat(dao.listAll())
                .extracting(Category::getCategoryId)
                .contains(category.getCategoryId(), anotherCategory.getCategoryId());


    }


    private Category exampleCategory() {
        Category category = new Category();
        category.setCategoryTitle(TestData.pickOne("Favorite Food", "Favorite Drink", "Favorite Class"));
        category.setCategoryText(TestData.pickOne("Category Description", "Share Your Opinion", "Some Test Text"));
        return category;
    }

}
