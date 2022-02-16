package com.example.task_vikings_android.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.helpers.TaskDBHelper;
import com.example.task_vikings_android.models.CompletedTaskModel;

import java.util.ArrayList;

/**
 * Created by asifkhan on 12/27/17.
 */

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedDataHolder>{
    private ArrayList<CompletedTaskModel> completedTaskModels;
    private Context context;
    private TaskDBHelper taskDBHelper;

    public CompletedTaskAdapter(ArrayList<CompletedTaskModel> completedTaskModels, Context context) {
        this.completedTaskModels = completedTaskModels;
        this.context = context;
    }

    @Override
    public CompletedTaskAdapter.CompletedDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_completed_task_layout,parent,false);
        return new CompletedDataHolder(view);
    }

    @Override
    public void onBindViewHolder(CompletedTaskAdapter.CompletedDataHolder holder, int position) {
        taskDBHelper =new TaskDBHelper(context);
        CompletedTaskModel completedTaskModel = completedTaskModels.get(position);
        holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.todoTitle.setText(completedTaskModel.getTodoTitle());
//        SettingsHelper.applyTextColor(holder.todoTitle,context);
        holder.todoContent.setText(completedTaskModel.getTodoContent());
        holder.todoTag.setText(completedTaskModel.getTodoTag());
        holder.todoDate.setText(completedTaskModel.getTodoDate());
        holder.todoTime.setText(completedTaskModel.getTodoTime());
    }

    @Override
    public int getItemCount() {
        return completedTaskModels.size();
    }

    public class CompletedDataHolder extends RecyclerView.ViewHolder {
        TextView todoTitle,todoContent,todoTag,todoDate,todoTime;
        public CompletedDataHolder(View itemView) {
            super(itemView);
            todoTitle=(TextView)itemView.findViewById(R.id.completed_todo_title);
            todoContent=(TextView)itemView.findViewById(R.id.completed_todo_content);
            todoTag=(TextView)itemView.findViewById(R.id.todo_tag);
            todoDate=(TextView)itemView.findViewById(R.id.todo_date);
            todoTime=(TextView)itemView.findViewById(R.id.todo_time);
        }
    }

    //filter the search
    public void filterCompletedTodos(ArrayList<CompletedTaskModel> newCompletedTaskModels){
        completedTaskModels =new ArrayList<>();
        completedTaskModels.addAll(newCompletedTaskModels);
        notifyDataSetChanged();
    }
}
