package com.dunai.home.client.workspace;

import org.json.JSONObject;

public class GraphWidget extends Widget {
    public GraphWidget(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor) {
        super(id, title, topic, retain, spanPortrait, spanLandscape, bgColor);
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

    @Override
    public String getType() {
        return "graph";
    }
}
