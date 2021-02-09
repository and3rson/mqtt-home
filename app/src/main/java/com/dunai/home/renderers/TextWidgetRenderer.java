package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.workspace.WorkspaceTextWidget;

/**
 * TODO: document your custom view class.
 */
public class TextWidgetRenderer extends WidgetRenderer {
    private WorkspaceTextWidget workspaceTextWidget;

    private TextView valueView;
    private TextView suffixView;

    public TextWidgetRenderer(Context context, WorkspaceTextWidget workspaceTextWidget, String value) {
        super(context, workspaceTextWidget);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceTextWidget = workspaceTextWidget;

        this.valueView = this.findViewById(R.id.textRendererValue);
        this.suffixView = this.findViewById(R.id.textRendererSuffix);

        if (value != null) {
            this.setValue(value);
        }
        this.suffixView.setText("");
    }

    public void setValue(String value) {
        this.valueView.setText(value);
        if (this.workspaceTextWidget.suffix != null) {
            this.suffixView.setText(this.workspaceTextWidget.suffix);
        }
        super.notifyValueChanged();
    }
}