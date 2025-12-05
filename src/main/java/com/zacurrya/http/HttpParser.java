package com.zacurrya.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    private static final int SP = 0x20; // Space
    private static final int CR = 0x0D; // Carriage Return
    private static final int LF = 0x0A; // Line Feed

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    // HTTP Parser for each thread
    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);

        HttpRequest request = new HttpRequest();

        try {
            parseRequestLine(reader, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            parseHeaders(reader, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        parseBody(reader, request);

        return request;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder(); // Holds incoming data as it is being processed

        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int _byte;
        while ( (_byte = reader.read()) != -1) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    LOGGER.debug("Request Line VERSION to Process : {}", processingDataBuffer.toString());
                    // Ensure that all required data has been parsed
                    if (!methodParsed || !requestTargetParsed) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    return;
                }
            }

            if (_byte == SP) {
                // TODO Process previous data
                if (!methodParsed) {
                    LOGGER.debug("Request Line METHOD to Process : {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line to REQ TARGET Process : {}", processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                LOGGER.debug("Request Line to Process : {}", processingDataBuffer.toString());
                processingDataBuffer.delete(0, processingDataBuffer.length());
            } else {
                processingDataBuffer.append((char) _byte);
                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private void parseBody(InputStreamReader reader, HttpRequest request) {

    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) {

    }
}
