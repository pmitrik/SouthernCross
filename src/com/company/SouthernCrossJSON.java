package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by pmitrik on 4/16/17.
 */
public class SouthernCrossJSON {

    SouthernCrossJSON() {
        // Nothing to do
    }

    /**
     * Collect the JSON data and store results in an array. If the file does not
     * exist then return NULL.
     *
     * @param fr the file to read the JSON data from
     * @return the JSON array to return or null if invalid
     */
    protected JSONArray getJSON(final FileReader fr) {

        JSONArray deviceArray = null;

        if (null != fr) {
            JSONParser parser = new JSONParser();

            try {
                JSONObject jsonObject = (JSONObject) parser.parse(fr);

                deviceArray = (JSONArray) jsonObject.get("devices");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return deviceArray;
    }

    /**
     * Place the information in to a JSON file.  Remember to remove one line from the CSV file due to it
     * being the column order for each line.
     *
     * @param total_devices total devices found
     * @param unique_devices total unique devices found
     * @param withinArea devices within specified geographic area
     * @param latestTimestamp newest timestamp
     * @param oldTimestamp oldest timestamp
     */
    protected void writeJSON(final long total_devices, final long unique_devices, final long withinArea, final long latestTimestamp, final long oldTimestamp) {

        JSONObject jObj = new JSONObject();
        jObj.put("Total devices", total_devices);
        jObj.put("Unique devices", unique_devices);
        jObj.put("Within geographic area", withinArea);
        jObj.put("Newest date", LocalDateTime.ofInstant(Instant.ofEpochSecond(latestTimestamp), ZoneId.systemDefault()));
        jObj.put("Oldest date", LocalDateTime.ofInstant(Instant.ofEpochSecond(oldTimestamp), ZoneId.systemDefault()));

        try (FileWriter file = new FileWriter("results.json")) {

            file.write(jObj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
