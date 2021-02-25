package com.dunai.home.client.workspace;

import androidx.annotation.Nullable;

import com.dunai.home.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Section extends Item {
    public Section(String id, String title, @Nullable String topic) {
        super(id, title, topic);
    }

    public JSONObject serialize() {
        return super.serialize();
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
