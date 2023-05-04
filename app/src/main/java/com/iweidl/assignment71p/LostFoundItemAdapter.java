package com.iweidl.assignment71p;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.iweidl.assignment71p.sqlitehelper.LostFoundItem;
import java.util.List;

public class LostFoundItemAdapter extends RecyclerView.Adapter<LostFoundViewHolder> {
    private List<LostFoundItem> items;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Constructor for the adapter
    public LostFoundItemAdapter(Context context, List<LostFoundItem> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    // Inflate the layout for each item and create a view holder
    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(view);
    }

    // Bind data to the view holder
    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(item));
    }

    // Get the total number of items in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Interface for handing item clicks
    public interface OnItemClickListener {
        void onItemClick(LostFoundItem item);
    }
}
