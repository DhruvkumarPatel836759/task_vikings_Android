package com.example.task_vikings_android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.activities.AllCategories;
import com.example.task_vikings_android.helpers.CategoriesDBHelper;
import com.example.task_vikings_android.models.CategoriesModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;



public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.TagDataHolder> {
    private ArrayList<CategoriesModel> categoriesModels;
    private Context context;
    private CategoriesDBHelper categoriesDBHelper;

    public CategoriesAdapter(ArrayList<CategoriesModel> categoriesModels, Context context) {
        this.categoriesModels = categoriesModels;
        this.context = context;
    }

    @Override
    public TagDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_all_categories_layout,parent,false);
        return new TagDataHolder(view);
    }

    @Override
    public void onBindViewHolder(TagDataHolder holder, int position) {
        final CategoriesModel categoriesModel = categoriesModels.get(position);
        holder.tag_title.setText(categoriesModel.getTagTitle());
        categoriesDBHelper =new CategoriesDBHelper(context);
        holder.tag_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.categories_edit_del_option,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                editTag(categoriesModel.getTagID());
                                return true;
                            case R.id.delete:
                                removeTag(categoriesModel.getTagID());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    public class TagDataHolder extends RecyclerView.ViewHolder{
        TextView tag_title;
        ImageView tag_option;
        public TagDataHolder(View itemView) {
            super(itemView);
            tag_title=(TextView)itemView.findViewById(R.id.tag_title);
            tag_option=(ImageView)itemView.findViewById(R.id.tags_option);
        }
    }

    //remove tag
    private void removeTag(final int tagID){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.tag_delete_dialog_title);
        builder.setMessage(R.string.tag_delete_dialog_msg);
        builder.setPositiveButton(R.string.tag_delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(categoriesDBHelper.removeTag(tagID)){
                    Toast.makeText(context, R.string.tag_deleted_success, Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, AllCategories.class));
                }
            }
        }).setNegativeButton(R.string.tag_delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, R.string.tag_no_delete, Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, AllCategories.class));
            }
        }).create().show();
    }

    //update tag
    private void editTag(final int tagID){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.edit_categories_dialog,null);
        builder.setView(view);

        final TextInputEditText tagEditTitle=(TextInputEditText)view.findViewById(R.id.edit_tag_title);
        tagEditTitle.setText(categoriesDBHelper.fetchTagTitle(tagID));
        final TextView cancel=(TextView)view.findViewById(R.id.cancel);
        final TextView editNewtag=(TextView)view.findViewById(R.id.edit_new_tag);


        editNewtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTagTitle=tagEditTitle.getText().toString();
                boolean isTagEmpty=tagEditTitle.getText().toString().isEmpty();
                boolean tagExists= categoriesDBHelper.tagExists(getTagTitle);

                if(isTagEmpty){
                    tagEditTitle.setError("Tag title required !");
                }else if(tagExists){
                    tagEditTitle.setError("Tag title already exists!");
                }else if(categoriesDBHelper.saveTag(new CategoriesModel(tagID,getTagTitle))){
                    Toast.makeText(context, R.string.tag_saved_success, Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, AllCategories.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, R.string.tag_no_save, Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, AllCategories.class));
            }
        });
        builder.create().show();
    }

    //search filter
    public void filterTags(ArrayList<CategoriesModel> newCategoriesModels){
        categoriesModels =new ArrayList<>();
        categoriesModels.addAll(newCategoriesModels);
        notifyDataSetChanged();
    }
}
