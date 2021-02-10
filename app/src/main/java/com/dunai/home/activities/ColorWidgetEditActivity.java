package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ColorWidget;

public class ColorWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private CheckBox retain;
    private TextView topic;
    private SeekBar span;
    private CheckBox alpha;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.colorRendererEditTitle);
        retain = findViewById(R.id.colorRendererEditRetain);
        topic = findViewById(R.id.colorRendererEditTopic);
        span = findViewById(R.id.colorRendererEditSpan);
        alpha = findViewById(R.id.colorRendererAlpha);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            ColorWidget item = ((ColorWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            retain.setChecked(item.retain);
            span.setProgress(item.span - 1);
            alpha.setChecked(item.alpha);
            this.setTitle("Edit color widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create color widget");
        }
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
                    new ColorWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            span.getProgress() + 1,
                            null,
                            "HTML", // TODO
                            alpha.isChecked()
                    )
            );
        } else {
            client.createItem(
                    new ColorWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            retain.isChecked(),
                            span.getProgress() + 1,
                            null,
                            "HTML", // TODO
                            alpha.isChecked()
                    )
            );
        }
        ColorWidgetEditActivity.this.finish();
    }
}