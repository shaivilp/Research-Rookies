package me.shaivil.harvestr;


import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;
import me.shaivil.harvestr.jobs.Request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Logger.log(LogType.SUCCESS, "Successfully started Harvestr");

        ExecutorService executor = Executors.newFixedThreadPool(100);

        Request request = new Request();
        executor.execute(request);

    }
}
