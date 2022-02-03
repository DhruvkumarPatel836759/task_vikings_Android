package com.example.task_vikings_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class toFillTitle_ListAdapter extends ArrayAdapter<User> {
    private LayoutInflater myLayoutInflator;
    private ArrayList<User> users;
    private int myViewResourceId;

    public toFillTitle_ListAdapter(Context context, int textViewResouceId, ArrayList<User> users){
        super(context,textViewResouceId,users);
        this.users = users;
        myLayoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myViewResourceId = textViewResouceId;
    }
    public View getView(int position, View convertView, ViewGroup parents){
        convertView = myLayoutInflator.inflate(myViewResourceId,null);

        User user = users.get(position);

        if (user != null){
            TextView title = (TextView) convertView.findViewById(R.id.titleTL);
//            TextView description = (TextView) convertView.findViewById(R.id.descriptionShow);
//            TextView date = (TextView) convertView.findViewById(R.id.dateShow);
//            TextView time = (TextView) convertView.findViewById(R.id.timeShow);
//            TextView event = (TextView) convertView.findViewById(R.id.eventShow);

            if (title != null) {
                title.setText(user.getTitle());
            }
//            if (description != null) {
//                description.setText(user.getDescription());
//            }
//            if (date != null) {
//                date.setText(user.getDate());
//            }
//            if (time != null) {
//                time.setText(user.getTime());
//            }
//            if (event != null) {
//                event.setText(user.getEvent());
//            }
        }
        return convertView;
    }
}
