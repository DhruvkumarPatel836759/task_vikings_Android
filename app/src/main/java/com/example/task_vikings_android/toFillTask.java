package com.example.task_vikings_android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class toFillTask extends AppCompatActivity {


    EditText addTaskTitle,addTaskDiscription,taskDate,taskTime,taskEvent;
    DatabaseHelper mydb;
    Button addTask;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TextView dayShow,dateShow,monthShow;


    int tHours,tMinutes;


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
        dayShow=findViewById(R.id.dayShow);
        dateShow=findViewById(R.id.dateShow);
        monthShow=findViewById(R.id.monthShow);

        Calendar calendar=Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        toFillTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month=month+1;
                        String date=day+"/"+month+"/"+year;
                        taskDate.setText(date);
//                        String mon = "";
//                        if(month==1)
//                            mon = "Jan";
//                        if(month==2)
//                            mon = "Feb";
//                        if(month==3)
//                            mon = "Mar";
//                        if(month==4)
//                            mon = "Apr";
//                        if(month==5)
//                            mon = "May";
//                        if(month==6)
//                            mon = "Jun";
//                        if(month==7)
//                            mon = "Jul";
//                        if(month==8)
//                            mon = "Aug";
//                        if(month==9)
//                            mon = "Sep";
//                        if(month==10)
//                            mon = "Oct";
//                        if(month==11)
//                            mon = "Nov";
//                        if(month==12)
//                            mon = "Dec";
//                        mon = "Jan";
//
//                        dayShow.setText(mon);
//

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog= new TimePickerDialog(
                        toFillTask.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
//                        initialize hours and minutes
                        tHours=hourOfDay;
                        tMinutes=minute;

                        //initialize calendar
                        Calendar calendar1=Calendar.getInstance();

//                        set hours and minutes
                        calendar1.set(0,0,0,tHours,tMinutes);
//                        set selected time on edit text
                        taskTime.setText(DateFormat.format("hh:mm aa",calendar1));
                    }
                },12,0,false
                );
                //Displayed previous selected time
                timePickerDialog.updateTime(tHours,tMinutes);
//                show the dialog
                timePickerDialog.show();
            }

        });

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