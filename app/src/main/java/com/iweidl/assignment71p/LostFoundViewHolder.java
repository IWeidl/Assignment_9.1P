package com.iweidl.assignment71p;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LostFoundViewHolder extends RecyclerView.ViewHolder {
    public TextView itemName;
    public TextView itemDescription;

    public LostFoundViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.itemName);
        itemDescription = itemView.findViewById(R.id.itemDescription);
    }
}
