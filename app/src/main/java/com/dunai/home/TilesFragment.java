package com.dunai.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class TilesFragment extends Fragment {
    private HomeClient client;
    private Workspace workspace;
    private HashMap<String, View> topicRendererMap = new HashMap<>();
    private HashMap<String, String> topicValueMap = new HashMap<>();

    public TilesFragment() {
        // Required empty public constructor
        this.client = HomeClient.getInstance();
        client.setWorkspaceChangedCallback(workspace -> {
            this.workspace = workspace;
            GridLayout tiles = getView().findViewById(R.id.tiles);
            try {
                tiles.removeAllViews();
                Log.i("HomeApp", "Workspace: " + workspace);

                Log.i("HomeApp", "Creating tiles: " + String.valueOf(workspace.items.size()));
                for (int i = 0; i < workspace.items.size(); i++) {
                    WorkspaceItem item = workspace.items.get(i);
                    View renderer;
                    if (item instanceof WorkspaceSection) {
                        renderer = new SectionRenderer(getContext(), (WorkspaceSection) item);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 12, 12);
                        renderer.setLayoutParams(params);
                    } else if (item instanceof WorkspaceText) {
                        renderer = new TextRenderer(getContext(), (WorkspaceText) item, this.topicValueMap.get((((WorkspaceText) item).topic)));
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, ((WorkspaceText) item).span, (float) ((WorkspaceText) item).span);
                        //                        params.columnSpec = GridLayout.Spec(GridLayout.UNDEFINED, 6, GridLayout.ALIGN_BOUNDS, 1);
                        renderer.setLayoutParams(params);
                        topicRendererMap.put(((WorkspaceText) item).topic, renderer);
                    } else {
                        throw new Exception("Unknown item type: " + item.type);
                    }
//                    renderer.setOnLongClickListener(v -> true);
                    tiles.addView(renderer);
                    registerForContextMenu(renderer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to populate workspace:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (this.workspace.items.size() == 0) {
                TextView noWorkspace = new TextView(this.getContext());
                noWorkspace.setText("No workspace items defined.");
                noWorkspace.setGravity(Gravity.CENTER);
                tiles.addView(noWorkspace, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        });
        client.setDataReceivedListener((topic, payload) -> {
            Log.i("HomeApp", topic + " -> " + payload);
            topicValueMap.put(topic, payload);
            View view = topicRendererMap.get(topic);
            if (view != null) {
                ((TextRenderer) view).setValue(payload);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tiles, container, false);

        ((FloatingActionButton) view.findViewById(R.id.tilesFab)).setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final String[] items = {
                    "Section",
                    "Text"
            };
            builder.setTitle("Create new item");
            builder.setItems(items, ((dialog, which) -> {
                Class<? extends AppCompatActivity> cls;
                if (which == 0) {
                    cls = SectionRendererEditActivity.class;
                } else if (which == 1) {
                    cls = TextRendererEditActivity.class;
                } else {
                    cls = null;
                }
                if (cls != null) {
                    Intent intent = new Intent(getContext(), cls);
                    this.startActivity(intent);
                }
            }));
            builder.show();
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        Log.i("HomeApp", "" + v);
        Log.i("HomeApp", "" + v.getParent());
        Log.i("HomeApp", "" + v.getParent().getParent());
        int groupId = ((GridLayout) v.getParent()).indexOfChild(v);
        menu.add(groupId, 0, 0, "Move back");
        menu.add(groupId, 1, 0, "Move forth");
        menu.add(groupId, 2, 0, "Edit");
        menu.add(groupId, 3, 0, "Delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        Toast.makeText(getContext(), "Clicked item " + item.getItemId() + " on view " + item.getGroupId(), Toast.LENGTH_SHORT).show();
        int workspaceItemIndex = item.getGroupId();
        String workspaceItemId = this.workspace.items.get(workspaceItemIndex).id;
        switch (item.getItemId()) {
            case 0:
                // Move back
                client.moveBack(workspaceItemId);
                break;
            case 1:
                // Move forth
                client.moveForth(workspaceItemId);
                break;
            case 2:
                // Edit
                WorkspaceItem workspaceItem = client.getItem(workspaceItemId);
                Class<? extends AppCompatActivity> cls;
                if (workspaceItem instanceof WorkspaceText) {
                    cls = TextRendererEditActivity.class;
                } else if (workspaceItem instanceof WorkspaceSection) {
                    cls = SectionRendererEditActivity.class;
                } else {
                    cls = null;
                }
                if (cls != null) {
                    Intent intent = new Intent(getContext(), cls);
                    intent.putExtra("item_id", workspaceItemId);
                    this.startActivity(intent);
                }
                break;
            case 3:
                // Delete
                client.deleteItem(workspaceItemId);
                break;
        }

        return super.onContextItemSelected(item);
    }
}