package com.dunai.home.client.workspace;

import com.dunai.home.client.workspace.annotations.Editable;

import org.json.JSONException;
import org.json.JSONObject;

public class Widget extends Item {
    @Editable(key = "Title", type = Editable.Type.STRING)
    public String title;
    public String bgColor;
    @Editable(key = "Topic", type = Editable.Type.STRING)
    public String topic;
    @Editable(key = "Width", type = Editable.Type.NUMBER, minValue = 1, maxValue = 12)
    public int span;
    public boolean retain;

    public Widget(String id, String type, String title, String topic, boolean retain, int span, String bgColor) {
        super(id, type);
        this.title = title;
        this.topic = topic;
        this.retain = retain;
        this.bgColor = bgColor;
        this.span = span;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("title", this.title);
            root.put("topic", this.topic);
            root.put("retain", this.retain);
            root.put("bgColor", this.bgColor);
            root.put("span", this.span);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
