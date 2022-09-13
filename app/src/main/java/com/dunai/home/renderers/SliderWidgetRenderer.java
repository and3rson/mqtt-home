package com.dunai.home.renderers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.SeekBar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.SliderWidget;

/**
 * TODO: document your custom view class.
 */
@SuppressLint("ViewConstructor")
public class SliderWidgetRenderer extends WidgetRenderer {

    private final SeekBar seekBar;
    private final int widgetMinValue;

    public SliderWidgetRenderer(Context context, SliderWidget workspaceSliderWidget, String value) {
        super(context, workspaceSliderWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slider_renderer, this.findViewById(R.id.rendererContainer), true);

        widgetMinValue = workspaceSliderWidget.minValue;
        final int sliderRange = workspaceSliderWidget.maxValue - workspaceSliderWidget.minValue;

        this.seekBar = this.findViewById(R.id.sliderRendererSeekBar);
        // SeekBar's minValue is 0 by default
        this.seekBar.setMax(sliderRange);

        if (value != null) {
            this.setValue(value);
        }

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    final int widgetValue = mapSeekBarToWidget(progress);
                    HomeClient.getInstance().publish(
                            workspaceSliderWidget.topic,
                            String.valueOf(widgetValue),
                            workspaceSliderWidget.retain
                    );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (value != null) {
            this.setValue(value);
        }
    }

    public void setValue(String value) {
        try {
            int widgetValue = Integer.parseInt(value);
            this.seekBar.setProgress(mapWidgetToSeekBar(widgetValue));
        } catch (Exception e) {
            //
        }
        super.notifyValueChanged();
    }

    private int mapWidgetToSeekBar(int value){
        return value - widgetMinValue;
    }

    private int mapSeekBarToWidget(int value){
        return widgetMinValue + value;
    }


}