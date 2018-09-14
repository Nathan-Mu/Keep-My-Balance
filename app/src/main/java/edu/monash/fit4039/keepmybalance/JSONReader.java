package edu.monash.fit4039.keepmybalance;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nathan on 9/5/17.
 */

public class JSONReader {

    public JSONReader() {}

    //transfer string to JSONObject
    public static JSONObject toJSONObject(String string)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //transfer string to JSONArray
    public static JSONArray toJSONArray(String string)
    {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //get first JSONObject from JSONArray
    public static JSONObject getFirstFromArray(JSONArray jsonArray)
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = jsonArray.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //get a JSONArray from JSONObject
    public static JSONArray getJSONArrayFromJSONObject(JSONObject jsonObject, String name)
    {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = jsonObject.getJSONArray(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //get a String from JSONObject
    public static String getStringFromJSONObject(JSONObject jsonObject, String name)
    {
        String string = "";
        try {
            string = jsonObject.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    //get an Integer from JSONObject
    public static int getIntegerFromJSONObject(JSONObject jsonObject, String name)
    {
        int i = -1;
        try {
            i = jsonObject.getInt(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    //get a double from JSONObject
    public static Double getDoubleFromJSONObject(JSONObject jsonObject, String name)
    {
        Double d = 0.0;
        try {
            d = jsonObject.getDouble(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    //get a JSONObject from a JSONObject
    public static JSONObject getJSONObjectFromJSONObject(JSONObject jsonObject, String name)
    {
        JSONObject json = new JSONObject();
        try {
            json = jsonObject.getJSONObject(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    //get a JSONObject from a JSONArray
    public static JSONObject getJSONObjectFromArray(JSONArray jsonArray, int i)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = jsonArray.getJSONObject(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

