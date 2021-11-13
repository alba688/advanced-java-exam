package no.kristiania.Controller;

import no.kristiania.Http.HttpReader;

import java.io.IOException;
import java.sql.SQLException;

public interface HttpController {
    HttpReader handle(HttpReader request) throws SQLException, IOException;
}
