package no.kristiania.Dao;

import no.kristiania.Objects.Questionnaire;

import javax.sql.DataSource;
import java.sql.*;

public class QuestionnaireDao extends AbstractDao <Questionnaire> {

    public QuestionnaireDao(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Questionnaire questionnaire) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into questionnaire (questionnaire_title, questionnaire_text) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, questionnaire.getQuestionnaireTitle());
                statement.setString(2, questionnaire.getQuestionnaireText());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    questionnaire.setQuestionnaireId(rs.getInt("questionnaire_id"));
                }
            }
        }
    }
    public Questionnaire retrieve(int id) throws SQLException {
        return super.retrieve("select * from questionnaire where questionnaire_id = (?)", id);
    }

    @Override
    protected Questionnaire mapFromResultSet(ResultSet rs) throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireId(rs.getInt("questionnaire_id"));
        questionnaire.setQuestionnaireTitle(rs.getString("questionnaire_title"));
        questionnaire.setQuestionnaireText(rs.getString("questionnaire_text"));
        return questionnaire;
    }
}
