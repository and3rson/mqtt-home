package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.WorkspaceGraphWidget;
import com.dunai.home.client.workspace.WorkspaceTextWidget;

public class GraphWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar span;
    private HomeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_renderer_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = HomeClient.getInstance();

        title = findViewById(R.id.graphRendererEditTitle);
        topic = findViewById(R.id.graphRendererEditTopic);
        span = findViewById(R.id.graphRendererEditSpan);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            WorkspaceGraphWidget item = ((WorkspaceGraphWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            span.setProgress(item.span - 1);
            this.setTitle("Edit graph widget \"" + item.title + "\"");
        } else {
            this.setTitle("Create graph widget");
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
                    new WorkspaceGraphWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            null
                    )
            );
        } else {
            client.createItem(
                    new WorkspaceGraphWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            span.getProgress() + 1,
                            null
                    )
            );
        }
        GraphWidgetEditActivity.this.finish();
    }
}