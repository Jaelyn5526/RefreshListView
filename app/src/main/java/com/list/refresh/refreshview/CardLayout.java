package com.list.refresh.refreshview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class CardLayout extends LinearLayout implements RefreshListView.NestedScrollingCallBack{

    private int[] childTop;
    public CardLayout(Context context) {
        this(context, null);
    }

    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof RecyclerView){
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                         getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                        lp.width);
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
                break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = getChildCount();
        childTop = new int[count];
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            childTop[i] = -child.getTop() + child.getPaddingTop();
        }
    }

    @Override
    public void nestedScrolling(int type, float percent) {
        int count = getChildCount();
        if (count < 2){
            return;
        }
        if (type == 0){
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child != null && child.getVisibility() != GONE){
                    ViewCompat.offsetTopAndBottom(child, (int) (((1 - percent) * childTop[i]) - child.getTop()));
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                ViewCompat.offsetTopAndBottom(child, childTop[i] - child.getTop());
            }
        }



    }

    @Override
    public void onRefresh() {

    }

    public boolean canScrollVertically(){
        View view = getChildAt(getChildCount()-1);
        if (view.getTranslationY() != -view.getTop()){
            return true;
        }
        return false;
    }

    public void offsetChildTopAndBottom(int offset){
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE){
                ViewCompat.offsetTopAndBottom(child, offset);
            }
        }
    }

    public int getCurrTop(){
        return getChildCount() > 0 ? getChildAt(0).getTop() + getTop(): 0;
    }
}
