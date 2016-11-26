package com.csci571.csci571hw9.LocalStorage;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by suhailansari on 23/11/16.
 */
public class LocalStorage {

    private static LocalStorage ourInstance;
    public Context context;

    public static LocalStorage getInstance(Context _context) {
        if (ourInstance == null) {
            ourInstance = new LocalStorage(_context);
        }
        return ourInstance;
    }



    private LocalStorage(Context _context) {
        context = _context;
    }

    public JSONArray getItems(String itemType) {
        SharedPreferences localStorage = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE);
        String jsonObjectStr = localStorage.getString(itemType, "{}");
        try{
            JSONObject jsonObject = new JSONObject(jsonObjectStr);
            Iterator<String> iter = jsonObject.keys();
            JSONArray jsonArray = new JSONArray();
            while(iter.hasNext()){
                jsonArray.put(jsonObject.getJSONObject(iter.next()));
            }
            return jsonArray;
        } catch (JSONException error) {
            return null;
        }
    }

    public JSONObject getItem(String itemType, String itemKey) {
        SharedPreferences localStorage = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE);
        String jsonObjectStr = localStorage.getString(itemType, "{}");
        try {
            JSONObject jsonObject = new JSONObject(jsonObjectStr);
            if(jsonObject.has(itemKey)) {
                return jsonObject.getJSONObject(itemKey);
            } else {
                return null;
            }
        } catch (JSONException error) {
            return null;
        }
    }

    public boolean setItem(String itemType, String itemKey, JSONObject jsonObject) {
        SharedPreferences localStorage = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = localStorage.edit();
        String itemsJsonStr = localStorage.getString(itemType, "{}");
        try {
            JSONObject itemJsonObject = new JSONObject(itemsJsonStr);
            itemJsonObject.put(itemKey, jsonObject);
            editor.putString(itemType, itemJsonObject.toString());
            editor.commit();
            return true;
        } catch (JSONException error) {
            return false;
        }
    }

    public boolean hasItem(String itemType, String itemKey) {
        SharedPreferences localStorage = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE);
        String itemsJsonStr = localStorage.getString(itemType, "{}");
        try {
            JSONObject itemJsonObject = new JSONObject(itemsJsonStr);
            return itemJsonObject.has(itemKey);
        } catch (JSONException error) {
            return false;
        }
    }

    public boolean deleteItem(String itemType, String itemKey) {
        SharedPreferences localStorage = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = localStorage.edit();
        String itemsJsonStr = localStorage.getString(itemType, "{}");
        try {
            JSONObject itemJsonObject = new JSONObject(itemsJsonStr);
            itemJsonObject.remove(itemKey);
            editor.putString(itemType, itemJsonObject.toString());
            editor.commit();
            return true;
        } catch (JSONException error) {
            return false;
        }
    }

    public String toggleItem(String itemType, String itemKey, JSONObject jsonObject) {
        if(this.hasItem(itemType, itemKey)){
            this.deleteItem(itemType, itemKey);
            return "deleted";
        } else {
            this.setItem(itemType, itemKey, jsonObject);
            return "added";
        }
    }
}
