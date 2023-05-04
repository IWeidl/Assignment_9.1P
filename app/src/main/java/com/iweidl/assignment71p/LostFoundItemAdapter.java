package com.iweidl.assignment71p;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iweidl.assignment71p.sqlitehelper.LostFoundItem;

import java.util.List;

public class LostFoundItemAdapter extends RecyclerView.Adapter<LostFoundViewHolder> {
    private List<LostFoundItem> items;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public LostFoundItemAdapter(Context context, List<LostFoundItem> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(LostFoundItem item);
    }
}
