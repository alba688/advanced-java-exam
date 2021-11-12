package no.kristiania.Dao;

import no.kristiania.Objects.Answer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class AnswerDao extends AbstractDao<Answer>{

    public AnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answer (question_id, answer_value, person_id) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setInt(1, answer.getQuestionId());
                statement.setInt(2, answer.getAnswerValue());
                statement.setInt(3, answer.getPersonId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setAnswerId((rs.getInt("answer_id")));
                }
            }
        }
    }

    public Answer retrieve(int id) throws SQLException {
        return super.retrieve("select * from answer where answer_id = (?)", id);
    }

    public List<Answer> listAll() throws SQLException {
        return super.listAll("select * from answer");
    }


    public List<Answer> listAllWithParameter(int id) throws SQLException {
        return super.listAllWithParameter("select * from answer where question_id = (?)", id);

    public int getAverage(int id) throws SQLException {
        return super.getAverage("select AVG(answer_value) from answer where question_id = (?)", id);

    }
    @Override
    protected Answer mapFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setAnswerValue(rs.getInt("answer_value"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setAnswerId(rs.getInt("answer_id"));
        answer.setPersonId(rs.getInt("person_id"));
        return answer;
    }

}
