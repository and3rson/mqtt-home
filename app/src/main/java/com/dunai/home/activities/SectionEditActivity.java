package com.dunai.home.activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.WorkspaceSection;

public class SectionEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

    @Override
    void onSavePressed() {
        TextView[] fields = {this.title};
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
                    new WorkspaceSection(
                            itemId,
                            title.getText().toString()
                    )
            );
        } else {
            client.createItem(
                    new WorkspaceSection(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString()
                    )
            );
        }
        SectionEditActivity.this.finish();
    }
}