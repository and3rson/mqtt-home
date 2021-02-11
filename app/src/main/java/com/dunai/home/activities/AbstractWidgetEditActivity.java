package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.Widget;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class AbstractWidgetEditActivity extends AbstractEditActivity {
    private HomeClient client;
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
    private Widget existing;

    protected abstract int getLayoutResource();
    protected abstract String getType();
    protected abstract List<TextView> getRequiredFields();
    protected abstract Widget construct(String id, String title, String topic, boolean retain, int spanPortrait, int spanLandscape, String bgColor);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutResource());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.client = HomeClient.getInstance();

        title = findViewById(R.id.rendererEditTitle);
        topic = findViewById(R.id.rendererEditTopic);
        retain = findViewById(R.id.rendererEditRetain); // Can be null. If absent in layout, this means that this widget does not publish any data.
        spanPortrait = findViewById(R.id.rendererEditSpanPortrait);
        spanLandscape = findViewById(R.id.rendererEditSpanLandscape);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            Widget item = ((Widget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            if (retain != null) {
                retain.setChecked(item.retain);
            }
            spanPortrait.setProgress(item.spanPortrait - 1);
            spanLandscape.setProgress(item.spanLandscape - 1);
            this.existing = item;
            this.setTitle("Edit " + this.getType() +  "widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create " + this.getType() + " widget");
        }
    }

    protected void onSavePressed() {
        if (!this.validateFields()) {
            return;
        }
        if (itemId != null) {
            this.client.updateItem(itemId, this.construct(
                    itemId,
                    title.getText().toString(),
                    topic.getText().toString(),
                    retain != null && retain.isChecked(),
                    spanPortrait.getProgress() + 1,
                    spanLandscape.getProgress() + 1,
                    null
            ));
        } else {
            this.client.createItem(this.construct(
                    String.valueOf(Math.round(Math.random() * 1e9)),
                    title.getText().toString(),
                    topic.getText().toString(),
                    retain != null && retain.isChecked(),
                    spanPortrait.getProgress() + 1,
                    spanLandscape.getProgress() + 1,
                    null
            ));
        }
        this.finish();
    }

    protected boolean validateFields() {
        AtomicBoolean errors = new AtomicBoolean(false);
        Stream.concat(Stream.of(this.title), this.getRequiredFields().stream()).forEach(field -> {
            if (field.getText().length() == 0) {
                field.setError("This field is required.");
                errors.set(true);
            } else {
                field.setError(null);
            }
        });
        return !errors.get();
    }

    protected @Nullable Widget getExisting() {
        return this.existing;
    }
}
