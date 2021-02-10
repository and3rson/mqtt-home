package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public class ButtonWidget extends Widget {
    public String caption;
    public String payload;

    public ButtonWidget(String id, String title, String topic, boolean retain, int span, String bgColor, String caption, String payload) {
        super(id, "button", title, topic, retain, span, bgColor);
        this.caption = caption;
        this.payload = payload;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("caption", this.caption);
            root.put("payload", this.payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
