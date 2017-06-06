package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pmitrik on 4/16/17.
 */
public class SouthernCrossCSV {

    SouthernCrossCSV() {
        // Nothing to do
    }

    /**
     * Read each line from the file in to an array. If the file is invalid
     * then return NULL.
     *
     * @param fr the file to read from
     * @return the array list or null if invalid
     */
    protected ArrayList<String> getCSV(final FileReader fr) {

        ArrayList<String> obj = null;

        try {
            if(null != fr) {
                obj = new ArrayList<>();

                BufferedReader br = new BufferedReader(fr);

                for (String line; (line = br.readLine()) != null; ) {

                    obj.add(line);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * The CSV data read in does not necessarily conform to a standard.  The first line is the order for
     * the rest of the file.  Use that to pull the requested data by finding the position within the array.
     *
     * @param arrayToSearch the array to search
     * @param stringToFind the string within the array to find
     * @return the position of the item or -1 if not found
     */
    private int findPosition(final String[] arrayToSearch, final String stringToFind) {
        return Arrays.asList(arrayToSearch).indexOf(stringToFind);
    }

    /**
     * Get the timestamp based on the columns and items passed.
     *
     * @param columns order of the list
     * @param items the items of the list
     * @return the found value
     */
    protected long getTimestamp(final String[] columns, final String[] items) {
        return Long.parseLong(items[findPosition(columns, "timestamp")]);
    }

    /**
     * Get the latitude based on the columns and items passed.
     *
     * @param columns order of the list
     * @param items the items of the list
     * @return the found value
     */
    protected double getLatitude(final String[] columns, final String[] items) {
        return Double.parseDouble(items[findPosition(columns, "lat")]);
    }

    /**
     * Get the longitude based on the columns and items passed.
     *
     * @param columns order of the list
     * @param items the items of the list
     * @return the found value
     */
    protected double getLongitude(final String[] columns, final String[] items) {
        return Double.parseDouble(items[findPosition(columns, "long")]);
    }

    /**
     * Check for a UUID match between the JSON and CSV lists.
     *
     * @param columns order of the list
     * @param items the items of the list
     * @param uuid the JSON uuid to check against
     * @return true if match otherwise false
     */
    protected Boolean matchesUUID(final String[] columns, final String[] items, final String uuid) {
        return items[findPosition(columns, "uuid")].equals(uuid);
    }
}
