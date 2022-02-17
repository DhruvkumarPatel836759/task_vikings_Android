package com.example.task_vikings_android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {

    private int id;
    private int taskId;
    private byte[] image;
    private String timestamp;

    public ImageModel(int id, int taskId, byte[] image, String timestamp) {
        this.id = id;
        this.taskId = taskId;
        this.image = image;
        this.timestamp = timestamp;
    }

    public ImageModel() {
    }

    protected ImageModel(Parcel in) {
        id = in.readInt();
        taskId = in.readInt();
        image = in.createByteArray();
        timestamp = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static final String TABLE_NAME = "table_images";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_IMAGE_NAME = "image";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TASK_ID + " INTEGER,"
                    + COLUMN_IMAGE_NAME + " BLOB,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(taskId);
        dest.writeByteArray(image);
        dest.writeString(timestamp);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
