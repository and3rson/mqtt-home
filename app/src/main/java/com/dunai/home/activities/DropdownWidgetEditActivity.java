package com.dunai.home.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.DropdownWidget;
import com.dunai.home.client.workspace.Widget;
import com.dunai.home.views.KeyValueView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropdownWidgetEditActivity extends AbstractWidgetEditActivity {
    private final ArrayList<DropdownWidget.KeyValue> keyValues = new ArrayList<>();
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
    private ListView list;
    private HomeClient client;
    private KeyValueAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dropdown_renderer_edit;
    }

    @Override
    protected String getType() {
        return "dropdown";
    }

    @Override
    protected List<TextView> getRequiredFields() {
        return Collections.emptyList();
    }

    @Override
    protected Widget construct(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor) {
        return new DropdownWidget(id, title, topic, retain, showTitle, showLastUpdate, spanPortrait, spanLandscape, bgColor, this.keyValues);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = findViewById(R.id.dropdownRendererEditList);

        findViewById(R.id.dropdownRendererEditAdd).setOnClickListener(v -> adapter.add(new DropdownWidget.KeyValue("", "")));

        DropdownWidget item = (DropdownWidget) getExisting();
        if (item != null) {
            keyValues.addAll(item.keyValues);
        } else {
            keyValues.add(new DropdownWidget.KeyValue("", ""));
        }

        adapter = new KeyValueAdapter(this, keyValues);
        list.setAdapter(adapter);
    }

    public static class KeyValueAdapter extends ArrayAdapter<DropdownWidget.KeyValue> {
        private final List<DropdownWidget.KeyValue> objects;

        public KeyValueAdapter(@NonNull Context context, @NonNull List<DropdownWidget.KeyValue> objects) {
            super(context, 0, objects);
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            KeyValueView view = new KeyValueView(getContext());

            final DropdownWidget.KeyValue keyValue = this.getItem(position);

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