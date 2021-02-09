package com.dunai.home.client.workspace;

import com.dunai.home.client.workspace.annotations.Editable;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkspaceSwitchWidget extends WorkspaceWidget {
    @Editable(key = "On value", type = Editable.Type.STRING)
    public String onValue;
    @Editable(key = "Off value", type = Editable.Type.STRING)
    public String offValue;

    public WorkspaceSwitchWidget(String id, String title, String topic, int span, String onValue, String offValue, String bgColor) {
        super(id, "switch", title, topic, span, bgColor);
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
