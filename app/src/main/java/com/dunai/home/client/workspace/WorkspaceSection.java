package com.dunai.home.client.workspace;

import com.dunai.home.client.workspace.annotations.Editable;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceSection extends WorkspaceItem {
    @Editable(key = "Title", type = Editable.Type.STRING)
    public String title;

    public WorkspaceSection(String id, String title) {
        super(id, "section");
        this.title = title;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("title", this.title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
