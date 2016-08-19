package com.andrewfrolkin.jsketch.models;

import android.os.Parcel;

/**
 * Created by andrewfrolkin on 2016-06-07.
 */
public class LineModel extends ShapeModel {
    private float length;
    private float endX;
    private float endY;

    public LineModel(float originX, float originY, float endX, float endY, Model.SideBarLineWidth lineType, Model.SideBarColor lineColor) {
        this.originX = originX;
        this.originY = originY;
        this.length = length;
        this.lineType = lineType;
        this.lineColor = lineColor;

        this.startX = originX;
        this.startY = originY;
        this.endX = endX;
        this.endY = endY;
    }

    private LineModel(Parcel in) {
        super(in);
        length = in.readFloat();
        endY = in.readFloat();
        endX = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(length);
        dest.writeFloat(endY);
        dest.writeFloat(endX);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LineModel> CREATOR = new Creator<LineModel>() {
        @Override
        public LineModel createFromParcel(Parcel in) {
            return new LineModel(in);
        }

        @Override
        public LineModel[] newArray(int size) {
            return new LineModel[size];
        }
    };

    public void setLength(float length) {
        this.length = length;
    }

    public float getLength() {
        return length;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndX(float x) {
        endX = x;
    }

    public void setEndY(float y) {
        endY = y;
    }

    public boolean doesIntersect(float x, float y) {
        float shortestDistance = distanceFromPointToLine(x, y, originX, originY, endX, endY);
        return (shortestDistance <= 8);
    }

    private float distanceFromPointToLine(float x, float y, float originX, float originY, float endX, float endY) {
        float num = (Math.abs(((endY - originY) * x) - ((endX - originX) * y) + endX*originY - endY*originX));
        double demom = (Math.sqrt(Math.pow(endY - originY, 2) + Math.pow(endX - originX, 2)));
        return ((float) (num / demom));
    }

}