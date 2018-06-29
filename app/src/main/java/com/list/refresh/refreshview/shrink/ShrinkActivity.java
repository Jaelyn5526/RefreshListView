package com.list.refresh.refreshview.shrink;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.list.refresh.refreshview.R;
import com.list.refresh.refreshview.WifiRecycleAdapter;

public class ShrinkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ShrinkListView refreshListView = findViewById(R.id.refreshlistview);
        WifiRecycleAdapter adapter = new WifiRecycleAdapter();
        RecyclerView recyclerView = refreshListView.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
