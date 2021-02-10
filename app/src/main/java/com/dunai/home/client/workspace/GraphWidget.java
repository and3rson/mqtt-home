package com.dunai.home.client.workspace;

import org.json.JSONObject;

public class GraphWidget extends Widget {
    public GraphWidget(String id, String title, String topic, boolean retain, int span, String bgColor) {
        super(id, "graph", title, topic, retain, span, bgColor);
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
//        try {
//            root.put("suffix", this.suffix);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return root;
    }
}
