package com.gmail.bishoybasily.k8s.service;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class Main {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(context -> {

            HttpServerResponse response = context.response();
            response.putHeader("content-type", "text/plain");

            String message = System.getenv("MESSAGE");
            if (message == null || "".equals(message))
                message = "Hello World from Vert.x-Web!";

            response.end(message);

        });

        server.requestHandler(router).listen(9090);

    }

}
