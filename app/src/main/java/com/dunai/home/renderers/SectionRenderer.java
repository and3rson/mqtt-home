package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 12);
        this.setLayoutParams(params);
    }
}