package com.dunai.home.client.workspace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WorkspaceItemFactory {
    public static WorkspaceItem createFromJSONObject(JSONObject item) throws Exception {
        switch (item.getString("type")) {
            case "section":
                return new WorkspaceSection(
                        item.getString("id"),
                        item.getString("title")
                );
            case "text":
                return new WorkspaceTextWidget(
                        item.getString("id"),
                        item.getString("title"),
                        item.getString("topic"),
                        item.has("span") ? item.getInt("span") : 12,
                        item.has("suffix") ? item.getString("suffix") : "",
                        item.has("bgColor") ? item.getString("bgColor") : null
                );
            case "switch":
                return new WorkspaceSwitchWidget(
                        item.getString("id"),
                        item.getString("title"),
                        item.getString("topic"),
                        item.has("span") ? item.getInt("span") : 12,
                        item.getString("onValue"),
                        item.getString("offValue"),
                        item.has("bgColor") ? item.getString("bgColor") : null
                );
            case "graph":
                return new WorkspaceGraphWidget(
                        item.getString("id"),
                        item.getString("title"),
                        item.getString("topic"),
                        item.has("span") ? item.getInt("span") : 12,
                        item.has("bgColor") ? item.getString("bgColor") : null
                );
            case "dropdown":
                ArrayList<WorkspaceDropdownWidget.KeyValue> keyValues = new ArrayList<>();
                JSONArray list = item.getJSONArray("keyValues");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    keyValues.add(new WorkspaceDropdownWidget.KeyValue(obj.getString("key"), obj.getString("value")));
                }
                return new WorkspaceDropdownWidget(
                        item.getString("id"),
                        item.getString("title"),
                        item.getString("topic"),
                        item.has("span") ? item.getInt("span") : 12,
                        keyValues,
                        item.has("bgColor") ? item.getString("bgColor") : null
                );
            default:
                throw new Exception("Unknown item type: " + item.getString("type"));
        }
    }
}
