package com.dunai.home.client;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceText extends WorkspaceItem {
    public String title;
    public String topic;
    public int span;
    public String suffix;
    public String bgColor;

    public WorkspaceText(String id, String title, String topic, int span, String suffix, String bgColor) {
        super(id, "text");
        this.title = title;
        this.topic = topic;
        this.span = span;
        this.suffix = suffix;
        this.bgColor = bgColor;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("title", this.title);
            root.put("topic", this.topic);
            root.put("span", this.span);
            root.put("suffix", this.suffix);
            root.put("bgColor", this.bgColor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
