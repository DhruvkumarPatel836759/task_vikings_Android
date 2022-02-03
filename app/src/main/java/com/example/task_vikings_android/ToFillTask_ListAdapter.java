package com.example.task_vikings_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ToFillTask_ListAdapter extends RecyclerView.Adapter<ToFillTask_ListAdapter.ExampleViewHolder> {
    private ArrayList<User> mUserList;
    private ItemClickListner mItemListner;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView description;
        TextView date;
        TextView time;
        TextView event;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleShow);
            description = itemView.findViewById(R.id.descriptionShow);
            date = itemView.findViewById(R.id.dateShow);
            time = itemView.findViewById(R.id.timeShow);
            event = itemView.findViewById(R.id.eventShow);
        }
    }

    public ToFillTask_ListAdapter(ArrayList<User> userList,ItemClickListner itemClickListner){
        mUserList = userList;
        this.mItemListner = itemClickListner;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list,parent,false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        User currentItem = mUserList.get(position);

        if (holder.title != null) {
            holder.title.setText(currentItem.getTitle());
        }
        if (holder.description != null) {
            holder.description.setText(currentItem.getDescription());
        }
        if (holder.date != null) {
            holder.date.setText(currentItem.getDate());
        }
        if (holder.time != null) {
            holder.time.setText(currentItem.getTime());
        }
        if (holder.event != null) {
            holder.event.setText(currentItem.getEvent());
        }

        holder.itemView.setOnClickListener(view -> {
            mItemListner.OnItemClick(mUserList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
    public interface ItemClickListner{
        void OnItemClick(User user);
    }

}
