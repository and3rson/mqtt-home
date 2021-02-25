package com.dunai.home.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dunai.home.R;
import com.dunai.home.client.workspace.ButtonWidget;
import com.dunai.home.client.workspace.Widget;
import com.dunai.home.views.KeyValueView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonWidgetEditActivity extends AbstractWidgetEditActivity {
    private final ArrayList<ButtonWidget.KeyValue> keyValues = new ArrayList<>();
    private LinearLayout list;
    private ToggleButton orientation;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_button_renderer_edit;
    }

    @Override
    protected String getType() {
        return "button";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Collections.emptyList();
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor) {
        ButtonWidget.Orientation orientation = this.orientation.isChecked() ? ButtonWidget.Orientation.VERTICAL : ButtonWidget.Orientation.HORIZONTAL;
        return new ButtonWidget(id, title, topic, retain, showTitle, showLastUpdate, spanPortrait, spanLandscape, bgColor, keyValues, orientation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = findViewById(R.id.buttonRendererEditList);
        orientation = findViewById(R.id.buttonRendererEditOrientation);

        findViewById(R.id.buttonRendererEditAdd).setOnClickListener(v -> {
            this.keyValues.add(new ButtonWidget.KeyValue("", ""));
            renderItems();
        });

        ButtonWidget item = (ButtonWidget) getExisting();
        if (item != null) {
            keyValues.addAll(item.keyValues);
            orientation.setChecked(item.orientation == ButtonWidget.Orientation.VERTICAL);
        } else {
            keyValues.add(new ButtonWidget.KeyValue("", ""));
        }
        renderItems();
    }

    private void renderItems() {
        this.list.removeAllViews();
        for(int position = 0; position < this.keyValues.size(); position++) {
            ButtonWidget.KeyValue keyValue = this.keyValues.get(position);
            KeyValueView view = new KeyValueView(this);
            view.setKey(keyValue.getKey());
            view.setValue(keyValue.getValue());
            int finalPosition = position;

            view.setOnMoveUpRequested(v -> {
                if (finalPosition > 0) {
                    Collections.swap(this.keyValues, finalPosition, finalPosition - 1);
                    renderItems();
                }
            });
            view.setOnMoveDownRequested(v -> {
                if (finalPosition < this.keyValues.size() - 1) {
                    Collections.swap(this.keyValues, finalPosition, finalPosition + 1);
                    renderItems();
                }
            });
            view.setOnKeyValueChangedListener((key, value) -> {
                keyValue.setKey(key);
                keyValue.setValue(value);
            });
            view.setOnDeleteRequestedListener(v -> {
                if (this.keyValues.size() > 1) {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm deletion")
                            .setMessage("Do you want to delete this item?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                this.keyValues.remove(keyValue);
                                renderItems();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
            this.list.addView(view);
        }
    }
}