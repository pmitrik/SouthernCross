package com.company;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by pmitrik on 4/6/17.
 */
public class SouthernCross {

    SouthernCrossJSON southernCrossJSON;
    SouthernCrossCSV southernCrossCSV;

    SouthernCross() {
        // Nothing to do
    }

    // Kick off the effort
    public void start(final String[] args) {

        if (null != args) {
            System.out.println("args.length -> " + args.length);

            if (2 != args.length) {
                System.out.println("");
                System.out.println("The typed command line was invalid.  Please use the following:");
                System.out.println("java -jar SouthernCross.jar <filename1> <filename2>");
                System.out.println("");
            } else {
                // Got valid arguments
                System.out.println("Valid arguments, check lists...");

                try {
                    southernCrossJSON = new SouthernCrossJSON();
                    southernCrossCSV  = new SouthernCrossCSV();

                    JSONArray jsonArray   = southernCrossJSON.getJSON(new FileReader(args[0]));
                    ArrayList<String> obj = southernCrossCSV.getCSV(new FileReader(args[1]));

                    checkLists(jsonArray, obj);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check both lists, collect information and print results to a JSON file.
     *
     * @param jsonArray array to check (JSON style format)
     * @param obj array to check (CSV style format)
     */
    private void checkLists(final JSONArray jsonArray, final ArrayList<String> obj) {

        int duplicates  = 0;
        int sameDevices = 0;
        int withinArea  = 0;

        long latestTimestamp = 0;
        long oldTimestamp    = System.currentTimeMillis();

        if ((null != jsonArray) && !jsonArray.isEmpty()) {

            System.out.println("...Checking for duplicates...");

            // The CSV is loop through many times and we only need to check somethings once
            Boolean firstTime = true;

            for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {

                Object object     = iterator.next();
                JSONObject device = (JSONObject) object;

                final String uuid      = (String) device.get("uuid");
                final long timestamp   = (long) device.get("timestamp");
                final double latitude  = (double) device.get("lat");
                final double longitude = (double) device.get("long");

                // JSON data within geographic area?
                if (isWithinGeographicArea(longitude, latitude)) {
                    ++withinArea;
                }

                String[] columns = null;

                if ((null != obj) && !obj.isEmpty()) {
                    for (int index = 0; index < obj.size(); ++index) {

                        String line = obj.get(index);
                        String[] items = line.split(",");

                        if (0 == index) {
                            columns = items;
                        } else {
                            final long csvTimestamp   = southernCrossCSV.getTimestamp(columns, items);
                            final double csvLatitude  = southernCrossCSV.getLatitude(columns, items);
                            final double csvLongitude = southernCrossCSV.getLongitude(columns, items);

                            if (southernCrossCSV.matchesUUID(columns, items, uuid)) {

                                ++sameDevices;

                                /**
                                 * the UUID matched so check if the rest of data matches
                                 */
                                if ((csvLatitude == latitude) &&
                                        (csvLongitude == longitude) &&
                                        (csvTimestamp == timestamp)) {

                                    // Exact match!
                                    ++duplicates;

                                    System.out.println();
                                    System.out.println("JSON: " + device);
                                    System.out.println("CSV: " + line);
                                }
                                else {

                                    // Not a duplicate, so check CSV geographic area
                                    if (isWithinGeographicArea(csvLongitude, csvLatitude) && firstTime) {
                                        ++withinArea;
                                    }
                                }
                            }
                            else {

                                // Not a duplicate, so check CSV geographic area
                                if (isWithinGeographicArea(csvLongitude, csvLatitude) && firstTime) {
                                    ++withinArea;
                                }
                            }

                            latestTimestamp = newestTimestamp(latestTimestamp, timestamp);
                            latestTimestamp = newestTimestamp(latestTimestamp, csvTimestamp);

                            oldTimestamp    = oldestTimestamp(oldTimestamp, timestamp);
                            oldTimestamp    = oldestTimestamp(oldTimestamp, csvTimestamp);
                        }
                    }

                    firstTime = false;
                }
            }
        }

        /**
         * Place the information in to a JSON file.  Remember to remove one line from the CSV file due to it
         * being the column order for each line.
         */
        southernCrossJSON.writeJSON(
                (obj.size() - 1) + jsonArray.size(),
                ((obj.size() - 1) + jsonArray.size() - sameDevices),
                withinArea,
                latestTimestamp,
                oldTimestamp
        );
    }

    private Boolean isWithinGeographicArea(final double longitude, final double latitude) {
        final double west  = -179.99;
        final double east  = 0.0;
        final double north = 89.99;
        final double south = 0.0;

        Boolean result = false;

        if ((longitude >= west) && (longitude <= east)) {
            if ((latitude <= north) && (latitude >= south)) {
                result = true;
            }
        }

        return result;
    }

    private long newestTimestamp(final long date1, final long date2) {
        long result = date1;

        if (date2 > result) {
            result = date2;
        }

        return result;
    }

    private long oldestTimestamp(final long date1, final long date2) {
        long result = date1;

        if (date2 < result) {
            result = date2;
        }

        return result;
    }
}
