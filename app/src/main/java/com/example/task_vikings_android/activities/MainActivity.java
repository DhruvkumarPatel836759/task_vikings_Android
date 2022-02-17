package com.example.task_vikings_android.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.adapters.PendingTaskAdapter;
import com.example.task_vikings_android.helpers.CategoriesDBHelper;
import com.example.task_vikings_android.helpers.TaskDBHelper;
import com.example.task_vikings_android.models.PendingTaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{
    private RecyclerView pendingTodos;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PendingTaskModel> pendingTaskModels;
    private PendingTaskAdapter pendingTaskAdapter;
    private FloatingActionButton addNewTodo;
    private CategoriesDBHelper categoriesDBHelper;
    private String getTagTitleString;
    private TaskDBHelper taskDBHelper;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar( (Toolbar) findViewById(R.id.toolbar));

        setTitle(getString(R.string.app_title));

        loadPendingTodos();
    }

    //loading all the pending todos
    private void loadPendingTodos(){
        pendingTodos=(RecyclerView)findViewById(R.id.pending_todos_view);
        linearLayout=(LinearLayout)findViewById(R.id.no_pending_todo_section);
        categoriesDBHelper =new CategoriesDBHelper(this);
        taskDBHelper =new TaskDBHelper(this);

        if(taskDBHelper.countTodos()==0){
            linearLayout.setVisibility(View.VISIBLE);
            pendingTodos.setVisibility(View.GONE);
        }else{
            pendingTaskModels =new ArrayList<>();
            pendingTaskModels = taskDBHelper.fetchAllTodos();
            pendingTaskAdapter =new PendingTaskAdapter(pendingTaskModels,this);
        }
        linearLayoutManager=new LinearLayoutManager(this);
        pendingTodos.setAdapter(pendingTaskAdapter);
        pendingTodos.setLayoutManager(linearLayoutManager);
        addNewTodo=(FloatingActionButton)findViewById(R.id.fabAddTodo);
        addNewTodo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddTodo:
                if(categoriesDBHelper.countTags()==0){
                    showDialog();
                }else{
                    showNewTodoDialog();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pending_task_options,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                ArrayList<PendingTaskModel> newPendingTaskModels =new ArrayList<>();
                for(PendingTaskModel pendingTaskModel : pendingTaskModels){
                    String getTodoTitle= pendingTaskModel.getTodoTitle().toLowerCase();
                    String getTodoContent= pendingTaskModel.getTodoContent().toLowerCase();
                    String getTodoTag= pendingTaskModel.getTodoTag().toLowerCase();

                    if(getTodoTitle.contains(newText) || getTodoContent.contains(newText) || getTodoTag.contains(newText)){
                        newPendingTaskModels.add(pendingTaskModel);
                    }
                }
                pendingTaskAdapter.filterTodos(newPendingTaskModels);
                pendingTaskAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                return true;
            case R.id.all_tags:
                startActivity(new Intent(this, AllCategories.class));
                return true;
            case R.id.completed:
                startActivity(new Intent(this, CompletedTasks.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.pending_todos) {
            startActivity(new Intent(this,MainActivity.class));
        } else if (id == R.id.completed_todos) {
            startActivity(new Intent(this, CompletedTasks.class));
        } else if (id == R.id.tags) {
            startActivity(new Intent(this, AllCategories.class));

        }

        return true;
    }

    //show dialog if there is no tag in the database
    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.tag_create_dialog_title_text);
        builder.setMessage(R.string.no_tag_in_the_db_text);
        builder.setPositiveButton(R.string.create_new_tag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, AllCategories.class));
            }
        }).setNegativeButton(R.string.tag_edit_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    //show add new todos dialog and adding the todos into the database
    private void showNewTodoDialog(){
        //getting current calendar credentials
        final Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        final int hour=calendar.get(Calendar.HOUR);
        final int minute=calendar.get(Calendar.MINUTE);

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.add_new_task_dialog,null);
        builder.setView(view);

        final TextInputEditText todoTitle=(TextInputEditText)view.findViewById(R.id.todo_title);
        final TextInputEditText todoContent=(TextInputEditText)view.findViewById(R.id.todo_content);
        Spinner todoTags=(Spinner)view.findViewById(R.id.todo_tag);
        //stores all the tags title in string format
        ArrayAdapter<String> tagsModelArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, categoriesDBHelper.fetchTagStrings());

        tagsModelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        todoTags.setAdapter(tagsModelArrayAdapter);
        todoTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getTagTitleString=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final TextInputEditText todoDate=(TextInputEditText)view.findViewById(R.id.todo_date);
        final TextInputEditText todoTime=(TextInputEditText)view.findViewById(R.id.todo_time);

        //getting the tododate
        todoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);
                        todoDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime()));
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //getting the todos time
        todoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        calendar.set(Calendar.HOUR_OF_DAY,i);
                        calendar.set(Calendar.MINUTE,i1);
                        String timeFormat=DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                        todoTime.setText(timeFormat);
                    }
                },hour,minute,false);
                timePickerDialog.show();
            }
        });
        TextView cancel=(TextView)view.findViewById(R.id.cancel);
        TextView addTodo=(TextView)view.findViewById(R.id.add_new_todo);

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting all the values from add new todos dialog
                String getTodoTitle=todoTitle.getText().toString();
                String getTodoContent=todoContent.getText().toString();
                int todoTagID= categoriesDBHelper.fetchTagID(getTagTitleString);
                String getTodoDate=todoDate.getText().toString();
                String getTime=todoTime.getText().toString();

                //checking the data fiels
                boolean isTitleEmpty=todoTitle.getText().toString().isEmpty();
                boolean isContentEmpty=todoContent.getText().toString().isEmpty();
                boolean isDateEmpty=todoDate.getText().toString().isEmpty();
                boolean isTimeEmpty=todoTime.getText().toString().isEmpty();

                //adding the todos
                if(isTitleEmpty){
                    todoTitle.setError("Todo title required !");
                }else if(isContentEmpty){
                    todoContent.setError("Todo content required !");
                }else if(isDateEmpty){
                    todoDate.setError("Todo date required !");
                }else if(isTimeEmpty){
                    todoTime.setError("Todo time required !");
                }else if(taskDBHelper.addNewTodo(
                        new PendingTaskModel(getTodoTitle,getTodoContent,String.valueOf(todoTagID),getTodoDate,getTime)
                )){
                    Toast.makeText(MainActivity.this, R.string.todo_title_add_success_msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MainActivity.class));
            }
        });
        builder.create().show();
    }
}
