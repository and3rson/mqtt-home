package com.dunai.home.client.workspace;

import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ButtonWidget extends Widget {
    public ArrayList<KeyValue> keyValues;
    public Orientation orientation;

    public static class KeyValue {
        private String key;
        private String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    };

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public ButtonWidget(String id, String title, String topic, boolean retain, int span, String bgColor, ArrayList<KeyValue> keyValues, Orientation orientation) {
        super(id, "button", title, topic, retain, span, bgColor);
        this.keyValues = keyValues;
        this.orientation = orientation;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            JSONArray list = new JSONArray();
            for (KeyValue keyValue : this.keyValues) {
                JSONObject obj = new JSONObject();
                obj.put("key", keyValue.getKey());
                obj.put("value", keyValue.getValue());
                list.put(obj);
            }
            root.put("keyValues", list);
            root.put("orientation", orientation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
