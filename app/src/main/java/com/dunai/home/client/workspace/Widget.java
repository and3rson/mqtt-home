package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Widget extends Item {
    public String bgColor;
    public boolean retain;
    public boolean showTitle;
    public boolean showLastUpdate;
    public int spanPortrait;
    public int spanLandscape;

    public Widget(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor) {
        super(id, title, topic);
        this.retain = retain;
        this.showTitle = showTitle;
        this.showLastUpdate = showLastUpdate;
        this.spanPortrait = spanPortrait;
        this.spanLandscape = spanLandscape;
        this.bgColor = bgColor;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("retain", this.retain);
            root.put("showTitle", this.showTitle);
            root.put("showLastUpdate", this.showLastUpdate);
            root.put("spanPortrait", this.spanPortrait);
            root.put("spanLandscape", this.spanLandscape);
            root.put("bgColor", this.bgColor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSubTitle() {
        return this.topic;
    }
}
