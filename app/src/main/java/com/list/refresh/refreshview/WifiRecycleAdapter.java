package com.list.refresh.refreshview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WifiRecycleAdapter extends RecyclerView.Adapter<WifiRecycleAdapter.MyHolder>{

    List<String> items = new ArrayList<>();

    public WifiRecycleAdapter(){
        items.clear();
        for (int i = 0; i < 20; i++) {
            items.add(String.valueOf(i));
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.ssidTv.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public TextView ssidTv;

        public MyHolder(View itemView) {
            super(itemView);
            ssidTv = itemView.findViewById(R.id.title);
        }
    }
}
