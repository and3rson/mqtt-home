package com.dunai.home.activities.reorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dunai.home.R;
import com.dunai.home.activities.interfaces.ItemTouchHelperAdapter;
import com.dunai.home.client.workspace.Item;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {
    private final Context context;
    private final ArrayList<Item> items;
    private final OnDragStartListener dragStartListener;

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }
    public RecyclerViewAdapter(Context context, ArrayList<Item> items, OnDragStartListener dragStartListener) {
        this.context = context;
        this.items = new ArrayList<>(items);
        this.dragStartListener = dragStartListener;
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reorderable_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemTitle.setText(items.get(position).getTitle());
        holder.itemSubTitle.setText(items.get(position).getSubTitle());
        holder.itemIcon.setImageDrawable(context.getResources().getDrawable(items.get(position).getIconResource(), context.getTheme()));
        holder.itemHandle.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onDragStarted(holder);
            }
            return false;
        });
    }

    @Override
    public void onItemDismiss(int position) {
//            items.remove(position);
//            notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Item prev = items.remove(fromPosition);
        items.add(toPosition > fromPosition ? toPosition : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
