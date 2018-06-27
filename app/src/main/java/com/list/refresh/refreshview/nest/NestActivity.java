package com.list.refresh.refreshview.nest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.list.refresh.refreshview.R;
import com.list.refresh.refreshview.RefreshListView;
import com.list.refresh.refreshview.WifiRecycleAdapter;

public class NestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nest);
        RefreshListView2 refreshListView = findViewById(R.id.refreshlistview);
        WifiRecycleAdapter adapter = new WifiRecycleAdapter();
        RecyclerView recyclerView = refreshListView.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}
