package com.andrewfrolkin.jsketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.andrewfrolkin.jsketch.models.CircleModel;
import com.andrewfrolkin.jsketch.models.LineModel;
import com.andrewfrolkin.jsketch.models.Model;
import com.andrewfrolkin.jsketch.models.RectModel;
import com.andrewfrolkin.jsketch.models.ShapeModel;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by andrewfrolkin on 2016-06-30.
 */

public class CanvasView extends CardView {
    private final Paint paint;

    // points for preview drawing
    private float startX;
    private float startY;
    private float endX;
    private float endY;

    boolean drawing;
    boolean dragging;

    // selected tools
    private Model.SideBarTool sideBarTool;
    private Model.SideBarLineWidth sideBarLineWidth;
    private Model.SideBarColor sideBarColor;

    private List<ShapeModel> shapes;
    private ShapeModel selectedShape;
    private int selectedShapeIndex;

    private PanningViewGroup parent;
    private MainActivity activity;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        drawing = false;
    }

    public void setSideBarTool(Model.SideBarTool tool) {
        sideBarTool = tool;
        if (tool != Model.SideBarTool.SELECTION) {
            clearSelectedShape();
            invalidate();
        }
    }

    private void clearSelectedShape() {
        selectedShape = null;
        selectedShapeIndex = -1;
        activity.setSelectedShapeIndex(selectedShapeIndex);
    }

    public void setShapes(List<ShapeModel> shapes) {
        this.shapes = shapes;
    }

    public void setSideBarLineWidth(Model.SideBarLineWidth tool) {
        sideBarLineWidth = tool;
        if (selectedShape != null ) {
            ShapeModel s =  shapes.get(selectedShapeIndex);
            s.setLineType(sideBarLineWidth);
            invalidate();
        }
    }

    public void setSideBarColor(Model.SideBarColor col) {
        sideBarColor = col;
        if (selectedShape != null ) {
            ShapeModel s =  shapes.get(selectedShapeIndex);
            s.setLineColor(sideBarColor);
            invalidate();
        }
    }

    public void clearCanvas() {
        shapes.clear();
        clearSelectedShape();
        dragging = false;
        drawing = false;
    }

    private void drawSelectedShape(Canvas canvas) {
        if (shapes != null) {
            for (ShapeModel s : shapes) {
                setStroke(s.getLineType());
                setColor(s.getLineColor());

                if (s instanceof LineModel) {
                    if (selectedShape != null && shapes.indexOf(s) == selectedShapeIndex) {
                        // draw selected outline
                        setSelectedColor();
                        setSelectedStroke();
                        // use drawPath instead of drawLine because drawLine doesn't work with DashedPathEffect (see https://code.google.com/p/android/issues/detail?id=29944)
                        Path path = new Path();
                        path.moveTo(s.getOriginX(), s.getOriginY());
                        path.lineTo(((LineModel) s).getEndX(), ((LineModel) s).getEndY());
                        canvas.drawPath(path, paint);
                        paint.setPathEffect(null);
                        return;
                    }
                } else if (s instanceof RectModel) {
                    if (selectedShape != null && shapes.indexOf(s) == selectedShapeIndex) {
                        // draw selected outline
                        setSelectedColor();
                        setSelectedStroke();
                        canvas.drawRect(s.getStartX(), s.getStartY(), ((RectModel) s).getEndX(), ((RectModel) s).getEndY(), paint);
                        paint.setPathEffect(null);
                        return;
                    }
                } else if (s instanceof CircleModel) {
                    if (selectedShape != null && shapes.indexOf(s) == selectedShapeIndex) {
                        // draw selected outline
                        setSelectedColor();
                        setSelectedStroke();
                        canvas.drawCircle(s.getStartX(), s.getStartY(), ((CircleModel) s).getRadius(), paint);
                        paint.setPathEffect(null);
                        return;
                    }
                }
            }
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float minStartX = Math.min(startX, endX);
        float maxEndX = Math.max(startX, endX);

        float minStartY = Math.min(startY, endY);
        float maxEndY = Math.max(startY, endY);

        float radius = Math.min(maxEndX - minStartX, maxEndY - minStartY)/2;

        // draw all of the shapes first
        if (shapes != null) {
            for (ShapeModel s : shapes) {
                setStroke(s.getLineType());
                setColor(s.getLineColor());
                paint.setStyle(Paint.Style.STROKE);

                if (s instanceof LineModel) {
                    canvas.drawLine(s.getOriginX(), s.getOriginY(), ((LineModel) s).getEndX(), ((LineModel) s).getEndY(), paint);
                } else if (s instanceof RectModel) {
                    if (((RectModel) s).getFillColor() != Model.SideBarColor.NONE) {
                        setColor(((RectModel) s).getFillColor());
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(s.getStartX(), s.getStartY(), ((RectModel) s).getEndX(), ((RectModel) s).getEndY(), paint);
                    }

                    setColor(s.getLineColor());
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(s.getStartX(), s.getStartY(), ((RectModel) s).getEndX(), ((RectModel) s).getEndY(), paint);
                } else if (s instanceof CircleModel) {
                    if (((CircleModel) s).getFillColor() != Model.SideBarColor.NONE) {
                        setColor(((CircleModel) s).getFillColor());
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(s.getStartX(), s.getStartY(), ((CircleModel) s).getRadius(), paint);
                    }

                    setColor(s.getLineColor());
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(s.getStartX(), s.getStartY(), ((CircleModel) s).getRadius(), paint);
                }
            }
        }

        // draw selected shape outline on top of canvas
        drawSelectedShape(canvas);

        parent = (PanningViewGroup) getParent();

        // draw shape preview
        if (drawing) {
            setStroke(sideBarLineWidth);
            setColor(sideBarColor);
            paint.setStyle(Paint.Style.STROKE);

            if (sideBarTool == Model.SideBarTool.LINE) {
                canvas.drawLine(startX, startY, endX, endY, paint);
            } else if (sideBarTool == Model.SideBarTool.CIRCLE) {
                if (endX <= startX) {
                    minStartX = startX - radius*2;
                } else {
                    minStartX = startX;
                }

                if (endY <= startY) {
                    minStartY = startY - radius*2;
                } else {
                    minStartY = startY;
                }

                if (radius > 10) {
                    canvas.drawCircle(minStartX + radius, minStartY + radius, radius, paint);
                }
            } else if (sideBarTool == Model.SideBarTool.RECT) {
                if (maxEndX - minStartX > 20 && maxEndY - minStartY > 20) {
                    canvas.drawRect(minStartX, minStartY, maxEndX, maxEndY, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        parent = (PanningViewGroup) getParent();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    startX = event.getX() - parent.getPosX();
                    startY = event.getY() - parent.getPosY();
                    endX = startX + 3;
                    endY = startY + 3;
                    checkCollision(startX, startY);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    endX = event.getX() - parent.getPosX();
                    endY = event.getY() - parent.getPosY();
                    checkShapeDrag(endX, endY);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 1) {
                    endX = event.getX() - parent.getPosX();
                    endY = event.getY() - parent.getPosY();
                    drawing = false;
                    addShapes();
                    dragging = false;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_CANCEL:
                endX = event.getX() - parent.getPosX();
                endY = event.getY() - parent.getPosY();
                drawing = false;
                dragging = false;
                invalidate();
                break;
        }
        return true;
    }

    private void checkShapeDrag(float x, float y) {
        if (sideBarTool == Model.SideBarTool.SELECTION && selectedShape != null && dragging) {
            ShapeModel s = shapes.get(selectedShapeIndex);
            float translationX = x - startX;
            float translationY = y - startY;

            if (s instanceof LineModel) {
                s.setOriginX(s.getOriginX() + translationX);
                s.setOriginY(s.getOriginY() + translationY);
                ((LineModel) s).setEndX(((LineModel) s).getEndX() + translationX);
                ((LineModel) s).setEndY(((LineModel) s).getEndY() + translationY);
            } else if (s instanceof RectModel) {
                s.setStartX(s.getStartX() + translationX);
                s.setStartY(s.getStartY() + translationY);
                ((RectModel) s).setEndX(((RectModel) s).getEndX() + translationX);
                ((RectModel) s).setEndY(((RectModel) s).getEndY() + translationY);
            } else if (s instanceof CircleModel) {
                s.setStartX(s.getStartX() + translationX);
                s.setStartY(s.getStartY() + translationY);
            }

            startX = endX;
            startY = endY;
        }
    }

    private void checkCollision(float x, float y) {
        if (sideBarTool == Model.SideBarTool.ERASER || sideBarTool == Model.SideBarTool.FILL || sideBarTool == Model.SideBarTool.SELECTION) {
            ListIterator li = shapes.listIterator(shapes.size());

            // Iterate in reverse.
            while (li.hasPrevious()) {
                ShapeModel s = (ShapeModel) li.previous();
                if (s.doesIntersect(x, y)) {
                    if (sideBarTool == Model.SideBarTool.ERASER) {
                        li.remove();
                        dragging = false;
                    } else if (sideBarTool == Model.SideBarTool.FILL) {
                        if (s instanceof CircleModel) {
                            ((CircleModel) s).setFillColor(sideBarColor);
                        } else if (s instanceof RectModel) {
                            ((RectModel) s).setFillColor(sideBarColor);
                        }
                        dragging = false;
                    } else if (sideBarTool == Model.SideBarTool.SELECTION) {
                        setSelectedShape(s, shapes.indexOf(s));
                        dragging = true;
                    }
                    return;
                }
            }
            dragging = false;
            clearSelectedShape();
        } else if (sideBarTool == Model.SideBarTool.CIRCLE || sideBarTool == Model.SideBarTool.RECT || sideBarTool == Model.SideBarTool.LINE) {
            drawing = true;
        }
    }

    private void addShapes() {
        float minStartX = Math.min(startX, endX);
        float maxEndX = Math.max(startX, endX);

        float minStartY = Math.min(startY, endY);
        float maxEndY = Math.max(startY, endY);

        float radius = Math.min(maxEndX - minStartX, maxEndY - minStartY)/2;

        if (sideBarTool == Model.SideBarTool.LINE) {
            shapes.add(new LineModel(startX, startY, endX, endY, sideBarLineWidth, sideBarColor));
        } else if (sideBarTool == Model.SideBarTool.CIRCLE) {
            if (endX <= startX) {
                minStartX = startX - radius*2;
            } else {
                minStartX = startX;
            }

            if (endY <= startY) {
                minStartY = startY - radius*2;
            } else {
                minStartY = startY;
            }

            shapes.add(new CircleModel(minStartX + radius, minStartY + radius, radius, sideBarLineWidth, sideBarColor, Model.SideBarColor.NONE));
        } else if (sideBarTool == Model.SideBarTool.RECT) {
            shapes.add(new RectModel(minStartX, minStartY, maxEndX, maxEndY, sideBarLineWidth, sideBarColor, Model.SideBarColor.NONE));
        }
    }

    public List<ShapeModel> getShapes() {
        return shapes;
    }

    public void setStroke(Model.SideBarLineWidth stroke) {
        switch (stroke) {
            case THIN:
                paint.setStrokeWidth(3.0f);
                break;
            case MEDIUM:
                paint.setStrokeWidth(8.0f);
                break;
            case THICK:
                paint.setStrokeWidth(12.0f);
                break;
            default:
                paint.setStrokeWidth(3.0f);
                break;
        }
    }


    private void setSelectedStroke() {
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {20.0f,20.0f}, 0));
    }

    public void setColor(Model.SideBarColor color) {
        switch (color) {
            case RED:
                paint.setColor(ContextCompat.getColor(getContext(), R.color.red));
                break;
            case GREEN:
                paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
                break;
            case BLUE:
                paint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
                break;
            default:
                paint.setColor(ContextCompat.getColor(getContext(), R.color.red));
                break;
        }
    }

    public void setSelectedColor() {
        paint.setColor(Color.LTGRAY);
    }

    private void setSelectedShape(ShapeModel selectedShape, int index) {
        this.selectedShape = selectedShape;
        this.selectedShapeIndex = index;
        activity.selectColor(selectedShape.getLineColor());
        activity.selectLineWidth(selectedShape.getLineType());
        activity.setSelectedShapeIndex(index);
    }

    public void setSelectedShape(int index) {
        if (index != -1) {
            this.selectedShape = shapes.get(index);
            this.selectedShapeIndex = index;
            activity.selectColor(selectedShape.getLineColor());
            activity.selectLineWidth(selectedShape.getLineType());
            activity.setSelectedShapeIndex(index);
        }
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap( this.getLayoutParams().width, this.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        this.layout(0, 0, this.getLayoutParams().width, this.getLayoutParams().height);
        this.draw(c);
        this.setDrawingCacheEnabled(false);
        return b;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}