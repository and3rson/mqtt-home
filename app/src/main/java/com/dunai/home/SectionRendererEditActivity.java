package com.dunai.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SectionRendererEditActivity extends AppCompatActivity {
    private String itemId;
    private TextView title;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_renderer_edit);

        client = HomeClient.getInstance();

        title = findViewById(R.id.sectionRendererEditTitle);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            WorkspaceSection item = ((WorkspaceSection) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            this.setTitle("Edit section \"" + item.title + "\"");
        } else {
            this.setTitle("Create section");
        }

        ((Button) findViewById(R.id.sectionRendererEditSave)).setOnClickListener((View.OnClickListener) v -> {
            if (itemId != null) {
                client.updateItem(
                        itemId,
                        new WorkspaceSection(
                                itemId,
                                title.getText().toString()
                        )
                );
            } else {
                client.createItem(
                        new WorkspaceSection(
                                title.getText().toString().toLowerCase().replace(" ", "_"),
                                title.getText().toString()
                        )
                );
            }
            SectionRendererEditActivity.this.finish();
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