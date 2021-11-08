package no.kristiania.Dao;

import no.kristiania.Objects.Question;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {
    protected final DataSource dataSource;

    protected AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    abstract protected T mapFromResultSet(ResultSet rs) throws SQLException;

    protected T retrieve(String sql, int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();

                    return mapFromResultSet(rs);
                }
            }
        }
    }

    protected List<T> listAll(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList <T> result = new ArrayList<>();
                    while(rs.next()) {
                        result.add(mapFromResultSet(rs));
                    };

                    return result;
                }
            }
        }
    }

}
