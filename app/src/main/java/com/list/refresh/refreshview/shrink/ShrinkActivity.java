package com.list.refresh.refreshview.shrink;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.list.refresh.refreshview.R;
import com.list.refresh.refreshview.WifiRecycleAdapter;

import org.w3c.dom.Text;

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

        LinearLayout linearLayout = refreshListView.getCardLayout();
        Button textView = new Button(this);
        textView.setText("aaaaaaaaaaaaaaaaaaaaaa");
        linearLayout.addView(textView, 0);

        TextView textView1 = new TextView(this);
        textView1.setText("bbbbbbbbbbbb");
        linearLayout.addView(textView1, 0);
    }
}
