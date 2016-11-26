package com.csci571.csci571hw9;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by suhailansari on 24/11/16.
 */
public class CustomUtils {
    private static CustomUtils ourInstance = new CustomUtils();

    public static CustomUtils getInstance() {
        return ourInstance;
    }

    private CustomUtils() {
    }

    public JSONArray sortJsonArray(JSONArray jsonArray, final String keyName) {

        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonValues.add(jsonArray.getJSONObject(i));
            } catch (JSONException error) {
                Log.e("Custom Utils", "json sorting");
            }
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {

            @Override
            public int compare(JSONObject a, JSONObject b) {

                if (keyName == "introduced_on") {

                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");

                    try {

                        Date dateA = dateFormatter.parse(a.getString(keyName));
                        Date dateB = dateFormatter.parse(b.getString(keyName));

                        return - dateA.compareTo(dateB);

                    } catch (JSONException error) {
                        Log.e("Custom Utils", "json comparing");
                    } catch (ParseException error) {
                        Log.e("Custom Utils", "json comparing");
                    }

                }

                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(keyName);
                    valB = (String) b.get(keyName);
                } catch (JSONException e) {
                    Log.e("Custom Utils", "json comparing");
                }

                return valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
}
