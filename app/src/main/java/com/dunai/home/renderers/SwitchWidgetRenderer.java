package com.dunai.home.renderers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import androidx.core.content.res.ResourcesCompat;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.SwitchWidget;

/**
 * TODO: document your custom view class.
 */
@SuppressLint("ViewConstructor")
public class SwitchWidgetRenderer extends WidgetRenderer {
    private final SwitchWidget workspaceSwitchWidget;

    //    private ToggleButton button;
    private final ImageButton button;
    private boolean isChecked = false;

    public SwitchWidgetRenderer(Context context, SwitchWidget workspaceSwitchWidget, String value) {
        super(context, workspaceSwitchWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.switch_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceSwitchWidget = workspaceSwitchWidget;

        this.button = this.findViewById(R.id.switchRendererButton);

        if (value != null) {
            this.setValue(value);
        }

        this.button.setOnClickListener(v -> HomeClient.getInstance().publish(
                workspaceSwitchWidget.topic,
                isChecked ? workspaceSwitchWidget.offValue : workspaceSwitchWidget.onValue,
                workspaceSwitchWidget.retain
        ));
    }

    public void setValue(String value) {
//        this.button.setChecked(value.equals(this.workspaceSwitchWidget.onValue));
        isChecked = value.equals(this.workspaceSwitchWidget.onValue);
        Drawable icon = ResourcesCompat.getDrawable(getResources(), isChecked ? R.drawable.ic_checked : R.drawable.ic_unchecked, null);
        this.button.setImageDrawable(icon);
//        this.button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        super.notifyValueChanged();
    }
}