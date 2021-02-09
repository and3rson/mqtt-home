package com.dunai.home.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.WorkspaceDropdownWidget;
import com.dunai.home.client.workspace.WorkspaceTextWidget;
import com.dunai.home.views.KeyValueView;
import com.google.android.material.textfield.TextInputEditText;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropdownWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar span;
    private ListView list;
    private HomeClient client;

    private ArrayList<WorkspaceDropdownWidget.KeyValue> keyValues = new ArrayList<>();

    private KeyValueAdapter adapter;

    public static class KeyValueAdapter extends ArrayAdapter<WorkspaceDropdownWidget.KeyValue> {
        private final List<WorkspaceDropdownWidget.KeyValue> objects;

        public KeyValueAdapter(@NonNull Context context, @NonNull List<WorkspaceDropdownWidget.KeyValue> objects) {
            super(context, 0, objects);
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            KeyValueView view = new KeyValueView(getContext());

            final WorkspaceDropdownWidget.KeyValue keyValue = this.getItem(position);

            System.out.println("INIT " + position);

            view.setKey(keyValue.getKey());
            view.setValue(keyValue.getValue());

            view.setOnMoveUpRequested(v -> {
                System.out.println("Up");
                if (position > 0) {
                    Collections.swap(objects, position, position - 1);
                    notifyDataSetChanged();
                }
            });
            view.setOnMoveDownRequested(v -> {
                System.out.println("Down");
                if (position < getCount() - 1) {
                    Collections.swap(objects, position, position + 1);
                    notifyDataSetChanged();
                }
            });
            view.setOnKeyValueChangedListener((key, value) -> {
                System.out.println("KV changed: " + key + " / " + value);
                keyValue.setKey(key);
                keyValue.setValue(value);
            });
            view.setOnDeleteRequestedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(keyValue);
                }
            });
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropdown_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.dropdownRendererEditTitle);
        topic = findViewById(R.id.dropdownRendererEditTopic);
        span = findViewById(R.id.dropdownRendererEditSpan);
        list = findViewById(R.id.dropdownRendererEditList);

        ((Button) findViewById(R.id.dropdownRendererEditAdd)).setOnClickListener((View.OnClickListener) v -> adapter.add(new WorkspaceDropdownWidget.KeyValue("", "")));

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            WorkspaceDropdownWidget item = ((WorkspaceDropdownWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            span.setProgress(item.span - 1);
            this.keyValues.addAll(item.keyValues);
            // TODO
            this.setTitle("Edit dropdown widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create dropdown widget");
        }

        this.adapter = new KeyValueAdapter(this, this.keyValues);
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
                    new WorkspaceDropdownWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            this.keyValues,
                            null
                    )
            );
        } else {
            client.createItem(
                    new WorkspaceDropdownWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            this.keyValues,
                            null
                    )
            );
        }
        DropdownWidgetEditActivity.this.finish();
    }
}