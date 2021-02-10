package com.dunai.home.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ButtonWidget;
import com.dunai.home.views.KeyValueView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar span;
    private ListView list;
    private ToggleButton orientation;
    private HomeClient client;

    private ArrayList<ButtonWidget.KeyValue> keyValues = new ArrayList<>();

    private ButtonWidgetEditActivity.KeyValueAdapter adapter;

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.buttonRendererEditTitle);
        topic = findViewById(R.id.buttonRendererEditTopic);
        retain = findViewById(R.id.buttonRendererEditRetain);
        span = findViewById(R.id.buttonRendererEditSpan);
        list = findViewById(R.id.buttonRendererEditList);
        orientation = findViewById(R.id.buttonRendererEditOrientation);

        ((Button) findViewById(R.id.buttonRendererEditAdd)).setOnClickListener((View.OnClickListener) v -> adapter.add(new ButtonWidget.KeyValue("", "")));

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            ButtonWidget item = ((ButtonWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            retain.setChecked(item.retain);
            span.setProgress(item.span - 1);
            this.keyValues.addAll(item.keyValues);
            orientation.setChecked(item.orientation == ButtonWidget.Orientation.VERTICAL);
            this.setTitle("Edit button widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create button widget");
            keyValues.add(new ButtonWidget.KeyValue("", ""));
        }

        this.adapter = new ButtonWidgetEditActivity.KeyValueAdapter(this, this.keyValues);
        list.setAdapter(this.adapter);
    }

    @Override
    void onSavePressed() {
        TextView[] fields = {this.topic};
        boolean errors = false;
        for (TextView field : fields) {
            if (field.getText().length() == 0) {
                field.setError("This field is required.");
                errors = true;
            } else {
                field.setError(null);
            }
        }
        if (errors) {
            return;
        }

        if (itemId != null) {
            client.updateItem(
                    itemId,
                    new ButtonWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            span.getProgress() + 1,
                            null,
                            keyValues,
                            orientation.isChecked() ? ButtonWidget.Orientation.VERTICAL : ButtonWidget.Orientation.HORIZONTAL
                    )
            );
        } else {
            client.createItem(
                    new ButtonWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            span.getProgress() + 1,
                            null,
                            keyValues,
                            orientation.isChecked() ? ButtonWidget.Orientation.VERTICAL : ButtonWidget.Orientation.HORIZONTAL
                    )
            );
        }
        ButtonWidgetEditActivity.this.finish();
    }
}