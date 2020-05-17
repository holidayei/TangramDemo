package com.holiday.tangram.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
    public static String getString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? jsonObject.getString(key) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
