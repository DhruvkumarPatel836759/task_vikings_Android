package com.example.task_vikings_android.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_vikings_android.R;
import com.example.task_vikings_android.activities.CompletedTasks;
import com.example.task_vikings_android.activities.MainActivity;
import com.example.task_vikings_android.helpers.CategoriesDBHelper;
import com.example.task_vikings_android.helpers.TaskDBHelper;
import com.example.task_vikings_android.models.ImageModel;
import com.example.task_vikings_android.models.PendingTaskModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by asifkhan on 12/27/17.
 */

public class PendingTaskAdapter extends RecyclerView.Adapter<PendingTaskAdapter.PendingDataHolder>{
    private ArrayList<PendingTaskModel> pendingTaskModels;
    private Context context;
    private String getTagTitleString;
    private CategoriesDBHelper categoriesDBHelper;
    private TaskDBHelper taskDBHelper;
    ImagesAdapter imagesAdapter;

    public PendingTaskAdapter(ArrayList<PendingTaskModel> pendingTaskModels, Context context) {
        this.pendingTaskModels = pendingTaskModels;
        this.context = context;
    }

    @Override
    public PendingTaskAdapter.PendingDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pending_task_layout,parent,false);
        return new PendingDataHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingTaskAdapter.PendingDataHolder holder, int position) {
        taskDBHelper =new TaskDBHelper(context);
        final PendingTaskModel pendingTaskModel = pendingTaskModels.get(position);
        holder.todoTitle.setText(pendingTaskModel.getTodoTitle());
        holder.todoContent.setText(pendingTaskModel.getTodoContent());
        holder.todoDate.setText(pendingTaskModel.getTodoDate());
        holder.todoTag.setText(pendingTaskModel.getTodoTag());
        holder.todoTime.setText(pendingTaskModel.getTodoTime());
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.task_edit_del_options,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                showDialogEdit(pendingTaskModel.getTodoID(),pendingTaskModel);
                                return true;
                            case R.id.delete:
                                showDeleteDialog(pendingTaskModel.getTodoID());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
        holder.makeCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompletedDialog(pendingTaskModel.getTodoID());
            }
        });

        holder.mShowImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ImageModel> imagesByTask = taskDBHelper.getImagesByTask(pendingTaskModel.getTodoID());
                Log.e("liST",imagesByTask.size()+"");
                if(imagesByTask.size()==0){
                    Toast.makeText(context, "No Images found", Toast.LENGTH_SHORT).show();
                }else{
                    setImagesAdapter(imagesByTask);
                }
            }
        });

        holder.btnPly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pendingTaskModel.getFileName().equalsIgnoreCase("")){
                    Toast.makeText(context, "No Audio found", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        MediaPlayer mediaPlayer=new MediaPlayer();
                        mediaPlayer.setDataSource(pendingTaskModel.getFileName());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(context,"recording is Playing ",Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setImagesAdapter(List<ImageModel> imagesByTask) {
        showDialog(context,imagesByTask);
    }

    public  void showDialog(Context context, List<ImageModel> list) {
        Dialog alertDialog;
        alertDialog = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.image_layout, null);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        imagesAdapter=new ImagesAdapter(context,list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setAdapter(imagesAdapter);
        imagesAdapter.notifyDataSetChanged();


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //this line MUST BE BEFORE setContentView
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        alertDialog.setContentView(view);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //showing confirmation dialog for deleting the todos
    private void showDeleteDialog(final int tagID){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Todo delete confirmation");
        builder.setMessage("Do you really want to delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(taskDBHelper.removeTodo(tagID)){
                    Toast.makeText(context, "Todo deleted successfully !", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Todo not deleted !", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }).create().show();
    }

    @Override
    public int getItemCount() {
        return pendingTaskModels.size();
    }

    //showing edit dialog for editing todos according to the todoid
    private void showDialogEdit(final int todoID, PendingTaskModel pendingTaskModel){
        taskDBHelper =new TaskDBHelper(context);
        categoriesDBHelper =new CategoriesDBHelper(context);
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.edit_task_dialog,null);
        builder.setView(view);
//        SettingsHelper.applyThemeTextView((TextView)view.findViewById(R.id.edit_todo_dialog_title),context);
        final TextInputEditText todoTitle=(TextInputEditText)view.findViewById(R.id.task_title);
        final TextInputEditText todoContent=(TextInputEditText)view.findViewById(R.id.todo_content);
        Spinner todoTags=(Spinner)view.findViewById(R.id.todo_tag);
        //stores all the tags title in string format
        ArrayAdapter<String> tagsModelArrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, categoriesDBHelper.fetchTagStrings());
        //setting dropdown view resouce for spinner
        tagsModelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting the spinner adapter
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

        //setting the default values coming from the database
        todoTitle.setText(taskDBHelper.fetchTodoTitle(todoID));
        todoContent.setText(taskDBHelper.fetchTodoContent(todoID));
        todoDate.setText(taskDBHelper.fetchTodoDate(todoID));
        todoTime.setText(taskDBHelper.fetchTodoTime(todoID));

        //getting current calendar credentials
        final Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        final int hour=calendar.get(Calendar.HOUR);
        final int minute=calendar.get(Calendar.MINUTE);

        //getting the tododate
        todoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog timePickerDialog=new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar newCalendar=Calendar.getInstance();
                        newCalendar.set(Calendar.HOUR,i);
                        newCalendar.set(Calendar.MINUTE,i1);
                        String timeFormat= DateFormat.getTimeInstance(DateFormat.SHORT).format(newCalendar.getTime());
                        todoTime.setText(timeFormat);
                    }
                },hour,minute,false);
                timePickerDialog.show();
            }
        });
        TextView cancel=(TextView)view.findViewById(R.id.cancel);
        TextView addTodo=(TextView)view.findViewById(R.id.add_new_todo);
//        SettingsHelper.applyTextColor(cancel,context);
//        SettingsHelper.applyTextColor(addTodo,context);
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
                }else if(taskDBHelper.updateTodo(
                        new PendingTaskModel(todoID,getTodoTitle,getTodoContent,String.valueOf(todoTagID),getTodoDate,getTime,pendingTaskModel.getFileName())
                )){
                    Toast.makeText(context, R.string.todo_title_add_success_msg, Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context,MainActivity.class));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,MainActivity.class));
            }
        });
        builder.create().show();
    }

    //showing confirmation dialog for making the todo completed
    private void showCompletedDialog(final int tagID){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Todo completion dialog");
        builder.setMessage("Completed the todo ?");
        builder.setPositiveButton("Completed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(taskDBHelper.makeCompleted(tagID)){
                    context.startActivity(new Intent(context, CompletedTasks.class));
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    public class PendingDataHolder extends RecyclerView.ViewHolder {
        TextView todoTitle,todoContent,todoTag,todoDate,todoTime;
        ImageView option,makeCompleted;
        Button mShowImagesBtn;
        ImageButton btnPly;
        public PendingDataHolder(View itemView) {
            super(itemView);
            todoTitle=(TextView)itemView.findViewById(R.id.pending_todo_title);
            todoContent=(TextView)itemView.findViewById(R.id.pending_todo_content);
            todoTag=(TextView)itemView.findViewById(R.id.todo_tag);
            todoDate=(TextView)itemView.findViewById(R.id.todo_date);
            todoTime=(TextView)itemView.findViewById(R.id.todo_time);
            option=(ImageView)itemView.findViewById(R.id.option);
            makeCompleted=(ImageView)itemView.findViewById(R.id.make_completed);
            mShowImagesBtn=itemView.findViewById(R.id.mShowImagesBtn);
            btnPly=itemView.findViewById(R.id.btnPly);
        }
    }

    //filter the search
    public void filterTodos(ArrayList<PendingTaskModel> newPendingTaskModels){
        pendingTaskModels =new ArrayList<>();
        pendingTaskModels.addAll(newPendingTaskModels);
        notifyDataSetChanged();
    }




    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

        Context context;
        List<ImageModel> childFeedList;

        public ImagesAdapter(Context context, List<ImageModel> childFeedList) {
            this.context = context;
            this.childFeedList = childFeedList;
        }

        @Override
        public ImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_design, parent, false);
            return new ImagesAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImagesAdapter.MyViewHolder holder, final int position) {
            ImageModel ledger = childFeedList.get(position);
            byte[] image = ledger.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.mImage.setImageBitmap(bitmap);



        }


        @Override
        public int getItemCount() {
            return childFeedList.size();
        }




        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView mImage;
            public MyViewHolder(View itemView) {
                super(itemView);

                mImage = itemView.findViewById(R.id.mImage);

            }
        }
    }
}
