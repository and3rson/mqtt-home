package com.dunai.home.client;

import com.dunai.home.client.workspace.WorkspaceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Workspace {
    public ArrayList<WorkspaceItem> items = new ArrayList<>();
    public JSONObject serialize() {
        JSONArray items = new JSONArray();
        JSONObject root = new JSONObject();
        for (int i = 0; i < this.items.size(); i++) {
            items.put(this.items.get(i).serialize());
        }
        try {
            root.put("items", items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
