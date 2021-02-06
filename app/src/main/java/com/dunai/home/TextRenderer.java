package com.dunai.home;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class TextRenderer extends LinearLayout {
    private WorkspaceText workspaceText;

    private TextView titleView;
    private TextView valueView;
    private TextView suffixView;
    private TextView lastUpdate;
    private TextView pin;

    private int bgColor;
    private Date lastUpdateDate;
    private Timer lastUpdateTimer;

    public TextRenderer(Context context, WorkspaceText workspaceText, String value) {
        super(context);
        this.workspaceText = workspaceText;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_renderer, this, true);

        this.titleView = this.findViewById(R.id.textRendererTitle);
        this.valueView = this.findViewById(R.id.textRendererValue);
        this.suffixView = this.findViewById(R.id.textRendererSuffix);
        this.lastUpdate = this.findViewById(R.id.textRendererLastUpdate);
        this.pin = this.findViewById(R.id.textRendererPin);

        if (this.workspaceText.bgColor != null) {
            this.bgColor = Color.parseColor(this.workspaceText.bgColor);
        } else {
            this.bgColor = Color.parseColor("#FF282828");
        }
        if (this.workspaceText.suffix != null) {
            this.suffixView.setText(this.workspaceText.suffix);
        }
        this.refreshLastUpdate();
        this.setBackgroundColor(this.bgColor);

        this.titleView.setText(this.workspaceText.title);
        if (value != null) {
            this.setValue(value);
        }
        this.pin.setAlpha(0);

        this.lastUpdateTimer = new Timer();
        this.lastUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshLastUpdate();
            }
        }, 0, 1000);
    }

    public void setValue(String value) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#FF6200EE"), this.bgColor);
        colorAnimator.setDuration(600);
        colorAnimator.addUpdateListener(animation -> {
            setBackgroundColor((Integer) animation.getAnimatedValue());
        });
        colorAnimator.start();
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0, 1, 0);
        alphaAnimator.setDuration(1200);
        alphaAnimator.addUpdateListener(animation -> {
            TextRenderer.this.pin.setAlpha((Float) animation.getAnimatedValue());
//            TextRenderer.this.setAlpha(1.0f - (Float) animation.getAnimatedValue());
        });
        alphaAnimator.start();
        this.valueView.setText(value);
        this.lastUpdateDate = new Date();
        this.refreshLastUpdate();
    }

    private void refreshLastUpdate() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (this.lastUpdateDate != null) {
                long diff = (new Date().getTime() - this.lastUpdateDate.getTime()) / 1000;
                if (diff < 10) {
                    this.lastUpdate.setText("just now");
                } else {
                    this.lastUpdate.setText("" + diff + " s ago");
                }
                this.lastUpdate.setTextColor(Color.parseColor("#777777"));
            } else {
                this.lastUpdate.setText("never");
                this.lastUpdate.setTextColor(Color.parseColor("#CC0000"));
            }
        });
//        this.lastUpdate.setText(
//                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()).toString()
//        );
    }
}