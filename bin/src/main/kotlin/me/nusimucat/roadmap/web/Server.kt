package me.nusimucat.roadmap.web

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers

object Server {
    private var server: Undertow = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(object : HttpHandler() {
            @Throws(Exception::class)
            override fun handleRequest(exchange: HttpServerExchange) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain")
                exchange.getResponseSender().send("Hello World")
            }
        }).build()

    fun runServer() {
        server.start()
    }
}
