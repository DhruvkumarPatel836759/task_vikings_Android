package com.example.task_vikings_android.models;

/**
 * Created by asifkhan on 12/28/17.
 */

public class CategoriesModel {
    private int tagID;
    private String tagTitle;

    public CategoriesModel(){}

    public CategoriesModel(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public CategoriesModel(int tagID, String tagTitle) {
        this.tagID = tagID;
        this.tagTitle = tagTitle;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }
}
