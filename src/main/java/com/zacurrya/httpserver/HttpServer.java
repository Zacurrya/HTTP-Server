package com.zacurrya.httpserver;

import com.zacurrya.httpserver.config.Configuration;
import com.zacurrya.httpserver.config.ConfigurationManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Driver Class for the HTTP Server
 */
public class HttpServer {

    public static void main(String[] args) {

        System.out.println("Starting HTTP Server...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using Port: " + config.getPort());
        System.out.println("Using Web Root: " + config.getWebRoot());

        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String html = "<html><head><title>Java HTTP Server</title></head><body><h1>Built with Maven, Java & Jackson (Json conversion)</h1></body></html>";

            final String CRLF = "\n\r"; // 13, 10


            String response =
                    "HTTP/1.1 200 OK" + CRLF +  // Status Line;   :   HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                    "Content-Length: " + html.getBytes().length + CRLF +  // HEADER
                            CRLF +
                            html +
                            CRLF + CRLF ;

            outputStream.write(response.getBytes());

            inputStream.close();
            outputStream.close();
            socket.close();
            serverSocket.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
