package mobiledev.unb.ca.lab4uielements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import javax.json.Json;
import javax.json.stream.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataModel {

    private ArrayList<GeoData> geoDataArray = new ArrayList<>();

    // Initializer to read our data source (JSON file) into an array of course objects
    public DataModel() {

        downloadJSON();
    }

    public void downloadJSON() {
        InputStream inputStream = null;
        String requestURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
        try {

            HttpURLConnection connection = null;

            // TODO Establish an HttpURLConnecion to requestURL
            // Hint: Read this:
            // http://developer.android.com/training/basics/network-ops/connecting.html

            URL url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            // Starts the query
            connection.connect();
            int response = connection.getResponseCode();

            inputStream = connection.getInputStream();

            // Convert the InputStream to String
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            processJSON(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processJSON(StringBuffer sb) {
        String jsonString = sb.toString();
        JsonParser parser = Json.createParser(new StringReader(jsonString));
        boolean titleTrigger = false;
        boolean coordTrigger = false;
        int count = 0;
        int coordCount = 0;

        // Finally, parse resultant JSON
        try {
            while(parser.hasNext()) {
                JsonParser.Event event = parser.next();
                switch (event) {
                    case KEY_NAME:
                        if(parser.getString().equals("title")) {
                            titleTrigger = true;
                        }
                        else if (parser.getString().equals("coordinates")) {
                            coordTrigger = true;
                        }
                        break;
                    case VALUE_STRING:
                        if(titleTrigger && parser.getString().startsWith("M")) {
                            GeoData geoData = new GeoData();
                            geoData.title = parser.getString();
                            geoDataArray.add(geoData);
                            titleTrigger = false;
                        }
                        break;
                    case VALUE_NUMBER:
                        if(coordTrigger && (coordCount == 0) ) {
                            GeoData geoData = geoDataArray.get(count);
                            geoData.longitude = parser.getString();
                            coordCount++;
                        }
                        else if(!coordTrigger && (coordCount == 1)) {
                            GeoData geoData = geoDataArray.get(count);
                            geoData.latitude = parser.getString();
                            coordCount = 0;
                            count++;
                        }
                        coordTrigger = false;
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter method for courses ArrayList
    public ArrayList<GeoData> getGeoData() {
        return geoDataArray;
    }
}
