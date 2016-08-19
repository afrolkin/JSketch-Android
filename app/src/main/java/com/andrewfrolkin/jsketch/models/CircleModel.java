package com.andrewfrolkin.jsketch.models;

import android.os.Parcel;

/**
 * Created by andrewfrolkin on 2016-06-07.
 */
public class CircleModel extends ShapeModel {
    private float radius;
    private Model.SideBarColor fillColor;

    public CircleModel(float originX, float originY, float radius, Model.SideBarLineWidth lineType, Model.SideBarColor lineColor, Model.SideBarColor fillColor) {
        this.originX = originX;
        this.originY = originY;
        this.radius = radius;
        this.lineType = lineType;
        this.lineColor = lineColor;
        this.fillColor = fillColor;

        this.startX = originX;
        this.startY = originY;
    }

    private CircleModel(Parcel in) {
        super(in);
        radius = in.readFloat();
        fillColor = Model.SideBarColor.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(radius);
        dest.writeString(fillColor.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleModel> CREATOR = new Creator<CircleModel>() {
        @Override
        public CircleModel createFromParcel(Parcel in) {
            return new CircleModel(in);
        }

        @Override
        public CircleModel[] newArray(int size) {
            return new CircleModel[size];
        }
    };

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setFillColor(Model.SideBarColor c) {
        fillColor = c;
    }

    public float getRadius() {
        return radius;
    }

    public Model.SideBarColor getFillColor() {
        return fillColor;
    }

    public boolean doesIntersect(float x, float y) {
        return ((Math.sqrt((x - startX)*(x - startX) + (y-startY) * (y - startY)) < radius));
    }

}