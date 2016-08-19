package com.andrewfrolkin.jsketch;

import android.util.AttributeSet;
import android.view.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by andrewfrolkin on 2016-07-06.
 */

public class PanningViewGroup extends ViewGroup {

    // Pointer ids
    private static final int INVALID_POINTER_ID = -1;
    private int activePointerId = -1;

    private float[] dispatchTouchEventWorkingArray = new float[2];
    private float[] onTouchEventWorkingArray = new float[2];

    // translation matrix
    private Matrix translateMatrix = new Matrix();
    private Matrix translateMatrixInverse = new Matrix();

    private float posX;
    private float posY;

    private float startX;
    private float startY;

    private boolean dragging = false;

    public PanningViewGroup(Context context, AttributeSet attrs){
        super(context, attrs);
        translateMatrix.setTranslate(0, 0);
    }

    public PanningViewGroup(Context context) {
        super(context);
    }

    // found in android docs https://developer.android.com/training/gestures/viewgroup.html
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        onTouchEventWorkingArray[0] = event.getX();
        onTouchEventWorkingArray[1] = event.getY();
        onTouchEventWorkingArray = scaledPointsToScreenPoints(onTouchEventWorkingArray);

        event.setLocation(onTouchEventWorkingArray[0], onTouchEventWorkingArray[1]);

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE: {
                int pointerCount = event.getPointerCount();

                if (pointerCount == 2) {
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();

                startX = x;
                startY = y;

                activePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_UP: {
                activePointerId = INVALID_POINTER_ID;
                dragging = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                dragging = false;
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onTouchEventWorkingArray[0] = event.getX();
        onTouchEventWorkingArray[1] = event.getY();
        onTouchEventWorkingArray = scaledPointsToScreenPoints(onTouchEventWorkingArray);

        event.setLocation(onTouchEventWorkingArray[0], onTouchEventWorkingArray[1]);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                int pointerCount = event.getPointerCount();

                if (pointerCount == 2) {
                    dragging = true;
                    final int pointerIndex = event.findPointerIndex(activePointerId);
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    final float newX = x - startX;
                    final float newY = y - startY;

                    posX += newX;
                    posY += newY;
                    translateMatrix.preTranslate(newX, newY);
                    translateMatrix.invert(translateMatrixInverse);

                    startX = x;
                    startY = y;

                    invalidate();
                    break;
                }
            }
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View c = getChildAt(i);

            if (c.getVisibility() != GONE) {
                c.layout(l, t, l + c.getMeasuredWidth(), t + c.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View c = getChildAt(i);

            if (c.getVisibility() != GONE) {
                measureChild(c, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        translateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a){
        translateMatrixInverse.mapPoints(a);
        return a;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }


    public boolean isDragging() {
        return dragging;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(posX, posY);

        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dispatchTouchEventWorkingArray[0] = ev.getX();
        dispatchTouchEventWorkingArray[1] = ev.getY();
        dispatchTouchEventWorkingArray = screenPointsToScaledPoints(dispatchTouchEventWorkingArray);
        ev.setLocation(dispatchTouchEventWorkingArray[0], dispatchTouchEventWorkingArray[1]);

        return super.dispatchTouchEvent(ev);
    }
}