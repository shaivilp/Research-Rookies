package me.shaivil.harvestr.jobs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shaivil.harvestr.utils.APIUtil;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;

import java.io.IOException;

public class Request extends Thread{
    @Override
    public void run() {
        while(true){
            try {
                String response = APIUtil.fetchData();
                Logger.log(LogType.INFO, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                sleep(15 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
