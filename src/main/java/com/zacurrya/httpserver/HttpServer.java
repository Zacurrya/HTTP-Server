package com.zacurrya.httpserver;

import com.zacurrya.httpserver.config.Configuration;
import com.zacurrya.httpserver.config.ConfigurationManager;
import com.zacurrya.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Driver Class for the HTTP Server
 */
public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {

        LOGGER.info("Starting HTTP Server...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + config.getPort());
        LOGGER.info("Using Web Root: " + config.getWebRoot());

        // Starts a server listener thread
        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
