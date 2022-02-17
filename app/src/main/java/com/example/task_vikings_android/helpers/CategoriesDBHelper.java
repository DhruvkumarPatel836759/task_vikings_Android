package com.example.task_vikings_android.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.task_vikings_android.models.CategoriesModel;

import java.util.ArrayList;


public class CategoriesDBHelper {
    private Context context;
    private DatabaseHelper databaseHelper;

    public CategoriesDBHelper(Context context){
        this.context=context;
        databaseHelper=new DatabaseHelper(context);
    }

    //add new tags into the database
    public boolean addNewTag(CategoriesModel categoriesModel){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.COL_TAG_TITLE, categoriesModel.getTagTitle());
        sqLiteDatabase.insert(DatabaseHelper.TABLE_TAG_NAME,null,contentValues);
        sqLiteDatabase.close();
        return true;
    }

    //check whether the tag exists or not
    public boolean tagExists(String tagTitle){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        String query="SELECT " + DatabaseHelper.COL_TAG_TITLE + " FROM " +
                DatabaseHelper.TABLE_TAG_NAME + " WHERE " + DatabaseHelper.COL_TAG_TITLE+"=?";
        Cursor cursor=sqLiteDatabase.rawQuery(query,new String[]{tagTitle});
        return (cursor.getCount()>0)?true:false;
    }

    //count tags from the database
    public int countTags(){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        String query="SELECT " + DatabaseHelper.COL_TAG_ID + " FROM " + DatabaseHelper.TABLE_TAG_NAME;
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        return cursor.getCount();
    }

    //fetch all the tags from the database
    @SuppressLint("Range")
    public ArrayList<CategoriesModel> fetchTags(){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        ArrayList<CategoriesModel> categoriesModels =new ArrayList<>();
        String query="SELECT * FROM " + DatabaseHelper.TABLE_TAG_NAME + " ORDER BY " + DatabaseHelper.COL_TAG_ID + " DESC";
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        while (cursor.moveToNext()){
            CategoriesModel categoriesModel =new CategoriesModel();
            categoriesModel.setTagID(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_TAG_ID)));
            categoriesModel.setTagTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TAG_TITLE)));
            categoriesModels.add(categoriesModel);
        }
        cursor.close();
        sqLiteDatabase.close();
        return categoriesModels;
    }

    //delete tag from the database according to the id
    public boolean removeTag(int tagID){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        sqLiteDatabase.execSQL(DatabaseHelper.FORCE_FOREIGN_KEY);
        sqLiteDatabase.delete(DatabaseHelper.TABLE_TAG_NAME,DatabaseHelper.COL_TAG_ID+"=?",
                new String[]{String.valueOf(tagID)});
        sqLiteDatabase.close();
        return true;
    }

    //update tag from the database according to the tag id
    public boolean saveTag(CategoriesModel categoriesModel){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.COL_TAG_TITLE, categoriesModel.getTagTitle());
        sqLiteDatabase.update(DatabaseHelper.TABLE_TAG_NAME,contentValues,DatabaseHelper.COL_TAG_ID+"=?",
                new String[]{String.valueOf(categoriesModel.getTagID())});
        sqLiteDatabase.close();
        return true;
    }


    //fetch all the tags title strings from the database
    @SuppressLint("Range")
    public ArrayList<String> fetchTagStrings(){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        ArrayList<String> tagsModels=new ArrayList<>();
        String query="SELECT " + DatabaseHelper.COL_TAG_TITLE+ " FROM " + DatabaseHelper.TABLE_TAG_NAME;
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        while (cursor.moveToNext()){
            tagsModels.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TAG_TITLE)));
        }
        cursor.close();
        sqLiteDatabase.close();
        return tagsModels;
    }

    //fetch tag title from the database according to the tag id
    @SuppressLint("Range")
    public String fetchTagTitle(int tagID){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        String fetchTitle="SELECT " + DatabaseHelper.COL_TAG_TITLE + " FROM " + DatabaseHelper.TABLE_TAG_NAME
                + " WHERE " + DatabaseHelper.COL_TAG_ID+"=?";
        Cursor cursor=sqLiteDatabase.rawQuery(fetchTitle,new String[]{String.valueOf(tagID)});
        String title="";
        if(cursor.moveToFirst()){
            title=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TAG_TITLE));
        }
        cursor.close();
        sqLiteDatabase.close();
        return title;
    }

    //fetch tag id from the database according to the tag title
    @SuppressLint("Range")
    public int fetchTagID(String tagTitle){
        SQLiteDatabase sqLiteDatabase=this.databaseHelper.getReadableDatabase();
        String fetchTitle="SELECT " + DatabaseHelper.COL_TAG_ID + " FROM " + DatabaseHelper.TABLE_TAG_NAME
                + " WHERE " + DatabaseHelper.COL_TAG_TITLE+"=?";
        Cursor cursor=sqLiteDatabase.rawQuery(fetchTitle,new String[]{tagTitle});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_TAG_ID));
    }
}
