package com.example.task_vikings_android.models;


public class PendingTaskModel {
    private int todoID;
    private String todoTitle,todoContent,todoDate, todoTime,todoTag,fileName;

    public PendingTaskModel(){}

    public PendingTaskModel(String todoTitle, String todoContent, String todoTag, String todoDate, String todoTime,String fileName) {
        this.todoTitle = todoTitle;
        this.todoContent = todoContent;
        this.todoDate = todoDate;
        this.todoTime = todoTime;
        this.todoTag = todoTag;
        this.fileName = fileName;
    }

    public PendingTaskModel(int todoID, String todoTitle, String todoContent, String todoTag, String todoDate, String todoTime,String fileName) {
        this.todoID = todoID;
        this.todoTitle = todoTitle;
        this.todoContent = todoContent;
        this.todoDate = todoDate;
        this.todoTime = todoTime;
        this.todoTag = todoTag;
        this.fileName = fileName;
    }

    public int getTodoID() {
        return todoID;
    }

    public void setTodoID(int todoID) {
        this.todoID = todoID;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getTodoContent() {
        return todoContent;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    public String getTodoDate() {
        return todoDate;
    }

    public void setTodoDate(String todoDate) {
        this.todoDate = todoDate;
    }

    public String getTodoTime() {
        return todoTime;
    }

    public void setTodoTime(String todoTime) {
        this.todoTime = todoTime;
    }

    public String getTodoTag() {
        return todoTag;
    }

    public void setTodoTag(String todoTag) {
        this.todoTag = todoTag;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
