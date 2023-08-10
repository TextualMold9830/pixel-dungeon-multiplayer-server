package com.nikita22007.multiplayer.server.desktop;

import static java.lang.System.out;

public class Log {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    //ERROR
    public static void e(String tag, String msg) {
        out.println("[ERR] " + tag+": "+msg);
    }
    //WARNING
    public static void w(String tag, String msg){
        out.println("[WARN] " + tag+": "+msg);


    }
    //INFO
    public static void i(String tag, String msg){
        out.println("[INFO] " + tag+": "+msg);
    }
    public static void wtf(String tag, String msg, String error){
        out.println(tag+": "+msg+" from "+error);
    }
    public static void wtf(String tag, String msg){
        out.println("["+tag+"]: "+msg);
    }
}
