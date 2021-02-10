package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.ButtonWidget;

public class ButtonWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private CheckBox retain;
    private SeekBar span;
    private TextView caption;
    private TextView payload;
    private HomeClient client;

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
        caption = findViewById(R.id.buttonRendererEditCaption);
        payload = findViewById(R.id.buttonRendererEditPayload);

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
            caption.setText(item.caption);
            payload.setText(item.payload);
            this.setTitle("Edit button widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create button widget");
        }
    }

    @Override
    void onSavePressed() {
        TextView[] fields = {this.topic, this.caption, this.payload};
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
                            caption.getText().toString(),
                            payload.getText().toString()
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
                            caption.getText().toString(),
                            payload.getText().toString()
                    )
            );
        }
        ButtonWidgetEditActivity.this.finish();
    }
}