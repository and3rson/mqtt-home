package com.dunai.home.renderers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.workspace.Section;

/**
 * TODO: document your custom view class.
 */
public class SectionRenderer extends LinearLayout {
    private final Section workspaceSection;
    private final TextView titleView;

    public SectionRenderer(Context context, Section workspaceSection) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.section_renderer, this, true);

        this.workspaceSection = workspaceSection;

        this.titleView = this.findViewById(R.id.sectionRendererTitle);
        this.titleView.setText(workspaceSection.title);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 12);
        params.width = metrics.widthPixels;
        this.setLayoutParams(params);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("showMenuDots", true)) {
            findViewById(R.id.sectionMoreButton).setVisibility(GONE);
        }
        findViewById(R.id.sectionMoreButton).setOnClickListener(v -> showContextMenu());
    }
}