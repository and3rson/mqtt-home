package com.dunai.home.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.workspace.WorkspaceSection;

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