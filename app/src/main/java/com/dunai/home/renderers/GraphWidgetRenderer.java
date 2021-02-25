package com.dunai.home.renderers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.core.content.res.ResourcesCompat;

import com.dunai.home.R;
import com.dunai.home.client.workspace.GraphWidget;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * TODO: document your custom view class.
 */
@SuppressLint("ViewConstructor")
public class GraphWidgetRenderer extends WidgetRenderer {
    private final GraphView graphView;

    public GraphWidgetRenderer(Context context, GraphWidget workspaceGraphWidget, String value) {
        super(context, workspaceGraphWidget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setForceDarkAllowed(false);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.graph_renderer, this.findViewById(R.id.rendererContainer), true);

        this.graphView = this.findViewById(R.id.graphRendererGraph);
        this.graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        this.graphView.getGridLabelRenderer().setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, getResources().getDisplayMetrics()));
        this.graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        this.graphView.getGridLabelRenderer().setGridColor(Color.parseColor("#444444"));
//        this.graphView.getGridLabelRenderer().setGridColor(ResourcesCompat.getColor(getResources(), R.color.gray_400, null));
//        graphView.getViewport().setScalable(true);
//        graphView.getViewport().setScalableY(true);
//        graphView.getViewport().setMaxXAxisSize(50);
//        graphView.getLegendRenderer().setVisible(false);
//        graphView.setTitle("asd");

        if (value != null) {
            this.setValue(value);
        }
    }

    public void setValue(String value) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        double min = 0;
        double max = 0;
        int count = 0;
        boolean hadErrors = false;
        graphView.removeAllSeries();
        graphView.setTitle("");
        if (value != null) {
            String[] parts = value.split(",");
            for (int x = 0; x < parts.length; x++) {
                double num = 0;
                try {
                    num = Double.parseDouble(parts[x]);
                } catch (NumberFormatException e) {
                    if (!hadErrors) {
                        graphView.setTitle("Error in item " + x);
                        hadErrors = true;
                    }
                }
                series.appendData(new DataPoint(x, num), true, 128);
                min = Math.min(min, num);
                max = Math.max(max, num);
                count++;
            }
        }
        try {
            series.setColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, null));
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 2, getResources().getDisplayMetrics()));
//            series.setAnimated(true);
            this.graphView.addSeries(series);
            super.notifyValueChanged();

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(count - 1);
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setMinY(min);
            graphView.getViewport().setMaxY(max);

            graphView.getSecondScale().setMinY(min);
            graphView.getSecondScale().setMaxY(max);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}