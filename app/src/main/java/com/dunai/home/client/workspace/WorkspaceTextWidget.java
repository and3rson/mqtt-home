package com.dunai.home.client.workspace;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceTextWidget extends WorkspaceWidget {
    public String suffix;

    public WorkspaceTextWidget(String id, String title, String topic, int span, String suffix, String bgColor) {
        super(id, "text", title, topic, span, bgColor);
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
