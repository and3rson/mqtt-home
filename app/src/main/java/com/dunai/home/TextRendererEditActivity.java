package com.dunai.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TextRendererEditActivity extends AppCompatActivity {
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
            this.setTitle("Edit text \"" + item.title + "\"");
        } else {
            this.setTitle("Create text");
        }

        ((Button) findViewById(R.id.textRendererEditSave)).setOnClickListener((View.OnClickListener) v -> {
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
                                title.getText().toString().toLowerCase().replace(" ", "_"),
                                title.getText().toString(),
                                topic.getText().toString(),
                                Integer.parseInt(span.getText().toString().equals("") ? "12" : span.getText().toString()),
                                suffix.getText().toString(),
                                null
                        )
                );
            }
            TextRendererEditActivity.this.finish();
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