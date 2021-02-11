package com.dunai.home.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.SwitchWidget;
import com.dunai.home.client.workspace.Widget;

import java.util.Arrays;
import java.util.List;

public class SwitchWidgetEditActivity extends AbstractWidgetEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
    private TextView onValue;
    private TextView offValue;
    private HomeClient client;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_switch_renderer_edit;
    }

    @Override
    protected String getType() {
        return "switch";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Arrays.asList(this.onValue, this.offValue);
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor) {
        return new SwitchWidget(id, title, topic, retain, spanPortrait, spanLandscape, bgColor, onValue.getText().toString(), offValue.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onValue = findViewById(R.id.switchRendererEditOnValue);
        offValue = findViewById(R.id.switchRendererEditOffValue);

        SwitchWidget item = (SwitchWidget) getExisting();
        if (item != null) {
            onValue.setText(item.onValue);
            offValue.setText(item.offValue);
        }
    }
}