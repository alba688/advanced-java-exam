package no.kristiania.Dao;

import no.kristiania.Objects.Answer;

import javax.sql.DataSource;
import java.sql.*;

public class AnswerDao extends AbstractDao<Answer>{

    public AnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answer (question_id, answer_value) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setInt(1, answer.getQuestionId());
                statement.setInt(2, answer.getAnswerValue());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setQuestionId((rs.getInt("answer_id")));
                }
            }
        }
    }
    @Override
    protected Answer mapFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setAnswerValue(rs.getInt("answer_value"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setAnswerId(rs.getInt("answer_id"));
        return answer;
    }

}
