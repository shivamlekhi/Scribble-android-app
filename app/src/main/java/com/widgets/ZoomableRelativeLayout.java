package com.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;

import com.leag.scratchpad.DrawingView;

/**
 * Created by Shivam Lekhi on 11/6/2014.
 */
public class ZoomableRelativeLayout extends RelativeLayout {
    float mScaleFactor = 1;
    float mPivotX;
    float mPivotY;
    ScaleGestureDetector scaleGestureDetector = null;
    private DrawingView drawview;

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;


    public ZoomableRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        // scaleGestureDetector = new ScaleGestureDetector(getContext(), new OnPinchListener());
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

    }

    public void setDrawingView(DrawingView DrawView) {
        this.drawview = DrawView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //if(event.getPointerCount() == 1){
            // drawview.customMotionEvent(event);
        //} else {
            // detector.onTouchEvent(event);
        // }

        return true;
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
/*

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScaleFactor, mScaleFactor, mPivotX, mPivotY);
        canvas.restore();
*/
    }

    public void scale(float scaleFactor, float pivotX, float pivotY) {
        mScaleFactor = scaleFactor;
        mPivotX = pivotX;
        mPivotY = pivotY;
        this.invalidate();
    }

    public void restore() {
        mScaleFactor = 1;
        this.invalidate();
    }

    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float scaleFactor = 1.f;
        private float MIN_ZOOM = 1f;
        private float MAX_ZOOM = 5f;

        float startingSpan;
        float endSpan;
        float startFocusX;
        float startFocusY;


        public boolean onScaleBegin(ScaleGestureDetector detector) {
            startingSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();

            return true;
        }


        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            scale(detector.getCurrentSpan() / startingSpan, startFocusX, startFocusY);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            // MainLayout.restore();
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }

}