package no.kristiania.Dao;

import no.kristiania.Objects.Question;

import javax.sql.DataSource;
import java.sql.*;

public class QuestionDao {

    private final DataSource datasource;

    public QuestionDao(DataSource dataSource) {
        this.datasource = dataSource;
    }

    public void save(Question question) throws SQLException {
        try (Connection connection = datasource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into question (question_title) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, question.getQuestionTitle());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    question.setQuestionId((rs.getInt("question_id")));
                }
            }
        }

    }

    public Question retrieve(int questionId) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from question where question_id = ?"
            )) {
                statement.setInt(1, questionId);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return mapFromResultSet(rs);
                }
            }
        }
    }

    private Question mapFromResultSet(ResultSet rs) throws SQLException {
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
