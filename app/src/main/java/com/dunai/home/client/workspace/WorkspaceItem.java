package com.dunai.home.client.workspace;

import com.dunai.home.client.workspace.annotations.Editable;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceItem {
    public String id;
    public String type;

    public WorkspaceItem(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public JSONObject serialize() {
        JSONObject root = new JSONObject();
        try {
            root.put("id", this.id);
            root.put("type", this.type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
