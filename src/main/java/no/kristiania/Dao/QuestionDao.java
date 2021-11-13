package no.kristiania.Dao;

import no.kristiania.Objects.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class QuestionDao extends AbstractDao<Question> {

    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    private static final Logger logger = LoggerFactory.getLogger(QuestionDao.class);

    public void save(Question question) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into question (question_title, low_label, high_label, number_of_values, category_id) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, question.getQuestionTitle());
                statement.setString(2, question.getLowLabel());
                statement.setString(3, question.getHighLabel());
                statement.setInt(4, question.getNumberOfValues());
                statement.setInt(5, question.getCategoryId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    question.setQuestionId((rs.getInt("question_id")));
                }
            }
        }
        logger.info("Question " + question.getQuestionTitle() + " saved");

    }

    public Question retrieve(int questionId) throws SQLException {
        logger.info("Question " + questionId + " saved");
        return super.retrieve("select * from question where question_id = ?", questionId);
    }


    public List<Question> listAllWithParameter(int id) throws SQLException {
        logger.info("All questions belonging to category "+ id  + " listed");
        return super.listAllWithParameter("select * from question where category_id = ?", id);
    }


    public List<Question> listAll() throws SQLException {
        logger.info("All questions listed");
        return super.listAll("select * from question");
    }


    public void edit(Question editedQuestion) throws SQLException {
        logger.info("Question " + editedQuestion.getQuestionTitle() + " edited");
        super.edit("update question set question_title = ?, low_label = ?, high_label = ?, number_of_values = ? where question_id = ?", editedQuestion);
    }

    public void delete(int questionId) throws SQLException {
        logger.info("Question " + questionId + " deleted");
        super.delete("delete from answer where question_id = ?", questionId);
        super.delete("delete from question where question_id = ?", questionId);
    }


    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionTitle(rs.getString("question_title"));
        question.setLowLabel(rs.getString("low_label"));
        question.setHighLabel(rs.getString("high_label"));
        question.setNumberOfValues(rs.getInt("number_of_values"));
        question.setCategoryId(rs.getInt("category_id"));
        return question;
    }
}
