package com.dunai.home.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ButtonWidget;
import com.dunai.home.client.workspace.Widget;
import com.dunai.home.views.KeyValueView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonWidgetEditActivity extends AbstractWidgetEditActivity {
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private final ArrayList<ButtonWidget.KeyValue> keyValues = new ArrayList<>();
    private String itemId;
    private ListView list;
    private ToggleButton orientation;
    private HomeClient client;
    private ButtonWidgetEditActivity.KeyValueAdapter adapter;

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

    public void justifyListViewHeightBasedOnChildren (ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = findViewById(R.id.buttonRendererEditList);
        orientation = findViewById(R.id.buttonRendererEditOrientation);

        findViewById(R.id.buttonRendererEditAdd).setOnClickListener(v -> adapter.add(new ButtonWidget.KeyValue("", "")));

        ButtonWidget item = (ButtonWidget) getExisting();
        if (item != null) {
            keyValues.addAll(item.keyValues);
            orientation.setChecked(item.orientation == ButtonWidget.Orientation.VERTICAL);
        } else {
            keyValues.add(new ButtonWidget.KeyValue("", ""));
        }

        adapter = new KeyValueAdapter(this, keyValues);
        list.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(list);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                justifyListViewHeightBasedOnChildren(list);
                super.onChanged();
            }
        });
    }

    public static class KeyValueAdapter extends ArrayAdapter<ButtonWidget.KeyValue> {
        private final List<ButtonWidget.KeyValue> objects;

        public KeyValueAdapter(@NonNull Context context, @NonNull List<ButtonWidget.KeyValue> objects) {
            super(context, 0, objects);
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            KeyValueView view = new KeyValueView(getContext());

            final ButtonWidget.KeyValue keyValue = this.getItem(position);

            view.setKey(keyValue.getKey());
            view.setValue(keyValue.getValue());

            view.setOnMoveUpRequested(v -> {
                if (position > 0) {
                    Collections.swap(objects, position, position - 1);
                    notifyDataSetChanged();
                }
            });
            view.setOnMoveDownRequested(v -> {
                if (position < getCount() - 1) {
                    Collections.swap(objects, position, position + 1);
                    notifyDataSetChanged();
                }
            });
            view.setOnKeyValueChangedListener((key, value) -> {
                keyValue.setKey(key);
                keyValue.setValue(value);
            });
            view.setOnDeleteRequestedListener(v -> {
                if (getCount() > 1) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm deletion")
                            .setMessage("Do you want to delete this item?")
                            .setPositiveButton("Yes", (dialog, which) -> remove(keyValue))
                            .setNegativeButton("No", null)
                            .show();
                }
            });
            return view;
        }
    }
}