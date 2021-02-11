package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ButtonWidget;

/**
 * TODO: document your custom view class.
 */
public class ButtonWidgetRenderer extends WidgetRenderer {
    private final ButtonWidget workspaceButtonWidget;

    private final LinearLayout layout;

    public ButtonWidgetRenderer(Context context, ButtonWidget workspaceButtonWidget, String value) {
        super(context, workspaceButtonWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceButtonWidget = workspaceButtonWidget;

        this.layout = this.findViewById(R.id.buttonRendererLayout);
        this.layout.removeAllViews();

        if (this.workspaceButtonWidget.orientation == ButtonWidget.Orientation.HORIZONTAL) {
            this.layout.setOrientation(HORIZONTAL);
        } else {
            this.layout.setOrientation(VERTICAL);
        }

        for (ButtonWidget.KeyValue keyValue : this.workspaceButtonWidget.keyValues) {
            Button button = new Button(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (this.workspaceButtonWidget.orientation == ButtonWidget.Orientation.VERTICAL) {
                params.width = LayoutParams.MATCH_PARENT;
            }
            params.weight = 1.0f;
            button.setLayoutParams(params);
            button.setText(keyValue.getKey());

            final ButtonWidget.KeyValue kv = keyValue;

            button.setOnClickListener(v -> HomeClient.getInstance().publish(
                    workspaceButtonWidget.topic,
                    kv.getValue(),
                    workspaceButtonWidget.retain
            ));
            this.layout.addView(button);
        }

        if (value != null) {
            this.setValue(value);
        }
    }

    public void setValue(String value) {
        super.notifyValueChanged();
    }
}