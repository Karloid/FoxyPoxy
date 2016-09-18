package com.krld.foxypoxy;

import com.krld.foxypoxy.network.TLClientVerticle;
import io.vertx.core.Vertx;

public class Starter {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TLClientVerticle());
        vertx.deployVerticle(new FoxyPoxyBotVerticle());
    }
}
