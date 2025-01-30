package me.shaivil.loggerutil;

import me.shaivil.loggerutil.LogType;

public class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void log(LogType type, String msg){
        if(type ==  LogType.INFO){
            System.out.println("[" + ANSI_CYAN + "INFO" + ANSI_RESET + "] " + msg);
        }
        if(type == LogType.DEBUG){
            System.out.println("[" + ANSI_BLUE + "DEBUG" + ANSI_RESET + "] " + msg);
        }
        if(type ==  LogType.ERROR){
            System.out.println("[" + ANSI_RED + "*" + ANSI_RESET + "] " + msg);
        }
        if(type ==  LogType.WARNING){
            System.out.println("[" + ANSI_YELLOW + "-" + ANSI_RESET + "] " + msg);
        }
        if(type ==  LogType.SUCCESS){
            System.out.println("[" + ANSI_GREEN+  "+" + ANSI_RESET + "] " + msg);
        }
        if(type ==  LogType.CONFIG){
            System.out.println("[" + ANSI_PURPLE + "CONFIG" + ANSI_RESET + "] " + msg);
        }
        if(type ==  LogType.DATABASE){
            System.out.println("[" + ANSI_GREEN + "DATABASE" + ANSI_RESET + "] " + msg);
        }

    }

    public static void log(String prefix, String msg){
        System.out.println("[" + ANSI_GREEN+ prefix + ANSI_RESET + "] " + msg);
    }
}
