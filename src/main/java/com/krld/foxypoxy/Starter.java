package com.krld.foxypoxy;

import com.krld.foxypoxy.network.TLClient;

public class Starter {
    public static void main(String[] args) {
        new TLClient(new FoxyPoxyBot()).run();
    }
}
