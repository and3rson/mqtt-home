package com.dunai.home.client.workspace;

import com.dunai.home.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Section extends Item {
    public String title;

    public Section(String id, String title) {
        super(id);
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

    @Override
    public String getType() {
        return "section";
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_w_section;
    }

    @Override
    public String getSubTitle() {
        return "";
    }
}
