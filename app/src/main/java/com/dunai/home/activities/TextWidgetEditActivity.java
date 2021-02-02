package com.dunai.home.activities;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.TextWidget;
import com.dunai.home.client.workspace.Widget;

import java.util.Collections;
import java.util.List;

public class TextWidgetEditActivity extends AbstractWidgetEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
    private TextView prefix;
    private TextView suffix;
    private HomeClient client;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_text_renderer_edit;
    }

    @Override
    protected String getType() {
        return "text";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Collections.emptyList();
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor) {
        return new TextWidget(id, title, topic, retain, spanPortrait, spanLandscape, bgColor, prefix.getText().toString(), suffix.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefix = findViewById(R.id.textRendererEditPrefix);
        suffix = findViewById(R.id.textRendererEditSuffix);

        TextWidget item = (TextWidget) getExisting();
        if (item != null) {
            prefix.setText(item.prefix);
            suffix.setText(item.suffix);
        }
    }
}