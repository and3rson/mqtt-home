package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Widget extends Item {
    public String title;
    public String bgColor;
    public String topic;
    public int spanPortrait;
    public int spanLandscape;
    public boolean retain;

    public Widget(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor) {
        super(id);
        this.title = title;
        this.topic = topic;
        this.retain = retain;
        this.bgColor = bgColor;
        this.spanPortrait = spanPortrait;
        this.spanLandscape = spanLandscape;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("title", this.title);
            root.put("topic", this.topic);
            root.put("retain", this.retain);
            root.put("bgColor", this.bgColor);
            root.put("spanPortrait", this.spanPortrait);
            root.put("spanLandscape", this.spanLandscape);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
