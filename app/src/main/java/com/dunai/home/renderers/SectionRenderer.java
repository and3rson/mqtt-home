package com.dunai.home.renderers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.workspace.Section;

import java.util.IllegalFormatConversionException;
import java.util.MissingFormatArgumentException;

/**
 * TODO: document your custom view class.
 */
@SuppressLint("ViewConstructor")
public class SectionRenderer extends AbstractRenderer {
    private final TextView titleView;
    private final Section workspaceSection;

    public SectionRenderer(Context context, Section workspaceSection, String value) {
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

        if (value != null) {
            this.setValue(value);
        }
    }

    public void setValue(String value) {
        try {
            this.titleView.setText(String.format(this.workspaceSection.title, value));
        } catch (MissingFormatArgumentException | IllegalFormatConversionException exc) {
            this.titleView.setText(this.workspaceSection.title);
            exc.printStackTrace();
        }
    }
}