package com.dunai.home.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.workspace.GraphWidget;

public class GraphWidgetEditActivity extends AbstractEditActivity {
    private String itemId;
    private TextView title;
    private TextView topic;
    private SeekBar spanPortrait;
    private SeekBar spanLandscape;
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
        spanPortrait = findViewById(R.id.graphRendererEditSpanPortrait);
        spanLandscape = findViewById(R.id.graphRendererEditSpanLandscape);

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getStringExtra("item_id");
            GraphWidget item = ((GraphWidget) client.getItem(itemId));
            if (item == null) {
                finish();
                return;
            }
            title.setText(item.title);
            topic.setText(item.topic);
            spanPortrait.setProgress(item.spanPortrait - 1);
            spanLandscape.setProgress(item.spanLandscape - 1);
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
                    new GraphWidget(
                            itemId,
                            title.getText().toString(),
                            topic.getText().toString(),
                            false,
                            spanPortrait.getProgress() + 1,
                            spanLandscape.getProgress() + 1,
                            null
                    )
            );
        } else {
            client.createItem(
                    new GraphWidget(
                            String.valueOf(Math.round(Math.random() * 1e9)),
                            title.getText().toString(),
                            topic.getText().toString(),
                            false,
                            spanPortrait.getProgress() + 1,
                            spanLandscape.getProgress() + 1,
                            null
                    )
            );
        }
        GraphWidgetEditActivity.this.finish();
    }
}