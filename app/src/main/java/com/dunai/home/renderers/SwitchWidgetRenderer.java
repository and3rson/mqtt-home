package com.dunai.home.renderers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ToggleButton;

import androidx.core.content.res.ResourcesCompat;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.WorkspaceSwitchWidget;

/**
 * TODO: document your custom view class.
 */
public class SwitchWidgetRenderer extends WidgetRenderer {
    private WorkspaceSwitchWidget workspaceSwitchWidget;

    private ToggleButton button;

    public SwitchWidgetRenderer(Context context, WorkspaceSwitchWidget workspaceSwitchWidget, String value) {
        super(context, workspaceSwitchWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.switch_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceSwitchWidget = workspaceSwitchWidget;

        this.button = this.findViewById(R.id.switchRendererButton);

        if (value != null) {
            this.setValue(value);
        }

        this.button.setOnClickListener(v -> {
            HomeClient.getInstance().publish(
                    workspaceSwitchWidget.topic,
                    button.isChecked() ? workspaceSwitchWidget.onValue : workspaceSwitchWidget.offValue,
                    true
            );
        });
    }

    public void setValue(String value) {
        this.button.setChecked(value.equals(this.workspaceSwitchWidget.onValue));
        Drawable icon = ResourcesCompat.getDrawable(getResources(), this.button.isChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked, null);
        this.button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        super.notifyValueChanged();
    }
}