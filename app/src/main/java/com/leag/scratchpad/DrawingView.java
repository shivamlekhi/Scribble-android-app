package com.leag.scratchpad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Sam on 10/2/2014.
 */
public class DrawingView extends View {
    //drawing path
    private CustomPath drawPath;
    private CustomPath[] Paths;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float brushSize, lastBrushSize;

    private boolean erase=false, menuState = true, isDrawing = true;

    private ArrayList<CustomPath> paths = new ArrayList<CustomPath>();
    private ArrayList<CustomPath> undonePaths = new ArrayList<CustomPath>();

    // For Zoom Purposes
    float mScaleFactor = 1;
    float mPivotX;
    float mPivotY;

    private MotionEvent motionEvent;

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;

    private static int NONE = 0;
    private static int DRAG = 1;
    private static int ZOOM = 2;

    private int mode;

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private float startX = 0f;
    private float startY = 0f;

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private float translateX = 0f;
    private float translateY = 0f;

    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    private boolean dragged = true;

    private float displayWidth;
    private float displayHeight;

    private Canvas MainCanvas;

    Bitmap BackGroundImage;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new CustomPath();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPaint.setColor(Color.TRANSPARENT);

        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        displayWidth = display.getWidth();
        displayHeight = display.getHeight();

    }


    public void startNew(){
        paths.clear();
        invalidate();
    }


    public void SetMotionEvent(MotionEvent ev) {
        this.motionEvent = ev;
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        // drawPaint.setStrokeWidth(brushSize);
        drawPath.setStrokeSize(brushSize);
    }

    public void setBackground(Bitmap bmp) {
        this.BackGroundImage = bmp;

        try {
            MainCanvas.drawBitmap(BackGroundImage, 0,0, null);
        } catch(NullPointerException e) {

        }
        invalidate();
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        //set erase true or false
        erase=isErase;
        if(erase) {
            drawPath.isEraser(true);
        }
        else { drawPaint.setXfermode(null); }
    }

    public boolean isErasing() {
        return erase;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(this.scaleFactor, this.scaleFactor, this.detector.getFocusX(), this.detector.getFocusY());

        MainCanvas = canvas;

        if(BackGroundImage != null) {
            canvas.drawBitmap(BackGroundImage, 0, 0, null);
        }
        // canvas.scale(scaleFactor, scaleFactor);

        for (CustomPath p : paths){
            canvas.drawPath(p, p.getColor());
        }

        canvas.drawPath(drawPath, drawPath.getColor());
        drawPath.setStrokeSize(brushSize);

        translateX = (translateX * -1) < 0 ? 0 : translateX;
        translateY = (translateY * -1) < 0 ? 0 : translateY;

        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
        if((translateX * -1) < 0) {
            translateX = 0;
        }

        //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
        //as doing -1 * (scaleFactor - 1) * displayWidth
        else if((translateX * -1) > (scaleFactor - 1) * displayWidth) {
            translateX = (1 - scaleFactor) * displayWidth;
        }

        if(translateY * -1 < 0) {
            translateY = 0;
        }

        //We do the exact same thing for the bottom bound, except in this case we use the height of the display
        else if((translateY * -1) > (scaleFactor - 1) * displayHeight) {
            translateY = (1 - scaleFactor) * displayHeight;
        }

        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
        canvas.restore();

        //canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    public void onClickRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }

    public void CustomTrans(float x, float y) {
        int lastX, lastY;
        MainCanvas.translate(x/ scaleFactor,y/scaleFactor);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x,y;

        if(scaleFactor > 1) {
            x = event.getX() - (event.getX()*(scaleFactor-1) - event.getX());
            y = event.getY() - (event.getY()*(scaleFactor-1) - event.getY());
        } else {
            x = event.getX();
            y = event.getY();
        }

        if(isDrawing) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    if(x > MainCanvas.getWidth()) {
                        // MainCanvas.translate( 1 / scaleFactor, 1 / scaleFactor);
                    }

                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
                default:
                    return false;
            }
            invalidate();
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;

                    //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                    //amount for each coordinates This works even when we are translating the first time because the initial
                    //values for these two variables is zero.
                    startX = event.getX() - previousTranslateX;
                    startY = event.getY() - previousTranslateY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    translateX = event.getX() - startX;
                    translateY = event.getY() - startY;

                    //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                    //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                    double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                                    Math.pow(event.getY() - (startY + previousTranslateY), 2)
                    );

                    if(distance > 0) {
                        dragged = true;
                    }

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = NONE;
                    dragged = false;

                    //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                    //previousTranslate
                    previousTranslateX = translateX;
                    previousTranslateY = translateY;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = DRAG;

                    //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                    //and previousTranslateY when the second finger goes up
                    previousTranslateX = translateX;
                    previousTranslateY = translateY;
                    break;
            }

            detector.onTouchEvent(event);

            //We redraw the canvas only in the following cases:
            //
            // o The mode is ZOOM
            //        OR
            // o The mode is DRAG and the scale factor is not equal to 1 (meaning we have zoomed) and dragged is
            //   set to true (meaning the finger has actually moved)
            if ((mode == DRAG && scaleFactor != 1f && dragged) || mode == ZOOM) {
                invalidate();
            }
        }

        return true;
    }

    public void isDrawing() {
        isDrawing = !isDrawing;
    }

    public void setColor(int color){
        //set color
        invalidate();

        drawPaint.setColor(color);
        Paint pt = new Paint();
        pt.setColor(color);

        drawPath.SetColor(pt);
    }

    public void onClickUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        drawPath.lineTo(x,y);
        mX = x;
        mY = y;
    }

    private void touch_up() {
        // drawPath.lineTo(mX, mY);
        // commit the path to our offscreen
        drawCanvas.drawPath(drawPath, drawPath.getColor());
        // kill this so we don't double draw
        // drawPath.setStrokeSize(drawPaint.getStrokeWidth());

        float OldStrokeSize = drawPath.getStrokeSize();
        paths.add(drawPath);
        drawPath = new CustomPath();
        drawPath.SetColor(drawPaint);
        drawPath.setStrokeSize(OldStrokeSize);
    }

    protected void dispatchDraw(Canvas canvas) {
        drawCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        drawCanvas.scale(mScaleFactor, mScaleFactor, mPivotX, mPivotY);
        super.dispatchDraw(canvas);
        drawCanvas.restore();
    }

    public void scale(float scaleFactor, float pivotX, float pivotY) {
        mScaleFactor = scaleFactor;
        mPivotX = pivotX;
        mPivotY = pivotY;
        invalidate();
    }

    public void restore() {
        mScaleFactor = 1;
        invalidate();
    }

    public void inval() {
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            // invalidate();
            return true;
        }
    }
}

class CustomPath extends Path {
    Paint mPaint;

    public CustomPath() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void SetColor(Paint paint) {
        this.mPaint = new Paint();
        this.mPaint.setColor(paint.getColor());

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public Paint getColor() {
        return this.mPaint;
    }

    public void setStrokeSize(float size) {
        this.mPaint.setStrokeWidth(size);
    }

    public float getStrokeSize() {
        return this.mPaint.getStrokeWidth();
    }

    public void isEraser(boolean erase) {
        if(erase) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            mPaint.setXfermode(null);
        }
    }
}