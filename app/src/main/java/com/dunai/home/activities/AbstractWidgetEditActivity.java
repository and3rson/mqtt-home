package com.dunai.home.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.Widget;

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
    private CheckBox showTitle;
    private CheckBox showLastUpdate;
    private Widget existing;

    protected abstract int getLayoutResource();

    protected abstract String getType();

    protected abstract List<TextView> getRequiredFields();

    protected abstract Widget construct(String id, String title, String topic, boolean retain, boolean showTitle, boolean showLastUpdate, int spanPortrait, int spanLandscape, String bgColor);

    protected boolean isRetainEditable() {
        return true;
    }

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
        retain = findViewById(R.id.rendererEditRetain);
        if (!isRetainEditable()) {
            retain.setVisibility(View.GONE);
        }
        showTitle = findViewById(R.id.rendererEditShowTitle);
        showLastUpdate = findViewById(R.id.rendererEditShowLastUpdate);
        spanPortrait = findViewById(R.id.rendererEditSpanPortrait);
        spanLandscape = findViewById(R.id.rendererEditSpanLandscape);

        Intent intent = getIntent();
        Resources res = getResources();
        String type = res.getString(res.getIdentifier("w_" + this.getType(), "string", getPackageName()));
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            Widget item = ((Widget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            if (isRetainEditable()) {
                retain.setChecked(item.retain);
            }
            showTitle.setChecked(item.showTitle);
            showLastUpdate.setChecked(item.showLastUpdate);
            spanPortrait.setProgress(item.spanPortrait - 1);
            spanLandscape.setProgress(item.spanLandscape - 1);
            this.existing = item;
            this.setTitle(String.format(getString(R.string.edit_widget_s), type, item.title));
        } else {
            this.setTitle(String.format(getString(R.string.create_widget_s), type));
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
                    isRetainEditable() && retain.isChecked(),
                    showTitle.isChecked(),
                    showLastUpdate.isChecked(),
                    spanPortrait.getProgress() + 1,
                    spanLandscape.getProgress() + 1,
                    null
            ));
        } else {
            this.client.addItem(this.construct(
                    String.valueOf(Math.round(Math.random() * 1e9)),
                    title.getText().toString(),
                    topic.getText().toString(),
                    isRetainEditable() && retain.isChecked(),
                    showTitle.isChecked(),
                    showLastUpdate.isChecked(),
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
                field.setError(getString(R.string.field_required));
                errors.set(true);
            } else {
                field.setError(null);
            }
        });
        return !errors.get();
    }

    protected @Nullable
    Widget getExisting() {
        return this.existing;
    }
}
