package com.example.task_vikings_android.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.adapters.CompletedTaskAdapter;
import com.example.task_vikings_android.helpers.TaskDBHelper;
import com.example.task_vikings_android.models.CompletedTaskModel;

import java.util.ArrayList;

public class CompletedTasks extends AppCompatActivity {
    private RecyclerView completedTodos;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<CompletedTaskModel> completedTaskModels;
    private CompletedTaskAdapter completedTaskAdapter;
    private LinearLayout linearLayout;
    private TaskDBHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.complete_todo_activity_title));
        loadCompletedTodos();
    }

    //loading all the completed todos
    private void loadCompletedTodos(){
        completedTodos=(RecyclerView)findViewById(R.id.completed_todos_view);
        taskDBHelper =new TaskDBHelper(this);
        linearLayout=(LinearLayout)findViewById(R.id.no_completed_todo_section) ;
        if(taskDBHelper.countCompletedTodos()==0){
            linearLayout.setVisibility(View.VISIBLE);
            completedTodos.setVisibility(View.GONE);
        }else{
            linearLayout.setVisibility(View.GONE);
            completedTodos.setVisibility(View.VISIBLE);
            completedTaskModels =new ArrayList<>();
            completedTaskModels = taskDBHelper.fetchCompletedTodos();
            completedTaskAdapter =new CompletedTaskAdapter(completedTaskModels,this);
        }
        linearLayoutManager=new LinearLayoutManager(this);
        completedTodos.setAdapter(completedTaskAdapter);
        completedTodos.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.completed_task_options,menu);
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
                ArrayList<CompletedTaskModel> newCompletedTaskModels =new ArrayList<>();
                for(CompletedTaskModel completedTaskModel : completedTaskModels){
                    String getTodoTitle= completedTaskModel.getTodoTitle();
                    String getTodoContent= completedTaskModel.getTodoContent();
                    String getTodoTag= completedTaskModel.getTodoTag();

                    if(getTodoTitle.contains(newText) || getTodoContent.contains(newText) || getTodoTag.contains(newText)){
                        newCompletedTaskModels.add(completedTaskModel);
                    }
                }
                completedTaskAdapter.filterCompletedTodos(newCompletedTaskModels);
                completedTaskAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                return true;
            case R.id.delete_all:
                deleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //showing the delete confirmation dialog
    private void deleteDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Todo delete confirmation");
        builder.setMessage("Do you really want to delete all the completed todos ?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(taskDBHelper.removeCompletedTodos()){
                    startActivity(new Intent(CompletedTasks.this, CompletedTasks.class));
                    Toast.makeText(CompletedTasks.this, "All Completed todo deleted successfully !", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(CompletedTasks.this, "Todos not deleted !", Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }
}
