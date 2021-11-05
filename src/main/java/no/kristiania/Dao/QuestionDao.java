package no.kristiania.Dao;

import no.kristiania.Objects.Question;

import javax.sql.DataSource;
import java.sql.*;

public class QuestionDao extends AbstractDao<Question> {

    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Question question) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into question (question_title, low_label, high_label, number_of_values) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, question.getQuestionTitle());
                statement.setString(2, question.getLowLabel());
                statement.setString(3, question.getHighLabel());
                statement.setInt(4, question.getNumberOfValues());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    question.setQuestionId((rs.getInt("question_id")));
                }
            }
        }

    }

    public Question retrieve(int questionId) throws SQLException {
        return super.retrieve("select * from question where question_id = ?", questionId);
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionTitle(rs.getString("question_title"));
        question.setLowLabel(rs.getString("low_label"));
        question.setHighLabel(rs.getString("high_label"));
        question.setNumberOfValues(rs.getInt("number_of_values"));
        question.setQuestionnaireId(rs.getInt("questionnaire_id"));
        return question;
    }
}
