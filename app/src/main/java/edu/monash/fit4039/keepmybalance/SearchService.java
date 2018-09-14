package edu.monash.fit4039.keepmybalance;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static edu.monash.fit4039.keepmybalance.JSONReader.getJSONObjectFromArray;
import static edu.monash.fit4039.keepmybalance.JSONReader.toJSONArray;
import static edu.monash.fit4039.keepmybalance.JSONReader.toJSONObject;
import static edu.monash.fit4039.keepmybalance.KMBConstant.*;

/**
 * Created by nathan on 19/5/17.
 */


public class SearchService {

    //get current weather based on the location
    //resource: https://openweathermap.org/current#one
    public static JSONObject getCurrentWeather(Location location) {
        String urlString = "http://api.openweathermap.org/data/2.5/weather?lat="
                + location.getLatitude() + "&lon=" + location.getLongitude() +
                "&appid=" + OPEN_WEATHER_KEY;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            urlString += "";
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1500);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return toJSONObject(textResult);
    }

    //get address based on the location
    //resource: https://developers.google.com/maps/documentation/geocoding/intro?hl=en
    public static JSONObject getAddress(Location location) {
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + location.getLatitude() + "," + location.getLongitude() +
                "&key=" + GOOGLE_MAPS_KEY;
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            urlString += "";
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return toJSONObject(textResult);
    }
}
