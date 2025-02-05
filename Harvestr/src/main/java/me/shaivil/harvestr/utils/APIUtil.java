package me.shaivil.harvestr.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class APIUtil {

    static OkHttpClient client = new OkHttpClient();
    static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss.SSS a");
    public static String fetchData() throws IOException {
        //Object
        formatter.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        Request getRequest = new Request.Builder()
                .url("https://dekalbpublic.etaspot.net/service.php?service=get_vehicles&token=TESTING")
                .get()
                .build();

        String ret = "";
        Response response = client.newCall(getRequest).execute();
        if (response.code() == 200) {
            Logger.log(LogType.SUCCESS, "Successfully fetched data at " + formatter.format(new Date()));
            JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();

            if (json.has("get_vehicles") && json.get("get_vehicles").isJsonArray()) {
                JsonArray vehicles = json.get("get_vehicles").getAsJsonArray();
                ret = vehicles.toString();
            } else if (json.has("get_vehicles") && json.get("get_vehicles").isJsonPrimitive()) {
                ret = json.get("get_vehicles").getAsString();
            } else {
                Logger.log(LogType.ERROR, "Unexpected format for 'get_vehicles'");
                ret = "Unexpected format";
            }

        } else {
            Logger.log(LogType.ERROR, "Error while fetching data at " + formatter.format(new Date()));
            ret = "No new data";
        }

        return ret;
    }

    public static void downloadSmtdVehicles(String url, String folderPath, String fileName) throws IOException {
        formatter.setTimeZone(TimeZone.getTimeZone("America/Chicago"));

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            Logger.log(LogType.SUCCESS, "Successfully fetched data at " + formatter.format(new Date()));

            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException("Failed to create directory: " + folderPath);
                }
            }

            File file = new File(folder, fileName);
            try (InputStream inputStream = response.body().byteStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
