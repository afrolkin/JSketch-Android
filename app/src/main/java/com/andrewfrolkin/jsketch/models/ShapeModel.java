package com.andrewfrolkin.jsketch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewfrolkin on 2016-06-07.
 */

public abstract class ShapeModel implements Parcelable {
    protected float originX;
    protected float originY;
    protected float startX;
    protected float startY;
    protected int z;
    protected Model.SideBarLineWidth lineType;
    protected Model.SideBarColor lineColor;

    protected ShapeModel() {
    }

    public enum LineType {
        MEDIUM, THIN, THICK
    }

    protected ShapeModel(Parcel in) {
        originX = in.readFloat();
        originY = in.readFloat();
        startX = in.readFloat();
        startY = in.readFloat();
        z = in.readInt();
        lineType = Model.SideBarLineWidth.valueOf(in.readString());
        lineColor = Model.SideBarColor.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(originX);
        dest.writeFloat(originY);
        dest.writeFloat(startX);
        dest.writeFloat(startY);
        dest.writeInt(z);
        dest.writeString(lineType.name());
        dest.writeString(lineColor.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartX(float x) {
        this.startX = x;
    }

    public void setStartY(float y) {
        this.startY = y;
    }

    public int getZ() {
        return z;
    }

    public Model.SideBarColor getLineColor() {
        return lineColor;
    }

    public void setOriginX(float x) {
        originX = x;
    }

    public void setOriginY(float y) {
        originY = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setLineType(Model.SideBarLineWidth l) {
        lineType = l;
    }

    public Model.SideBarLineWidth getLineType() {
        return lineType;
    }

    public void setLineColor(Model.SideBarColor c) {
        lineColor = c;
    }

    public abstract boolean doesIntersect(float x, float y);

}
