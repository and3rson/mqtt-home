package com.dunai.home.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.workspace.SliderWidget;
import com.dunai.home.client.workspace.Widget;

import java.util.Arrays;
import java.util.List;

public class SliderWidgetEditActivity extends AbstractWidgetEditActivity {
    private TextView minValue;
    private TextView maxValue;
    private TextView step;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_slider_renderer_edit;
    }

    @Override
    protected String getType() {
        return "slider";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Arrays.asList(this.minValue, this.maxValue, this.step);
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor) {
        return new SliderWidget(
                id, title, topic, retain, showTitle, showLastUpdate, spanPortrait, spanLandscape, bgColor,
                Integer.parseInt(minValue.getText().toString()), Integer.parseInt(maxValue.getText().toString()), Integer.parseInt(step.getText().toString())
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        minValue = findViewById(R.id.sliderRendererEditMinValue);
        maxValue = findViewById(R.id.sliderRendererEditMaxValue);
        step = findViewById(R.id.sliderRendererEditStep);

        SliderWidget item = (SliderWidget) getExisting();
        if (item != null) {
            minValue.setText(String.valueOf(item.minValue));
            maxValue.setText(String.valueOf(item.maxValue));
            step.setText(String.valueOf(item.step));
        }
    }
}