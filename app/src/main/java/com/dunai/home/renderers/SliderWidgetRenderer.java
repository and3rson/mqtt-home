package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.SeekBar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.SliderWidget;

/**
 * TODO: document your custom view class.
 */
public class SliderWidgetRenderer extends WidgetRenderer {
    private final SliderWidget workspaceSliderWidget;

    private final SeekBar seekBar;

    public SliderWidgetRenderer(Context context, SliderWidget workspaceSliderWidget, String value) {
        super(context, workspaceSliderWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slider_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceSliderWidget = workspaceSliderWidget;

        this.seekBar = this.findViewById(R.id.sliderRendererSeekBar);
        this.seekBar.setMin(workspaceSliderWidget.minValue);
        this.seekBar.setMax(workspaceSliderWidget.maxValue);

        if (value != null) {
            this.setValue(value);
        }

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    HomeClient.getInstance().publish(
                            workspaceSliderWidget.topic,
                            String.valueOf(progress),
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
            this.seekBar.setProgress(Integer.parseInt(value));
        } catch (Exception e) {
            //
        }
        super.notifyValueChanged();
    }
}