package com.dunai.home.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.WorkspaceText;

public class TextWidgetEditActivity extends AppCompatActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private TextView span;
    private TextView suffix;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_renderer_edit);

        client = HomeClient.getInstance();

        title = findViewById(R.id.textRendererEditTitle);
        topic = findViewById(R.id.textRendererEditTopic);
        span = findViewById(R.id.textRendererEditSpan);
        suffix = findViewById(R.id.textRendererEditSuffix);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            WorkspaceText item = ((WorkspaceText) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            span.setText(String.valueOf(item.span));
            suffix.setText(item.suffix);
            this.setTitle("Edit text widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create text widget");
        }

        ((Button) findViewById(R.id.textRendererEditSave)).setOnClickListener((View.OnClickListener) v -> {
            TextView[] fields = {this.title, this.topic};
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
                        new WorkspaceText(
                                itemId,
                                title.getText().toString(),
                                topic.getText().toString(),
                                Integer.parseInt(span.getText().toString().equals("") ? "12" : span.getText().toString()),
                                suffix.getText().toString(),
                                null
                        )
                );
            } else {
                client.createItem(
                        new WorkspaceText(
                                String.valueOf(Math.round(Math.random() * 1e9)),
                                title.getText().toString(),
                                topic.getText().toString(),
                                Integer.parseInt(span.getText().toString().equals("") ? "12" : span.getText().toString()),
                                suffix.getText().toString(),
                                null
                        )
                );
            }
            TextWidgetEditActivity.this.finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}