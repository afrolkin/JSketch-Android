package com.andrewfrolkin.jsketch.models;

import android.os.Parcel;

/**
 * Created by andrewfrolkin on 2016-06-07.
 */
public class RectModel extends ShapeModel {
    private float endY;
    private float endX;
    private Model.SideBarColor fillColor;

    private RectModel(Parcel in) {
        super(in);
        endY = in.readFloat();
        endX = in.readFloat();
        fillColor = Model.SideBarColor.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(endY);
        dest.writeFloat(endX);
        dest.writeString(fillColor.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RectModel> CREATOR = new Creator<RectModel>() {
        @Override
        public RectModel createFromParcel(Parcel in) {
            return new RectModel(in);
        }

        @Override
        public RectModel[] newArray(int size) {
            return new RectModel[size];
        }
    };

    public RectModel(float originX, float originY, float endX, float endY, Model.SideBarLineWidth lineType, Model.SideBarColor lineColor, Model.SideBarColor fillColor) {
        this.originX = originX;
        this.originY = originY;
        this.endX = endX;
        this.endY = endY;
        this.lineType = lineType;
        this.lineColor = lineColor;
        this.fillColor = fillColor;

        this.startX = originX;
        this.startY = originY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public void setFillColor(Model.SideBarColor c) {
        fillColor = c;
    }

    public float getEndY() {
        return endY;
    }

    public float getEndX() {
        return endX;
    }

    public Model.SideBarColor getFillColor() {
        return fillColor;
    }

    public boolean doesIntersect(float x, float y) {
        float height = endY - startY;
        float width = endX - startX;
        return (startX <= x && x <= startX + width && startY <= y && y <= startY + height);
    }
}