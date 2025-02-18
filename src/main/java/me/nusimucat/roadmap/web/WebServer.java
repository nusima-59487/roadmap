package me.nusimucat.roadmap.web;

import me.nusimucat.roadmap.ConfigLoader;
import io.undertow.Undertow; 
import io.undertow.server.HttpHandler; 
import io.undertow.server.HttpServerExchange; 
import io.undertow.util.Headers; 

public class WebServer {

    private static Undertow server = null; 


    public void start () {
        final int port = ConfigLoader.getIntVal("webserver.port"); 
        final String host = ConfigLoader.getStringVal("webserver.host"); 

        server = Undertow.builder()
            .addHttpListener(port, host)
            .setHandler(new HttpHandler() {
                @Override
                public void handleRequest(final HttpServerExchange exchange) throws Exception {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain"); 
                    exchange.getResponseSender().send("Hello World"); 
                }
            }).build(); 
        server.start(); 
    }
}