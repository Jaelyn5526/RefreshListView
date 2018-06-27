package com.list.refresh.refreshview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class CircleLoadView extends View{

    private Paint paint;

    private int cx, cy;
    private int dx;
    private float[] mRadius = new float[3];
    private ValueAnimator animator;
    private float maxRadius;
    private float currRaduis = 0;

    public CircleLoadView(Context context) {
        this(context, null);
    }

    public CircleLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initView(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAlpha((int) (255 * 0.2f));

        cx = getWidth() / 2;
        cy = getHeight() / 2;
        dx = Math.min(cx, cy) / 4;
        for (int i = 0; i < mRadius.length; i++) {
            mRadius[i] = 0;
        }
        maxRadius = Math.min(cx, cy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (paint == null){
            initView();
        }
        for (int i = 0; i < mRadius.length; i++) {
            canvas.drawCircle(cx, cy, mRadius[i], paint);
        }
    }

    public void starAnim(){
        if (animator != null && animator.isRunning()){
            return;
        }

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float radius;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue() * currRaduis;
                mRadius[0] = radius;
                mRadius[1] = Math.max(0, radius - dx);
                mRadius[2] = Math.max(0, radius - 2 * dx);
                postInvalidate();
            }
        });
        animator.start();
    }

    public void updateView(float percent){
        if (percent < 0 && animator != null && animator.isRunning()) {
            currRaduis = 0;
            animator.end();
        } else if (percent > 0) {
            currRaduis = maxRadius * percent;
            cy = (int) currRaduis;
            starAnim();
        }

    }
}
























