package com.list.refresh.refreshview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.list.refresh.refreshview.nest.RefreshListView2;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RefreshListView2 refreshListView = findViewById(R.id.refreshlistview);
        WifiRecycleAdapter adapter = new WifiRecycleAdapter();
        RecyclerView recyclerView = refreshListView.getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}
