package com.dunai.home.activities;

import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.workspace.GraphWidget;
import com.dunai.home.client.workspace.Widget;

import java.util.Collections;
import java.util.List;

public class GraphWidgetEditActivity extends AbstractWidgetEditActivity {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_graph_renderer_edit;
    }

    @Override
    protected String getType() {
        return "graph";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Collections.emptyList();
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor) {
        return new GraphWidget(id, title, topic, retain, showTitle, showLastUpdate, spanPortrait, spanLandscape, bgColor);
    }

    @Override
    protected boolean isRetainEditable() {
        return false;
    }
}