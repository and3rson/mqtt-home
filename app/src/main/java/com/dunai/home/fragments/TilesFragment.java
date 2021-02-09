package com.dunai.home.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
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

import com.dunai.home.R;
import com.dunai.home.activities.DropdownWidgetEditActivity;
import com.dunai.home.activities.GraphWidgetEditActivity;
import com.dunai.home.activities.SectionEditActivity;
import com.dunai.home.activities.SwitchWidgetEditActivity;
import com.dunai.home.activities.TextWidgetEditActivity;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.Workspace;
import com.dunai.home.client.workspace.WorkspaceDropdownWidget;
import com.dunai.home.client.workspace.WorkspaceGraphWidget;
import com.dunai.home.client.workspace.WorkspaceItem;
import com.dunai.home.client.workspace.WorkspaceSection;
import com.dunai.home.client.workspace.WorkspaceSwitchWidget;
import com.dunai.home.client.workspace.WorkspaceTextWidget;
import com.dunai.home.client.workspace.WorkspaceWidget;
import com.dunai.home.renderers.DropdownWidgetRenderer;
import com.dunai.home.renderers.GraphWidgetRenderer;
import com.dunai.home.renderers.SectionRenderer;
import com.dunai.home.renderers.SwitchWidgetRenderer;
import com.dunai.home.renderers.TextWidgetRenderer;
import com.dunai.home.renderers.WidgetRenderer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class TilesFragment extends Fragment {
    private HomeClient client;
    private Workspace workspace;
    private HashMap<String, ArrayList<WidgetRenderer>> topicRenderersMap = new HashMap<>();
    private HashMap<String, String> topicValueMap = new HashMap<>();

    public TilesFragment() {
        // Required empty public constructor
        this.client = HomeClient.getInstance();
        client.setWorkspaceChangedCallback(workspace -> {
            this.workspace = workspace;
            GridLayout tiles = getView().findViewById(R.id.tiles);
            try {
                tiles.removeAllViews();
                topicRenderersMap.clear();
                Log.i("HomeApp", "Workspace: " + workspace);

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

                Log.i("HomeApp", "Creating tiles: " + String.valueOf(workspace.items.size()));
                for (int i = 0; i < workspace.items.size(); i++) {
                    WorkspaceItem item = workspace.items.get(i);
                    View renderer;
                    if (item instanceof WorkspaceSection) {
                        renderer = new SectionRenderer(getContext(), (WorkspaceSection) item);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 12);
                        renderer.setLayoutParams(params);
                    } else {
                        if (item instanceof WorkspaceTextWidget) {
                            renderer = new TextWidgetRenderer(getContext(), (WorkspaceTextWidget) item, this.topicValueMap.get((((WorkspaceTextWidget) item).topic)));
                        } else if (item instanceof WorkspaceSwitchWidget) {
                            renderer = new SwitchWidgetRenderer(getContext(), (WorkspaceSwitchWidget) item, this.topicValueMap.get((((WorkspaceSwitchWidget) item).topic)));
                        } else if (item instanceof WorkspaceGraphWidget) {
                            renderer = new GraphWidgetRenderer(getContext(), (WorkspaceGraphWidget) item, this.topicValueMap.get((((WorkspaceGraphWidget) item).topic)));
                        } else if (item instanceof WorkspaceDropdownWidget) {
                            renderer = new DropdownWidgetRenderer(getContext(), (WorkspaceDropdownWidget) item, this.topicValueMap.get((((WorkspaceDropdownWidget) item).topic)));
                        } else {
                            throw new Exception("Unknown item type: " + item.type);
                        }
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, ((WorkspaceWidget) item).span);
                        params.width = metrics.widthPixels * ((WorkspaceWidget) item).span / 12;
                        renderer.setLayoutParams(params);
                        ArrayList<WidgetRenderer> renderers = topicRenderersMap.get(((WorkspaceWidget) item).topic);
                        if (renderers == null) {
                            renderers = new ArrayList<>();
                        }
                        renderers.add((WidgetRenderer) renderer);
                        topicRenderersMap.put(((WorkspaceWidget) item).topic, renderers);
                    }
                    tiles.addView(renderer);
                    registerForContextMenu(renderer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to populate workspace: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (this.workspace.items.size() == 0) {
                TextView noWorkspace = new TextView(this.getContext());
                noWorkspace.setText("No workspace items defined.");
                noWorkspace.setGravity(Gravity.CENTER);
                tiles.addView(noWorkspace, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        });
        client.setDataReceivedListener((topic, payload) -> {
//            Log.i("HomeApp", topic + " -> " + payload);
            topicValueMap.put(topic, payload);
            ArrayList<WidgetRenderer> renderers = topicRenderersMap.get(topic);
            if (renderers != null) {
                renderers.forEach(renderer -> renderer.setValue(payload));
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
                    "Text widget",
                    "Switch widget",
                    "Graph widget",
                    "Dropdown widget"
            };
            builder.setTitle("Create new item");
            builder.setItems(items, ((dialog, which) -> {
                Class<? extends AppCompatActivity> cls;
                if (which == 0) {
                    cls = SectionEditActivity.class;
                } else if (which == 1) {
                    cls = TextWidgetEditActivity.class;
                } else if (which == 2) {
                    cls = SwitchWidgetEditActivity.class;
                } else if (which == 3) {
                    cls = GraphWidgetEditActivity.class;
                } else if (which == 4) {
                    cls = DropdownWidgetEditActivity.class;
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
                if (workspaceItem instanceof WorkspaceSection) {
                    cls = SectionEditActivity.class;
                } else if (workspaceItem instanceof WorkspaceTextWidget) {
                    cls = TextWidgetEditActivity.class;
                } else if (workspaceItem instanceof WorkspaceSwitchWidget) {
                    cls = SwitchWidgetEditActivity.class;
                } else if (workspaceItem instanceof WorkspaceGraphWidget) {
                    cls = GraphWidgetEditActivity.class;
                } else if (workspaceItem instanceof WorkspaceDropdownWidget) {
                    cls = DropdownWidgetEditActivity.class;
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
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm deletion")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> client.deleteItem(workspaceItemId))
                        .setNegativeButton("No", null)
                        .show();
                break;
        }

        return super.onContextItemSelected(item);
    }


}