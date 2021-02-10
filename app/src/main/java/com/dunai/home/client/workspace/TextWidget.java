package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public class TextWidget extends Widget {
    public String suffix;

    public TextWidget(String id, String title, String topic, boolean retain, int span, String bgColor, String suffix) {
        super(id, "text", title, topic, retain, span, bgColor);
        this.suffix = suffix;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("suffix", this.suffix);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
