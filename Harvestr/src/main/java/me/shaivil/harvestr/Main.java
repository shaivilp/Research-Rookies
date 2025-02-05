package me.shaivil.harvestr;


import me.shaivil.harvestr.jobs.SmtdRequest;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;
import me.shaivil.harvestr.jobs.Request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Logger.log(LogType.SUCCESS, "Successfully started Harvestr");

        ExecutorService executor = Executors.newFixedThreadPool(100);

        //Request request = new Request();
        //executor.execute(request);

        SmtdRequest smtdRequest = new SmtdRequest();
        executor.execute(smtdRequest);

    }
}
