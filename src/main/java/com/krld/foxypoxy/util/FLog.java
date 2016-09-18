package com.krld.foxypoxy.util;

public class FLog {
    private static void print(String logTag, String s) {  //TODO saving into file
        System.out.println(logTag + " " + s);
    }

    public static void d(String logTag, String s) {
        print(logTag, s);
    }

    public static void e(String logTag, String s) {
        print(logTag, s);
    }
}
