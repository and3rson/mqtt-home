package com.dunai.home.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dunai.home.R;
import com.dunai.home.activities.reorder.RecyclerViewAdapter;
import com.dunai.home.activities.reorder.TouchHelperCallback;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.Workspace;
import com.dunai.home.client.workspace.Item;

public class ReorderItemsActivity extends AppCompatActivity {

    private RecyclerViewAdapter adapter;
    ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reorder_items);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (HomeClient.getInstance().getWorkspace() == null) {
            finish();
            return;
        }
        this.adapter = new RecyclerViewAdapter(this, HomeClient.getInstance().getWorkspace().items, viewHolder -> itemTouchHelper.startDrag(viewHolder));

        RecyclerView recyclerView = this.findViewById(R.id.reorderList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menuSave) {
            Workspace workspace = new Workspace();
            workspace.items = this.adapter.getItems();
            HomeClient.getInstance().publishWorkspace(workspace);
            finish();
        }
        return super.onContextItemSelected(item);
    }
}