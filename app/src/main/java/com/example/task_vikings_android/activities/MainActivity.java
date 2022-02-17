package com.example.task_vikings_android.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView pendingTodos;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PendingTaskModel> pendingTaskModels;
    private PendingTaskAdapter pendingTaskAdapter;
    private FloatingActionButton addNewTodo;
    private CategoriesDBHelper categoriesDBHelper;
    private String getTagTitleString;
    private TaskDBHelper taskDBHelper;
    private LinearLayout linearLayout;
    private static final int REQUEST_CODE = 200;

    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int PICK_IMAGE = 2000;
    List<byte[]> multiImageArray  = new ArrayList<byte[]>();
    byte[] imageArray = null;
    //ImageAdapter adapter = new ImageAdapter();
    int selectType=-1;
    ImageView mImageSelected;
    RecyclerView rvList;
    ImageAdapter adapter = new ImageAdapter();

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String fileName="";

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
                    selectType=-1;
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

        Button mUploadImg=view.findViewById(R.id.mUploadImg);
        ImageButton mStartRec=view.findViewById(R.id.btnRcrd);
        ImageButton mStopRec=view.findViewById(R.id.btnStp);

        mStartRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    mStopRec.setVisibility(View.VISIBLE);
                    try{
                        String uuid = UUID.randomUUID().toString();
                        fileName = getExternalCacheDir().getAbsolutePath() + "/" + uuid + ".3gp";
                        Log.e("File Name", fileName);

                        mediaRecorder=new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setOutputFile(fileName);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(MainActivity.this,"Recording Has Started",Toast.LENGTH_LONG).show();

                        mStartRec.setVisibility(View.GONE);

                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    requestPermission();
                }
            }
        });

        mStopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartRec.setVisibility(View.VISIBLE);
                mStopRec.setVisibility(View.GONE);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder=null;
                Toast.makeText(MainActivity.this,"Recording has Stopped",Toast.LENGTH_LONG).show();
            }
        });
        mImageSelected=view.findViewById(R.id.imageSelected);
        rvList=view.findViewById(R.id.rvList);

        rvList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvList.setAdapter(adapter);

        Spinner todoTags=(Spinner)view.findViewById(R.id.todo_tag);
        //stores all the Categories title in string format
        ArrayAdapter<String> tagsModelArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, categoriesDBHelper.fetchTagStrings());
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
                }else if(selectType==-1){
                    Toast.makeText(MainActivity.this, "Please upload image", Toast.LENGTH_SHORT).show();
                }else{
                    long primaryKey = taskDBHelper.addNewTodo(new PendingTaskModel(getTodoTitle, getTodoContent, String.valueOf(todoTagID), getTodoDate, getTime,fileName));
                    if (primaryKey == -1) {
                        Toast.makeText(MainActivity.this, "Data is not inserted in database", Toast.LENGTH_SHORT).show();
                    }else{
                        if(selectType==1){
                            long l = taskDBHelper.insertImageByTaskId(imageArray, (int) primaryKey);
                            Log.e("Inserted",l+"");
                            Toast.makeText(MainActivity.this, "Data Successfully inserted", Toast.LENGTH_SHORT).show();
                        }else{
                            for(int i =0;i<multiImageArray.size();i++){
                                long l = taskDBHelper.insertImageByTaskId(multiImageArray.get(i), (int) primaryKey);
                            }
                            Toast.makeText(MainActivity.this, "Data Successfully inserted", Toast.LENGTH_SHORT).show();
                        }
                    }

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

        mUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    showImagePickerDialog();

                }else {
                    requestPermission();
                }
            }
        });
        builder.create().show();
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("How would you like to upload image? ");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }


    public boolean checkPermission() {
        int read = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(MainActivity.this, CAMERA);
        int audio = ContextCompat.checkSelfPermission(MainActivity.this, RECORD_AUDIO);

        return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
                && camera == PackageManager.PERMISSION_GRANTED && audio == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE, CAMERA,RECORD_AUDIO}, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3000:
                if (grantResults.length > 0) {
                    boolean read = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean write = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean audio = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (read && write && camera && audio) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
        multiImageArray.clear();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageSelected.setImageBitmap(imageBitmap);
            selectType = 1;
            imageArray = convertImageToByteArray(imageBitmap);
            mImageSelected.setVisibility(View.VISIBLE);
            rvList.setVisibility(View.GONE);
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            selectType = 2;
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            if(data.getData()!=null){
                try {
                    Uri mImageUri=data.getData();
                    mArrayUri.add(mImageUri);
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), mImageUri);
                    Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 120, 120, false);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                    byte[] byteArray = stream.toByteArray();
                    multiImageArray.add(byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        try {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri mImageUri = item.getUri();
                            mArrayUri.add(mImageUri);
                            Bitmap bmp = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), mImageUri);
                            Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 120, 120, false);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            multiImageArray.add(byteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    Log.v("TAG", "Selected Images" + mArrayUri.size());
                }
            }
            mImageSelected.setVisibility(View.GONE);
            rvList.setVisibility(View.VISIBLE);
            adapter.updateList(mArrayUri);
        }
    }

// to convert Image from bitmap to byte
    public  byte[] convertImageToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30,stream);
        return  stream.toByteArray();
    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        List<Uri> dataList = new ArrayList<Uri>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_design, parent, false);
            return new ImageAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.image.setImageURI(dataList.get(position));

        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            super.setHasStableIds(hasStableIds);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void updateList(List<Uri> dlist) {
            dataList.clear();
            dataList = new ArrayList<>(dlist);
            notifyDataSetChanged();
        }

        public void clearList() {
            dataList.clear();
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);

                image = (ImageView) itemView.findViewById(R.id.image);

            }
        }
    }


    //----------For Recording------------------
    private  boolean isMicroPhonePresent(){// method to check presence of microphone
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return  true;
        }else {
            return  false;
        }
    }
    private void getMicrophonePermission(){//code to get microphone access
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.RECORD_AUDIO
            },REQUEST_CODE);
        }
    }
    private String getRecordingPath(){
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File musicDirectory=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file=new File(musicDirectory,"testRecording"+".mp3");
        return file.getPath();

    }
}
