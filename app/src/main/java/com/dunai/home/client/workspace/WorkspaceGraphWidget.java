package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceGraphWidget extends WorkspaceWidget {
    public WorkspaceGraphWidget(String id, String title, String topic, int span, String bgColor) {
        super(id, "graph", title, topic, span, bgColor);
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
