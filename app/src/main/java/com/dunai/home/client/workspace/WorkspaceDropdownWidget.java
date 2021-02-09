package com.dunai.home.client.workspace;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;

public class WorkspaceDropdownWidget extends WorkspaceWidget {
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

    public String suffix;
    public ArrayList<KeyValue> keyValues;

    public WorkspaceDropdownWidget(String id, String title, String topic, int span, ArrayList<KeyValue> keyValues, String bgColor) {
        super(id, "dropdown", title, topic, span, bgColor);
        this.keyValues = keyValues;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
