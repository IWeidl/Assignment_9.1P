package com.iweidl.assignment71p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.iweidl.assignment71p.sqlitehelper.LostFoundDBHelper;
import com.iweidl.assignment71p.sqlitehelper.LostFoundItem;

import java.util.List;

public class ShowAllLostAndFoundActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundDBHelper dbHelper;
    private List<LostFoundItem> items;
    private LostFoundItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_lost_found);

        recyclerView = findViewById(R.id.recyclerViewItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new LostFoundDBHelper(this);

        items = dbHelper.getAllItems();

        adapter = new LostFoundItemAdapter(this, items, item -> {
            Intent intent = new Intent(ShowAllLostAndFoundActivity.this, ItemDetailsActivity.class);
            intent.putExtra("ITEM_ID", item.getItemId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    protected void onResume() {
        super.onResume();
        items.clear();
        items.addAll(dbHelper.getAllItems());
        adapter.notifyDataSetChanged();
    }
}