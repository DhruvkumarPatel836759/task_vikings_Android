package com.example.task_vikings_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button addTaskButton;
    ArrayAdapter<String> arrayAdapter;

    List<String> todoList;
    DatabaseHelper dbsHlp;
    ArrayList<User> userlist;
    User user;

    Button button;

    private RecyclerView mRecyclerViewCat;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    FloatingActionButton mAddFab, mAddAlarmFab, addTaskFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoList = new ArrayList<>();

//        listView = findViewById(R.id.recyclerViewCat);

//        listView.setAdapter(arrayAdapter);

            button = findViewById(R.id.button);

            mAddFab = findViewById(R.id.add_fab);
            mAddAlarmFab = findViewById(R.id.add_alarm_fab);
            addTaskFab = findViewById(R.id.add_task_fab);

            addAlarmActionText = findViewById(R.id.add_alarm_action_text);
            addPersonActionText = findViewById(R.id.add_person_action_text);

            mAddAlarmFab.setVisibility(View.GONE);
            addTaskFab.setVisibility(View.GONE);
            addAlarmActionText.setVisibility(View.GONE);
            addPersonActionText.setVisibility(View.GONE);

            isAllFabsVisible = false;

//            --------------------------------
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    openTest();
            }
        });
//        --------------------------------------
            mAddFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isAllFabsVisible) {

                                mAddAlarmFab.show();
                                addTaskFab.show();
                                addAlarmActionText.setVisibility(View.VISIBLE);
                                addPersonActionText.setVisibility(View.VISIBLE);

                                isAllFabsVisible = true;
                            } else {
                                mAddAlarmFab.hide();
                                addTaskFab.hide();
                                addAlarmActionText.setVisibility(View.GONE);
                                addPersonActionText.setVisibility(View.GONE);

                                isAllFabsVisible = false;
                            }
                        }
                    });



          addTaskFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFillMenu();
                            Toast.makeText(MainActivity.this, "Task Added", Toast.LENGTH_SHORT).show();
                        }
                    });

            mAddAlarmFab.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Alarm Added", Toast.LENGTH_SHORT).show();
                        }
                    });
        dbsHlp = new DatabaseHelper(this);
        userlist = new ArrayList<>();
        Cursor data = dbsHlp.getListContents();
        int numRows = data.getCount();
        if(numRows == 0){
            Toast.makeText(this, "There is nothing in this database", Toast.LENGTH_SHORT).show();
        }else{

            while (data.moveToNext()){
                user = new User(data.getString(1),data.getString(2),data.getString(3),data.getString(4),
                        data.getString(5));
                userlist.add(user);
            }
            mRecyclerViewCat = findViewById(R.id.recyclerViewCat);
            mRecyclerViewCat.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mAdapter = new toFillTitle_ListAdapter(userlist, new toFillTitle_ListAdapter.ItemClickListner() {
                @Override
                public void OnItemClick(User user) {
                    Toast.makeText(MainActivity.this, user.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),subTask.class);
                    intent.putExtra("Title " ,user.getTitle());
                    startActivity(intent);
                }
            });

            mRecyclerViewCat.setLayoutManager(mLayoutManager);
            mRecyclerViewCat.setAdapter(mAdapter);

        }

    }

        public void openFillMenu(){
            Intent intent = new Intent(this,toFillTask.class);
            startActivity(intent);
        }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("WRITE HERE TO SEARCH");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public void openTest(){
        Intent intent = new Intent(this,subTask.class);
        startActivity(intent);
    }

}