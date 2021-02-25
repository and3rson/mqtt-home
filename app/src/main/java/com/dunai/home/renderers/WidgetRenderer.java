package com.dunai.home.renderers;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.workspace.Widget;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WidgetRenderer extends AbstractRenderer {
    private final LinearLayout root;
    private final TextView pin;
    private final TextView lastUpdate;

    private final int bgColor;
    private Date lastUpdateDate;

    public WidgetRenderer(Context context, Widget widget) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renderer, this, true);

        TextView title = this.findViewById(R.id.rendererTitle);
        this.root = this.findViewById(R.id.rendererRoot);
        this.pin = this.findViewById(R.id.rendererPin);
        this.lastUpdate = this.findViewById(R.id.rendererLastUpdate);

//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics metrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getRealMetrics(metrics);

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                int activityWidth = ((AppCompatActivity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, isPortrait ? widget.spanPortrait : widget.spanLandscape);
                params.width = activityWidth * (isPortrait ? widget.spanPortrait : widget.spanLandscape) / 12;
                setLayoutParams(params);
                return false;
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!widget.showLastUpdate) {
            this.lastUpdate.setVisibility(GONE);
        }

        if (widget.title.isEmpty() || !widget.showTitle) {
            findViewById(R.id.rendererTitleContainer).setVisibility(GONE);
        } else {
            title.setText(widget.title);
        }

        if (!prefs.getBoolean("showMenuDots", true)) {
            findViewById(R.id.rendererMoreButton).setVisibility(GONE);
        }

        findViewById(R.id.rendererMoreButton).setOnClickListener(v -> showContextMenu());

        if (widget.bgColor != null) {
            this.bgColor = Color.parseColor(widget.bgColor);
        } else {
            this.bgColor = Color.parseColor("#FF282828");
        }
        this.root.setBackgroundColor(this.bgColor);
        this.refreshLastUpdate();

        this.pin.setAlpha(0);

        Timer lastUpdateTimer = new Timer();
        lastUpdateTimer.schedule(new TimerTask() {
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
                    this.lastUpdate.setText(R.string.t_just_now);
                } else if (diff < 60) {
                    this.lastUpdate.setText(String.format(getContext().getString(R.string.t_sec_ago), diff));
                } else if (diff < 3600) {
                    this.lastUpdate.setText(String.format(getContext().getString(R.string.t_min_ago), diff / 60));
                } else if (diff < 86400) {
                    this.lastUpdate.setText(String.format(getContext().getString(R.string.t_hours_ago), diff / 3600));
                } else {
                    this.lastUpdate.setText(String.format(getContext().getString(R.string.t_days_ago), diff / 86400));
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
        colorAnimator.addUpdateListener(animation -> this.root.setBackgroundColor((Integer) animation.getAnimatedValue()));
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

    public abstract void setValue(String value);
}
