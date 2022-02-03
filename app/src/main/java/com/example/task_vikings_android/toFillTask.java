package com.example.task_vikings_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class toFillTask extends AppCompatActivity {


    EditText addTaskTitle,addTaskDiscription,taskDate,taskTime,taskEvent;
    DatabaseHelper mydb;
    Button addTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_fill_task);

        addTaskTitle = findViewById(R.id.addTaskTitle);
        addTaskDiscription = findViewById(R.id.addTaskDescription);
        taskDate = findViewById(R.id.taskDate);
        taskTime = findViewById(R.id.taskTime);
        taskEvent = findViewById(R.id.taskEvent);
        addTask = findViewById(R.id.addTask);

        mydb = new DatabaseHelper(this);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = addTaskTitle.getText().toString();
                String description = addTaskDiscription.getText().toString();
                String date = taskDate.getText().toString();
                String time = taskTime.getText().toString();
                String event = taskEvent.getText().toString();

                if(title.length() != 0 && description.length() != 0 && date.length() != 0 &&
                        time.length() != 0 && event.length() != 0 ) {
                    AddData(title,description,date,time,event);
                    addTaskTitle.setText("");
                    addTaskDiscription.setText("");
                    taskDate.setText("");
                    taskTime.setText("");
                    taskEvent.setText("");
                    toReturn();
                }else {
                    Toast.makeText(toFillTask.this, "You must fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void AddData(String title,String description, String date, String time, String event){
        boolean insertData = mydb.addData(title,description,date,time,event);
        
        if(insertData == true){
            Toast.makeText(this, title + " Task is added Successfully!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something is wrong! please Try again", Toast.LENGTH_SHORT).show();
        }
    }
    public void toReturn(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}