package com.dunai.home;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class SectionRenderer extends LinearLayout {
    private WorkspaceSection workspaceSection;
    private TextView titleView;

    public SectionRenderer(Context context, WorkspaceSection workspaceSection) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.section_renderer, this, true);

        this.workspaceSection = workspaceSection;

        this.titleView = this.findViewById(R.id.sectionRendererTitle);
        this.titleView.setText(workspaceSection.title);
    }
}