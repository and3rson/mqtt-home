package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.Section;

public class SectionEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
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
        topic = findViewById(R.id.sectionRendererEditTopic);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            Section item = ((Section) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            if (item.topic != null) {
                topic.setText(item.topic);
            }
            this.setTitle(String.format(getString(R.string.section_s), item.title));
        } else {
            this.setTitle(getString(R.string.create_section));
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
                    new Section(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString().trim().length() > 0 ? topic.getText().toString() : null
                    )
            );
        } else {
            client.addItem(
                    new Section(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString().trim().length() > 0 ? topic.getText().toString() : null
                    )
            );
        }
        SectionEditActivity.this.finish();
    }
}