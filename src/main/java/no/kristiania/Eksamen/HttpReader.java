package no.kristiania.Eksamen;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpReader {

    static String readLine(Socket socket) throws IOException {
            StringBuilder line = new StringBuilder();
            int c;
            while ((c = socket.getInputStream().read()) != -1 && c != '\r') {
                line.append((char) c);
            }
            socket.getInputStream().read();
            return line.toString();
        }

    static String readBytes(InputStream in, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < contentLength ; i++) {
            result.append((char)in.read());
        }
        return result.toString();
    }
}
