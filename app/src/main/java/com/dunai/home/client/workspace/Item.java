package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Item {
    public String id;

    public Item(String id) {
        this.id = id;
    }

    public JSONObject serialize() {
        JSONObject root = new JSONObject();
        try {
            root.put("id", this.id);
            root.put("type", this.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public abstract String getType();
}
