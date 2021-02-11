package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.SwitchWidget;

public class SwitchWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
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
        retain = findViewById(R.id.switchRendererEditRetain);
        spanPortrait = findViewById(R.id.switchRendererEditSpanPortrait);
        spanLandscape = findViewById(R.id.switchRendererEditSpanLandscape);
        onValue = findViewById(R.id.switchRendererEditOnValue);
        offValue = findViewById(R.id.switchRendererEditOffValue);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            SwitchWidget item = ((SwitchWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            retain.setChecked(item.retain);
            spanPortrait.setProgress(item.spanPortrait - 1);
            spanLandscape.setProgress(item.spanLandscape - 1);
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
                    new SwitchWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            spanPortrait.getProgress() + 1,
                            spanLandscape.getProgress() + 1,
                            null,
                            onValue.getText().toString(),
                            offValue.getText().toString()
                    )
            );
        } else {
            client.createItem(
                    new SwitchWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            spanPortrait.getProgress() + 1,
                            spanLandscape.getProgress() + 1,
                            null,
                            onValue.getText().toString(),
                            offValue.getText().toString()
                    )
            );
        }
        SwitchWidgetEditActivity.this.finish();
    }
}