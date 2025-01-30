package me.shaivil.harvestr.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;
import okhttp3.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class APIUtil {

    static OkHttpClient client = new OkHttpClient();

    public static String fetchData() throws IOException {
        //Object
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss.SSS a");
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
}
