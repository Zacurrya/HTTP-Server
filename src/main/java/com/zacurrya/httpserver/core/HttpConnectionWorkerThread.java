package com.zacurrya.httpserver.core;

import com.zacurrya.HttpParser;
import com.zacurrya.HttpParsingException;
import com.zacurrya.HttpRequest;
import com.zacurrya.httpserver.HttpServer;
import com.zacurrya.httpserver.core.io.ReadFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private Socket socket;
    private WebRootHandler webRootHandler;
    private HttpParser httpParser;

    public HttpConnectionWorkerThread(Socket socket, WebRootHandler webRootHandler) {
        this.socket = socket;
        this.webRootHandler = webRootHandler;
        this.httpParser = new HttpParser();
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            final String CRLF = "\r\n"; // CR LF: 13, 10

            try {
                // Parse the HTTP request
                HttpRequest request = httpParser.parseHttpRequest(inputStream);
                String requestTarget = request.getRequestTarget();

                LOGGER.info("Request for: " + requestTarget);

                // Get file content and mime type from WebRootHandler
                byte[] fileData = webRootHandler.getFileByteArrayData(requestTarget);
                String mimeType = webRootHandler.getFileMimeType(requestTarget);

                // Build successful HTTP response
                String response =
                        "HTTP/1.1 200 OK" + CRLF +
                                "Content-Type: " + mimeType + CRLF +
                                "Content-Length: " + fileData.length + CRLF +
                                CRLF;

                outputStream.write(response.getBytes(StandardCharsets.US_ASCII));
                outputStream.write(fileData);

                LOGGER.info(" * Connection Processing Finished.");

            } catch (HttpParsingException e) {
                LOGGER.error("HTTP Parsing Error: " + e.getMessage());
                sendErrorResponse(outputStream, 400, "Bad Request", CRLF);
            } catch (FileNotFoundException e) {
                LOGGER.error("File not found: " + e.getMessage());
                sendErrorResponse(outputStream, 404, "Not Found", CRLF);
            } catch (ReadFileException e) {
                LOGGER.error("Error reading file: " + e.getMessage());
                sendErrorResponse(outputStream, 500, "Internal Server Error", CRLF);
            }

        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendErrorResponse(OutputStream outputStream, int statusCode, String statusMessage, String CRLF) {
        try {
            String errorHtml = "<html><body><h1>" + statusCode + " " + statusMessage + "</h1></body></html>";
            String response =
                    "HTTP/1.1 " + statusCode + " " + statusMessage + CRLF +
                            "Content-Type: text/html" + CRLF +
                            "Content-Length: " + errorHtml.getBytes().length + CRLF +
                            CRLF +
                            errorHtml;
            outputStream.write(response.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            LOGGER.error("Error sending error response", e);
        }
    }
}
