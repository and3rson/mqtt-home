package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Item {
    public String id;
    public String title;
    public String topic;

    public Item(String id, String title, String topic) {
        this.id = id;
        this.title = title;
        this.topic = topic;
    }

    public JSONObject serialize() {
        JSONObject root = new JSONObject();
        try {
            root.put("id", this.id);
            root.put("title", this.title);
            root.put("topic", this.topic);
            root.put("type", this.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public abstract String getType();
    public abstract String getTitle();
    public abstract String getSubTitle();
    public abstract int getIconResource();
}
