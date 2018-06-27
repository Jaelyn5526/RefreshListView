package com.list.refresh.refreshview.nest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.list.refresh.refreshview.R;
import com.list.refresh.refreshview.WifiRecycleAdapter;


public class MyNestScrollListView extends FrameLayout implements NestedScrollingParent {
    String TAG = "MyNestScrollListView";
    NestedScrollingParentHelper mParentHelper;
    FrameLayout mTaget;

    public MyNestScrollListView(Context context) {
        this(context, null);
    }

    public MyNestScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNestScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.nest_layout, this);
        initView();
    }

    private void initView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        WifiRecycleAdapter adapter = new WifiRecycleAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mParentHelper = new NestedScrollingParentHelper(this);
        mTaget = findViewById(R.id.parent_layout);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int nestedScrollAxes){
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy < 0 && !((RecyclerView)target).canScrollVertically(-1)){
            //下拉
                ViewCompat.offsetTopAndBottom(mTaget,-dy);
                consumed[1] = dy;
        } else if (dy > 0 && mTaget.getTop() != 0){
            //上拉
            if ((mTaget.getTop() + dy) <= 0){
                dy = -mTaget.getTop();
                ViewCompat.offsetTopAndBottom(mTaget,dy);
                consumed[1] = dy;
            } else {
                ViewCompat.offsetTopAndBottom(mTaget,-dy);
                consumed[1] = dy;
            }

        }
        if (dy < 0 && !((RecyclerView)target).canScrollVertically(-1)){
            Log.d(TAG, "onNestedPreScroll " + dy +" scroll---");
        } else {
            Log.d(TAG, "onNestedPreScroll " + dy);
        }
//        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "onNestedPreFling " + velocityX +" " + velocityY);

        return super.onNestedPreFling(target, velocityX, velocityY);
    }
}
