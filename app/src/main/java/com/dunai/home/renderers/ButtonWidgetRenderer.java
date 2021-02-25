package com.dunai.home.renderers;

import android.annotation.SuppressLint;
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
@SuppressLint("ViewConstructor")
public class ButtonWidgetRenderer extends WidgetRenderer {
    public ButtonWidgetRenderer(Context context, ButtonWidget workspaceButtonWidget, String value) {
        super(context, workspaceButtonWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_renderer, this.findViewById(R.id.rendererContainer), true);

        LinearLayout layout = this.findViewById(R.id.buttonRendererLayout);
        layout.removeAllViews();

        if (workspaceButtonWidget.orientation == ButtonWidget.Orientation.HORIZONTAL) {
            layout.setOrientation(HORIZONTAL);
        } else {
            layout.setOrientation(VERTICAL);
        }

        for (ButtonWidget.KeyValue keyValue : workspaceButtonWidget.keyValues) {
            Button button = new Button(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (workspaceButtonWidget.orientation == ButtonWidget.Orientation.VERTICAL) {
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
            button.setLongClickable(true);
            layout.addView(button);
        }

        if (value != null) {
            this.setValue(value);
        }
    }

    public void setValue(String value) {
        super.notifyValueChanged();
    }
}