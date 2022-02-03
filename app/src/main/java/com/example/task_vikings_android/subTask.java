package com.example.task_vikings_android;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class subTask extends AppCompatActivity {

    DatabaseHelper dbsHlp;
    ArrayList<User> userlist;
    User user;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);


        dbsHlp = new DatabaseHelper(this);
        userlist = new ArrayList<>();
        Cursor data = dbsHlp.getListContents();
        int numRows = data.getCount();
        if (numRows == 0) {
            Toast.makeText(this, "There is nothing in this database", Toast.LENGTH_SHORT).show();
        } else {

            while (data.moveToNext()) {
                user = new User(data.getString(1), data.getString(2), data.getString(3), data.getString(4),
                        data.getString(5));
                userlist.add(user);
            }

            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mAdapter = new ToFillTask_ListAdapter(userlist);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}