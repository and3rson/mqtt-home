package com.dunai.home.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.WorkspaceText;

import java.util.Date;
import java.util.Timer;

/**
 * TODO: document your custom view class.
 */
public class TextWidget extends Widget {
    private WorkspaceText workspaceText;

    private LinearLayout root;

    private TextView titleView;
    private TextView valueView;
    private TextView suffixView;
    private TextView lastUpdate;
    private TextView pin;

    private int bgColor;
    private Date lastUpdateDate;
    private Timer lastUpdateTimer;

    public ViewGroup getRoot() {
        return findViewById(R.id.rendererRoot);
    }

    public ViewGroup getPin() {
        return findViewById(R.id.rendererPin);
    }

    public TextWidget(Context context, WorkspaceText workspaceText, String value) {
        super(context, workspaceText.title, workspaceText.bgColor);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_renderer, this.findViewById(R.id.rendererContainer), true);

        this.workspaceText = workspaceText;

        this.titleView = this.findViewById(R.id.rendererTitle);
        this.valueView = this.findViewById(R.id.textRendererValue);
        this.suffixView = this.findViewById(R.id.textRendererSuffix);
        this.lastUpdate = this.findViewById(R.id.rendererLastUpdate);

        this.titleView.setText(this.workspaceText.title);
        if (value != null) {
            this.setValue(value);
        }
        this.suffixView.setText("");
    }

    public void setValue(String value) {
        this.valueView.setText(value);
        if (this.workspaceText.suffix != null) {
            this.suffixView.setText(this.workspaceText.suffix);
        }
        super.notifyValueChanged();
    }
}