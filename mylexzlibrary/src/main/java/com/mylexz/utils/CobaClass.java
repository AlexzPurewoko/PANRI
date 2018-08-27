package com.mylexz.utils;

public class CobaClass {
    static {

        System.loadLibrary("native-lib");
    }
    public static native String hello();
}
