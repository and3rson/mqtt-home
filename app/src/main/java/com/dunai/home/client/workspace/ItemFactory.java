package com.dunai.home.client.workspace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemFactory {
    public static Item createFromJSONObject(JSONObject item) throws Exception {
        String id = item.getString("id");
        String title = item.getString("title");
        switch (item.getString("type")) {
            case "section":
                return buildSection(item);
            case "text":
            case "switch":
            case "graph":
            case "dropdown":
            case "color":
            case "button":
            case "slider":
                return buildWidget(item);
            default:
                throw new Exception("Unknown item type: " + item.getString("type"));
        }
    }

    private static Section buildSection(JSONObject item) throws JSONException {
        return new Section(
                item.getString("id"),
                item.getString("title")
        );
    }

    private static Widget buildWidget(JSONObject item) throws Exception {
        String id = item.getString("id");
        String title = item.getString("title");
        String topic = item.getString("topic");
        int spanPortrait = item.has("spanPortrait") ? item.getInt("spanPortrait") : item.has("span") ? item.getInt("span") : 12;
        int spanLandscape = item.has("spanLandscape") ? item.getInt("spanLandscape") : spanPortrait;
        boolean retain = !item.has("retain") || item.getBoolean("retain");
        String bgColor = item.has("bgColor") ? item.getString("bgColor") : null;
        switch (item.getString("type")) {
            case "text":
                return new TextWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        item.has("prefix") ? item.getString("prefix") : "",
                        item.has("suffix") ? item.getString("suffix") : ""
                );
            case "switch":
                return new SwitchWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        item.getString("onValue"),
                        item.getString("offValue")
                );
            case "graph":
                return new GraphWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor
                );
            case "dropdown":
                ArrayList<DropdownWidget.KeyValue> dropdownKeyValues = new ArrayList<>();
                JSONArray dropdownList = item.getJSONArray("keyValues");
                for (int i = 0; i < dropdownList.length(); i++) {
                    JSONObject obj = dropdownList.getJSONObject(i);
                    dropdownKeyValues.add(new DropdownWidget.KeyValue(obj.getString("key"), obj.getString("value")));
                }
                return new DropdownWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        dropdownKeyValues
                );
            case "color":
                return new ColorWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        item.getString("format"),
                        item.getBoolean("alpha")
                );
            case "button":
                ArrayList<ButtonWidget.KeyValue> buttonKeyValues = new ArrayList<>();
                if (item.has("keyValues")) {
                    JSONArray buttonList = item.getJSONArray("keyValues");
                    for (int i = 0; i < buttonList.length(); i++) {
                        JSONObject obj = buttonList.getJSONObject(i);
                        buttonKeyValues.add(new ButtonWidget.KeyValue(obj.getString("key"), obj.getString("value")));
                    }
                }
                return new ButtonWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        buttonKeyValues,
                        ButtonWidget.Orientation.valueOf(item.has("orientation") ? item.getString("orientation") : "HORIZONTAL")
                );
            case "slider":
                return new SliderWidget(
                        id,
                        title,
                        topic,
                        retain,
                        spanPortrait,
                        spanLandscape,
                        bgColor,
                        item.getInt("minValue"),
                        item.getInt("maxValue"),
                        item.getInt("step")
                );
            default:
                throw new Exception("Unknown item type: " + item.getString("type"));
        }
    }
}
