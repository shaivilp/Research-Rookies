package me.shaivil.harvestr.jobs;

import me.shaivil.harvestr.storage.S3;
import me.shaivil.harvestr.utils.APIUtil;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SmtdRequest extends Thread{

    @Override
    public void run() {
        while (true){
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD-HH-mm-ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                String folder = "/tmp/harvestr-data/";
                String fileName = "vehicles-" + formatter.format(new Date()) + ".gtfsrt";

                //Download file from API
                APIUtil.downloadSmtdVehicles("http://ride.smtd.org/gtfsrt/vehicles", folder, fileName);

                //Upload file to S3
                S3.uploadFile(folder + fileName, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                sleep(30 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
