package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public class TextWidget extends Widget {
    public String prefix;
    public String suffix;

    public TextWidget(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor, String prefix, String suffix) {
        super(id, title, topic, retain, spanPortrait, spanLandscape, bgColor);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("prefix", this.prefix);
            root.put("suffix", this.suffix);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public String getType() {
        return "text";
    }
}
