package no.kristiania.Controller;

import no.kristiania.Http.HttpReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class FileController implements HttpController{
    @Override
    public HttpReader handle(HttpReader request) throws IOException {
        String[] requestLine = request.startLine.split(" ");
        String fileTarget = requestLine[1];

        if (fileTarget.equals("/")) {
            fileTarget = "/index.html";
        }

        InputStream fileResource = getClass().getResourceAsStream(fileTarget);
        String contentType = "text/plain";
        String responseText = "";
        if (fileResource != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            fileResource.transferTo(buffer);
             responseText = buffer.toString();

            if (fileTarget.endsWith(".html")) {
                contentType = "text/html";
            } else if (fileTarget.endsWith(".css")) {
                contentType = "text/css";
            }

    } else {
           responseText = "File not found: " + fileTarget;

            return new HttpReader("HTTP/1.1 404 File not found", responseText);
        }
        return new HttpReader("HTTP/1.1" +fileTarget+ " 200 OK",responseText,"Content-Type: "+contentType);
    }
}
