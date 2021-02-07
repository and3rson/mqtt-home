package com.dunai.home.widgets;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunai.home.R;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Widget extends LinearLayout {
    private final Timer lastUpdateTimer;
    private LinearLayout root;
    private TextView title;
    private TextView pin;
    private TextView lastUpdate;

    private int bgColor;
    private Date lastUpdateDate;

    public Widget(Context context, String title, String bgColor) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renderer, this, true);

        this.title = this.findViewById(R.id.rendererTitle);
        this.root = this.findViewById(R.id.rendererRoot);
        this.pin = this.findViewById(R.id.rendererPin);
        this.lastUpdate = this.findViewById(R.id.rendererLastUpdate);

        this.title.setText(title);

        if (bgColor != null) {
            this.bgColor = Color.parseColor(bgColor);
        } else {
            this.bgColor = Color.parseColor("#FF282828");
        }
        this.root.setBackgroundColor(this.bgColor);
        this.refreshLastUpdate();

        this.pin.setAlpha(0);

        this.lastUpdateTimer = new Timer();
        this.lastUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshLastUpdate();
            }
        }, 0, 1000);
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
//                this.lastUpdate.setText("never");
//                this.lastUpdate.setTextColor(Color.parseColor("#CC0000"));
                this.lastUpdate.setText("");
//                this.lastUpdate.setTextColor(Color.parseColor("#CC0000"));
            }
        });
//        this.lastUpdate.setText(
//                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()).toString()
//        );
    }

    public void notifyValueChanged() {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#FF6200EE"), this.bgColor);
        colorAnimator.setDuration(600);
        colorAnimator.addUpdateListener(animation -> {
            this.root.setBackgroundColor((Integer) animation.getAnimatedValue());
        });
        colorAnimator.start();
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(1, 1, 0);
        alphaAnimator.setDuration(1500);
        alphaAnimator.addUpdateListener(animation -> {
            this.pin.setAlpha((Float) animation.getAnimatedValue());
//            TextRenderer.this.setAlpha(1.0f - (Float) animation.getAnimatedValue());
        });
        alphaAnimator.start();
        this.lastUpdateDate = new Date();
        this.refreshLastUpdate();
    }
}
