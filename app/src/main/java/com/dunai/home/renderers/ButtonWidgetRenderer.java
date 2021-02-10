package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ButtonWidget;

/**
 * TODO: document your custom view class.
 */
public class ButtonWidgetRenderer extends WidgetRenderer {
    private ButtonWidget workspaceButtonWidget;

//    private ToggleButton button;
    private Button button;

    public ButtonWidgetRenderer(Context context, ButtonWidget workspaceButtonWidget, String value) {
        super(context, workspaceButtonWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceButtonWidget = workspaceButtonWidget;

        this.button = this.findViewById(R.id.buttonRendererButton);
        this.button.setText(this.workspaceButtonWidget.caption);

        if (value != null) {
            this.setValue(value);
        }

        this.button.setOnClickListener(v -> {
            HomeClient.getInstance().publish(
                    workspaceButtonWidget.topic,
                    workspaceButtonWidget.payload,
                    this.workspaceButtonWidget.retain
            );
        });
    }

    public void setValue(String value) {
        super.notifyValueChanged();
    }
}