package com.dunai.home.client;

import androidx.annotation.Nullable;

import com.dunai.home.client.workspace.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Workspace {
    public ArrayList<Item> items = new ArrayList<>();

    public JSONObject serialize() {
        JSONArray items = new JSONArray();
        JSONObject root = new JSONObject();
        for (int i = 0; i < this.items.size(); i++) {
            items.put(this.items.get(i).serialize());
        }
        try {
            root.put("items", items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    Workspace swapItems(int a, int b) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = new ArrayList<>(items);
        Item tmp = newWorkspace.items.get(b);
        newWorkspace.items.set(b, newWorkspace.items.get(a));
        newWorkspace.items.set(a, tmp);
        return newWorkspace;
    }

    Workspace addItem(Item item) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = new ArrayList<>(items);
        newWorkspace.items.add(item);
        return newWorkspace;
    }

    int findItem(String itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id.equals(itemId)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public Workspace updateItem(String id, Item item) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = new ArrayList<>(items);
        int index = findItem(id);
        if (index == -1) {
            return null;
        }
        newWorkspace.items.set(index, item);
        return newWorkspace;
    }

    public Workspace deleteItem(int index) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = new ArrayList<>(items);
        newWorkspace.items.remove(index);
        return newWorkspace;
    }
}
