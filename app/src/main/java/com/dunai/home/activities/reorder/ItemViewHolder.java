package com.dunai.home.activities.reorder;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dunai.home.R;
import com.dunai.home.activities.interfaces.ItemTouchHelperViewHolder;

public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    public final ImageView itemIcon;
    public final TextView itemTitle;
    public final TextView itemSubTitle;
    public final ImageView itemHandle;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemIcon = itemView.findViewById(R.id.itemIcon);
        itemTitle = itemView.findViewById(R.id.itemTitle);
        itemSubTitle = itemView.findViewById(R.id.itemSubTitle);
        itemHandle = itemView.findViewById(R.id.itemHandle);
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }
}
