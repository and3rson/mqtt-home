package com.dunai.home.client.workspace;

import com.dunai.home.client.workspace.annotations.Editable;

import org.json.JSONException;
import org.json.JSONObject;

public class SwitchWidget extends Widget {
    @Editable(key = "On value", type = Editable.Type.STRING)
    public String onValue;
    @Editable(key = "Off value", type = Editable.Type.STRING)
    public String offValue;

    public SwitchWidget(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor, String onValue, String offValue) {
        super(id, "switch", title, topic, retain, spanPortrait, spanLandscape, bgColor);
        this.onValue = onValue;
        this.offValue = offValue;
    }

    public JSONObject serialize() {
        JSONObject root = super.serialize();
        try {
            root.put("onValue", this.onValue);
            root.put("offValue", this.offValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
