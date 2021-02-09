package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.WorkspaceSwitchWidget;

public class SwitchWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar span;
    private TextView onValue;
    private TextView offValue;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.switchRendererEditTitle);
        topic = findViewById(R.id.switchRendererEditTopic);
        span = findViewById(R.id.switchRendererEditSpan);
        onValue = findViewById(R.id.switchRendererEditOnValue);
        offValue = findViewById(R.id.switchRendererEditOffValue);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            WorkspaceSwitchWidget item = ((WorkspaceSwitchWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            span.setProgress(item.span - 1);
            onValue.setText(item.onValue);
            offValue.setText(item.offValue);
            this.setTitle("Edit switch widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create switch widget");
        }
    }

    @Override
    void onSavePressed() {
        TextView[] fields = {this.topic, this.onValue, this.offValue};
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
                    new WorkspaceSwitchWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            onValue.getText().toString(),
                            offValue.getText().toString(),
                            null
                    )
            );
        } else {
            client.createItem(
                    new WorkspaceSwitchWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            onValue.getText().toString(),
                            offValue.getText().toString(),
                            null
                    )
            );
        }
        SwitchWidgetEditActivity.this.finish();
    }
}