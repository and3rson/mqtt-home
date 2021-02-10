package com.dunai.home.activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.TextWidget;

public class TextWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar span;
    private TextView suffix;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.textRendererEditTitle);
        topic = findViewById(R.id.textRendererEditTopic);
        span = findViewById(R.id.textRendererEditSpan);
        suffix = findViewById(R.id.textRendererEditSuffix);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            TextWidget item = ((TextWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            span.setProgress(item.span - 1);
            suffix.setText(item.suffix);
            this.setTitle("Edit text widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create text widget");
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
                    new TextWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            false,
                            span.getProgress() + 1,
                            null,
                            suffix.getText().toString()
                    )
            );
        } else {
            client.createItem(
                    new TextWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            false,
                            span.getProgress() + 1,
                            null,
                            suffix.getText().toString()
                    )
            );
        }
        TextWidgetEditActivity.this.finish();
    }
}