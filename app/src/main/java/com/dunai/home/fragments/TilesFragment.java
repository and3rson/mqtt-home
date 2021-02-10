package com.dunai.home.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.dunai.home.activities.ButtonWidgetEditActivity;
import com.dunai.home.activities.ColorWidgetEditActivity;
import com.dunai.home.activities.DropdownWidgetEditActivity;
import com.dunai.home.activities.GraphWidgetEditActivity;
import com.dunai.home.activities.SectionEditActivity;
import com.dunai.home.activities.SwitchWidgetEditActivity;
import com.dunai.home.activities.TextWidgetEditActivity;
import com.dunai.home.client.HomeClient;
import com.dunai.home.client.Workspace;
import com.dunai.home.client.workspace.ButtonWidget;
import com.dunai.home.client.workspace.ColorWidget;
import com.dunai.home.client.workspace.DropdownWidget;
import com.dunai.home.client.workspace.GraphWidget;
import com.dunai.home.client.workspace.Item;
import com.dunai.home.client.workspace.Section;
import com.dunai.home.client.workspace.SwitchWidget;
import com.dunai.home.client.workspace.TextWidget;
import com.dunai.home.client.workspace.Widget;
import com.dunai.home.renderers.ButtonWidgetRenderer;
import com.dunai.home.renderers.ColorWidgetRenderer;
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
            this.renderWorkspace();
        });
        this.workspace = HomeClient.getInstance().getWorkspace();
        client.setDataReceivedListener((topic, payload) -> {
//            Log.i("HomeApp", topic + " -> " + payload);
            topicValueMap.put(topic, payload);
            ArrayList<WidgetRenderer> renderers = topicRenderersMap.get(topic);
            if (renderers != null) {
                renderers.forEach(renderer -> renderer.setValue(payload));
            }
        });
    }

    private void renderWorkspace() {
        GridLayout tiles = getView().findViewById(R.id.tiles);
        if (this.workspace == null) {
            return;
        }
        try {
            tiles.removeAllViews();
            topicRenderersMap.clear();
            Log.i("HomeApp", "Workspace: " + workspace);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

            Log.i("HomeApp", "Creating tiles: " + String.valueOf(workspace.items.size()));
            for (int i = 0; i < workspace.items.size(); i++) {
                Item item = workspace.items.get(i);
                View renderer;
                if (item instanceof Section) {
                    renderer = new SectionRenderer(getContext(), (Section) item);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 12);
                    renderer.setLayoutParams(params);
                } else {
                    final String topic = this.topicValueMap.get((((Widget) item).topic));
                    if (item instanceof TextWidget) {
                        renderer = new TextWidgetRenderer(getContext(), (TextWidget) item, topic);
                    } else if (item instanceof SwitchWidget) {
                        renderer = new SwitchWidgetRenderer(getContext(), (SwitchWidget) item, topic);
                    } else if (item instanceof GraphWidget) {
                        renderer = new GraphWidgetRenderer(getContext(), (GraphWidget) item, topic);
                    } else if (item instanceof DropdownWidget) {
                        renderer = new DropdownWidgetRenderer(getContext(), (DropdownWidget) item, topic);
                    } else if (item instanceof ColorWidget) {
                        renderer = new ColorWidgetRenderer(getContext(), (ColorWidget) item, topic);
                    } else if (item instanceof ButtonWidget) {
                        renderer = new ButtonWidgetRenderer(getContext(), (ButtonWidget) item, topic);
                    } else {
                        throw new Exception("Unknown item type: " + item.type);
                    }
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, ((Widget) item).span);
                    params.width = metrics.widthPixels * ((Widget) item).span / 12;
                    renderer.setLayoutParams(params);
                    ArrayList<WidgetRenderer> renderers = topicRenderersMap.get(((Widget) item).topic);
                    if (renderers == null) {
                        renderers = new ArrayList<>();
                    }
                    renderers.add((WidgetRenderer) renderer);
                    topicRenderersMap.put(((Widget) item).topic, renderers);
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.topicValueMap = (HashMap<String, String>) savedInstanceState.getSerializable("topicValueMap");
        }
        this.renderWorkspace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tiles, container, false);

        this.workspace = HomeClient.getInstance().getWorkspace();

        ((FloatingActionButton) view.findViewById(R.id.tilesFab)).setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final String[] items = {
                    "Section",
                    "Text widget",
                    "Switch widget",
                    "Graph widget",
                    "Dropdown widget",
                    "Color widget",
                    "Button widget"
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
                } else if (which == 5) {
                    cls = ColorWidgetEditActivity.class;
                } else if (which == 6) {
                    cls = ButtonWidgetEditActivity.class;
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
                Item workspaceItem = client.getItem(workspaceItemId);
                Class<? extends AppCompatActivity> cls;
                if (workspaceItem instanceof Section) {
                    cls = SectionEditActivity.class;
                } else if (workspaceItem instanceof TextWidget) {
                    cls = TextWidgetEditActivity.class;
                } else if (workspaceItem instanceof SwitchWidget) {
                    cls = SwitchWidgetEditActivity.class;
                } else if (workspaceItem instanceof GraphWidget) {
                    cls = GraphWidgetEditActivity.class;
                } else if (workspaceItem instanceof DropdownWidget) {
                    cls = DropdownWidgetEditActivity.class;
                } else if (workspaceItem instanceof ColorWidget) {
                    cls = ColorWidgetEditActivity.class;
                } else if (workspaceItem instanceof ButtonWidget) {
                    cls = ButtonWidgetEditActivity.class;
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("topicValueMap", this.topicValueMap);
        super.onSaveInstanceState(outState);
    }
}