package com.example.task_vikings_android.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.adapters.CategoriesAdapter;
import com.example.task_vikings_android.helpers.CategoriesDBHelper;
import com.example.task_vikings_android.models.CategoriesModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AllCategories extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView allTags;
    private ArrayList<CategoriesModel> categoriesModels;
    private CategoriesAdapter categoriesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fabAddTag;
    private CategoriesDBHelper categoriesDBHelper;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(getString(R.string.all_tags_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadTags();
    }

    //load all the tags
    private void loadTags(){
        allTags=(RecyclerView)findViewById(R.id.viewAllTags);
        linearLayout=(LinearLayout)findViewById(R.id.no_tags_available);
        categoriesDBHelper =new CategoriesDBHelper(this);
        if(categoriesDBHelper.countTags()==0){
            linearLayout.setVisibility(View.VISIBLE);
            allTags.setVisibility(View.GONE);
        }else{
            allTags.setVisibility(View.VISIBLE);
            categoriesModels =new ArrayList<>();
            categoriesModels = categoriesDBHelper.fetchTags();
            categoriesAdapter =new CategoriesAdapter(categoriesModels,this);
            linearLayout.setVisibility(View.GONE);
        }
        linearLayoutManager=new LinearLayoutManager(this);
        allTags.setAdapter(categoriesAdapter);
        allTags.setLayoutManager(linearLayoutManager);
        fabAddTag=(FloatingActionButton)findViewById(R.id.fabAddTag);
        fabAddTag.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddTag:
                showNewTagDialog();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vew_categories_option,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                ArrayList<CategoriesModel> newCategoriesModels =new ArrayList<>();
                for(CategoriesModel categoriesModel : categoriesModels){
                    String tagTitle= categoriesModel.getTagTitle().toLowerCase();
                    if(tagTitle.contains(newText)){
                        newCategoriesModels.add(categoriesModel);
                    }
                }
                categoriesAdapter.filterTags(newCategoriesModels);
                categoriesAdapter.notifyDataSetChanged();
                return false;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //show add new tag dialog
    private void showNewTagDialog(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.add_new_categories_dialog,null);
        builder.setView(view);
        final TextInputEditText tagTitle=(TextInputEditText)view.findViewById(R.id.tag_title);
        final TextView cancel=(TextView)view.findViewById(R.id.cancel);
        final TextView addNewtag=(TextView)view.findViewById(R.id.add_new_tag);

        addNewtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTagTitle=tagTitle.getText().toString();
                boolean isTagEmpty=tagTitle.getText().toString().isEmpty();
                boolean tagExists= categoriesDBHelper.tagExists(getTagTitle);

                if(isTagEmpty){
                    tagTitle.setError("Categories title required !");
                }else if(tagExists){
                    tagTitle.setError("Categories title already exists!");
                }else {
                    if(categoriesDBHelper.addNewTag(new CategoriesModel(getTagTitle))){
                        Toast.makeText(AllCategories.this, R.string.tag_title_add_success_msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AllCategories.this, AllCategories.class));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllCategories.this, AllCategories.class));
            }
        });
        builder.create().show();
    }
}
