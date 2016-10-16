package com.krld.foxypoxy;

import io.vertx.core.Vertx;

public class Starter {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TLClientVerticle());
        vertx.deployVerticle(new GameLogic());
    }
}
