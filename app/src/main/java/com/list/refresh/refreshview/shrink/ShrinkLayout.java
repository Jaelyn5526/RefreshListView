package com.list.refresh.refreshview.shrink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;


import com.list.refresh.refreshview.R;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

public class ShrinkLayout extends LinearLayout implements NestedScrollingParent {
    private String LOG = "ShrinkLayout ";

    private int mTouchSlop;

    private int[] childTop = new int[3];
    private int DEFAULT_CLOSE_TOP = 10;
    private int DEFAULT_REFRESH_TOP = 90;
    private int DEFAULT_REFRESH_TOP_MIN = 71;
    private int DEFAULT_OPEN_TOP = 162;

    private boolean isBig = true;

    private int mDragCloseDistance;
    private int mDragRefreshDistance;
    private int mDragTotalDistance;
    private int mDragCloseLine;
    private int mDragOpenLine;

    private float mDisCloseRefresh;
    private float mDisRefresheOpen;

    private NestedScrollingParentHelper mParentHelper;
    private Scroller scroller;

    private RecyclerView recyclerView;

    private OnTouchListener onRecyclerListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL){
                finishSpinner(getChildAt(0).getY(), 0);
            }
            return false;
        }
    };
    public ShrinkLayout(Context context) {
        this(context, null);
    }

    public ShrinkLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShrinkLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
        inflate(context, R.layout.shrink_list_layout, this);
        recyclerView = findViewById(R.id.refresh_recycler_view);
        recyclerView.setOnTouchListener(onRecyclerListener);
        mParentHelper = new NestedScrollingParentHelper(this);
        scroller = new Scroller(getContext());
        for (int i = 0; i < 3; i++) {
            childTop[i] = 0;
        }
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    public void initDistance(boolean isBig) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (isBig) {
            mDragRefreshDistance = (int) (DEFAULT_REFRESH_TOP * metrics.density) + childTop[0];
        } else {
            mDragRefreshDistance = (int) (DEFAULT_REFRESH_TOP_MIN * metrics.density) + childTop[0];
        }
        mDragCloseDistance = (int) (DEFAULT_CLOSE_TOP * metrics.density) ;
        mDragTotalDistance = (int) (DEFAULT_OPEN_TOP * metrics.density) + childTop[0];
        mDragCloseLine = (mDragRefreshDistance - childTop[0] - mDragCloseDistance) / 3 + mDragCloseDistance;
        mDragOpenLine = (mDragTotalDistance - mDragRefreshDistance) / 3 + mDragRefreshDistance;
        mDisCloseRefresh = mDragRefreshDistance - mDragCloseDistance;
        mDisRefresheOpen = mDragTotalDistance - mDragRefreshDistance;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof RecyclerView) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                        lp.width);
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
                break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = getChildCount();
        if (recyclerView.isShown()){
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                childTop[i] = (int) (recyclerView.getY() - child.getY());
            }
        }
        initDistance(true);
        offsetAllChildTopAndBottom(mDragRefreshDistance - childTop[0]);
    }

    boolean isAttachedToWindow = false;
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
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
        if (dy < 0) {
            if (!target.canScrollVertically(-1)){
                //下拉
                offsetAllChildTopAndBottom( -dy);
                consumed[1] = dy;
            }
        } else if (dy > 0) {
            if (target.getY() > mDragCloseDistance){
                if (target.getY() - dy < mDragCloseDistance) {
                    dy = (int) target.getY();
                }
                offsetAllChildTopAndBottom( -dy);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (target.getY() <= mDragCloseDistance){
            return false;
        }
        return true;
    }

    /**
     * @param dirction -1  是否可以展开   1 是否可以折叠
     */
    private void canExpandVertically(int dirction){

    }

    private View getMoveOffsetView(){
        if (recyclerView != null && recyclerView.isShown()) {
            return recyclerView;
        } else {
            return getChildAt(0);
        }
    }

    private void offsetAllChildTopAndBottom(int dy){
        View target = getMoveOffsetView();
        if (target.getY() + dy < mDragCloseDistance && dy < 0) {
            dy = (int) (mDragCloseDistance - target.getY());
        }
        int count = getChildCount();
        View child;
        int currTargerTop = (int) target.getY() + dy;
        if (count > 1 && currTargerTop <= mDragRefreshDistance && recyclerView.isShown()) {
            float percent = (currTargerTop - mDragCloseDistance) / (float)(mDragRefreshDistance  - mDragCloseDistance);
            int offsetDy;
            target.offsetTopAndBottom(dy);
            for (int i = 0; i < count - 1; i++) {
                child = getChildAt(i);
                if (child != null && child.getVisibility() != GONE) {
                    offsetDy = (int) (currTargerTop - percent * childTop[i] - child.getY());
                    child.offsetTopAndBottom(offsetDy);
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                child = getChildAt(i);
                if (child != null && child.getVisibility() != GONE) {
                    child.offsetTopAndBottom(dy);
                }
            }
        }
        doCallShrinkScroll(currTargerTop);
    }

    private int mActivePointerId;
    private boolean isTouchRecyclerView = false;
    private boolean mIsBeingDragged = false;
    float mLastTouchY = 0;
    private int mInitialDownY;
    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (action == MotionEvent.ACTION_DOWN ) {
            if (isTouchDownRecyclerView(ev.getY())){
                isTouchRecyclerView = true;
                return false;
            } else {
                isTouchRecyclerView = false;
            }
        }
        if (isTouchRecyclerView ) {
            return false;
        }

        Log.d(LOG, "onInterceptTouchEvent");
        if (!isEnabled()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                if (isTouchDownRecyclerView(ev.getY())){
                    return false;
                }
                mInitialDownY = (int) (ev.getY(pointerIndex) + 0.5f);
                mLastTouchY = (int) (ev.getY() + 0.5f);

                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                int y = (int) (ev.getY(pointerIndex) + 0.5f);
                startDragging(y);
               /* if (mTarget.getTop() != mDragCloseDistance && mIsBeingDragged){
                    return true;
                }*/
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        Log.d(LOG, "sBeingDragged: " + mIsBeingDragged);
        return mIsBeingDragged;
    }

    public boolean isTouchDownRecyclerView(float y){
        if (recyclerView.getY() <= y && y <= recyclerView.getY()+recyclerView.getHeight()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;


        if (!isEnabled()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                mInitialDownY = (int) (ev.getY(pointerIndex) + 0.5f);
                mLastTouchY = (int) (ev.getY(pointerIndex) + 0.5f);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                int y = (int) (ev.getY(pointerIndex) + 0.5f);
                int dy = (int) (y - mLastTouchY);
                startDragging(y);
                mLastTouchY = y;
                if (mIsBeingDragged){
                    offsetAllChildTopAndBottom(dy);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    if (recyclerView.isShown() && recyclerView.getY() <= mDragCloseDistance){
                        recyclerView.onTouchEvent(ev);
                    } else {
                        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                        final float yvel = -mVelocityTracker.getYVelocity(mActivePointerId);
                        finishSpinner(getChildAt(0).getY(), yvel);
                    }
                }
                mActivePointerId = INVALID_POINTER;
            }

            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
            mIsBeingDragged = true;
        }
    }

     private void finishSpinner(float targetTop, float YVelocity) {
        int starY = (int) getMoveOffsetView().getY();
        if (targetTop + YVelocity <= mDragCloseLine){
            //关闭
            scroller.startScroll(0, starY, 0, mDragCloseDistance - starY);
        } else if (targetTop + YVelocity <= mDragOpenLine){
            //展开
            scroller.startScroll(0, starY, 0, mDragRefreshDistance - starY);
        } else {
            //刷新
            scroller.startScroll(0, starY, 0, mDragTotalDistance - starY);
        }
        invalidate();
    }

    private boolean canScrollVertically() {
        return false;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currOffsetTop = scroller.getCurrY();
            offsetAllChildTopAndBottom((int) (currOffsetTop - getMoveOffsetView().getY()));
            postInvalidate();
        }
    }

    float percent;
    private void doCallShrinkScroll(int mCurrentTargetOffsetTop){
        if (onShrinkScrollListener == null){
            return;
        }
        if (mCurrentTargetOffsetTop <= mDragRefreshDistance){
            //展开状态
            percent = (mCurrentTargetOffsetTop - mDragCloseDistance) / mDisCloseRefresh;
            onShrinkScrollListener.onScrolled(0, Math.max(0, percent));
        } else {
            //刷新状态
            percent = (mCurrentTargetOffsetTop - mDragRefreshDistance) / mDisRefresheOpen;
            onShrinkScrollListener.onScrolled(1,  Math.max(0, percent));
        }

    }

    OnShrinkScrollListener onShrinkScrollListener;

    public void setOnShrinkScrollListener(OnShrinkScrollListener onShrinkScrollListener) {
        this.onShrinkScrollListener = onShrinkScrollListener;
    }

    public interface OnShrinkScrollListener{
        void onScrolled(int type, float percent);
    }

    public void upDateView(boolean isBig){
        if (isBig == this.isBig){
            return;
        }
        this.isBig = isBig;
        initDistance(isBig);
        if (!isAttachedToWindow) {
            return;
        }
        if (getChildAt(0).getTop() != mDragCloseDistance){
            int targetTop = getChildAt(0).getTop();
            int starY = (int) getMoveOffsetView().getY();
            int offset = 0;
            if (targetTop <= mDragCloseLine){
                //关闭
                offset = mDragCloseDistance - starY;
            } else if (targetTop <= mDragOpenLine){
                //展开
                offset = mDragRefreshDistance - starY;
            } else {
                //刷新
                offset = mDragTotalDistance - starY;
            }
            offsetAllChildTopAndBottom(offset);
            invalidate();
        }
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }
}
