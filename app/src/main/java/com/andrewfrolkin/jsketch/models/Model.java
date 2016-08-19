package com.andrewfrolkin.jsketch.models;

/**
 * Created by andrewfrolkin on 2016-06-12.
 */
public class Model {

    public enum SideBarLineWidth {
        THIN, MEDIUM, THICK, NONE
    }

    public enum SideBarTool {
        SELECTION, ERASER, LINE, CIRCLE, RECT, FILL, NONE
    }


    public enum SideBarColor {
        RED, GREEN, BLUE, NONE, OTHER;
    }

}
