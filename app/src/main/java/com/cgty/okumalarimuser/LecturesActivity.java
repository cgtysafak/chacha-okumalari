package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cgty.okumalarimuser.interfaces.ItemClickListener;
import com.cgty.okumalarimuser.model.Lecture;
import com.cgty.okumalarimuser.viewholder.LectureViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;

public class LecturesActivity extends AppCompatActivity
{
    // To add new lecture...
    Button button_addLecture;
    FButton button_selectLecture;
    FButton button_uploadLecture;
    MaterialEditText editText_lectureName;
    MaterialEditText editText_malz;
    MaterialEditText editText_notes;
    MaterialEditText editText_linkToWatch;
    Uri uriToSave;
    // RecyclerView...
    RecyclerView recycler_lecture;
    RecyclerView.LayoutManager layoutManager;
    // Firebase...
    FirebaseDatabase database;
    DatabaseReference lecturePath;
    FirebaseStorage storage;
    StorageReference imagePath;
    FirebaseRecyclerAdapter<Lecture, LectureViewHolder> adapter;
    // Get Intent...
    Lecture newLecture;
    String sectionId = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);
    
        // Initializing Firebase variables...
        database = FirebaseDatabase.getInstance();
        lecturePath = database.getReference("Lectures");
        storage = FirebaseStorage.getInstance();
        imagePath = storage.getReference();
    
        // Initializing Recycler variables...
        recycler_lecture = findViewById(R.id.recycler_lectures);
        recycler_lecture.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recycler_lecture.setLayoutManager(layoutManager);
    
        //getting Intent
        if (getIntent() != null)
        {
            sectionId = getIntent().getStringExtra("SectionId");
        }
    
        if (!sectionId.isEmpty())
        {
            uploadLectures(sectionId);
        }
    }
    
    private void uploadLectures(String sectionId)
    {
        // To list correct Lectures in each Section's RecyclerList.
        Query filter;
        filter = lecturePath.orderByChild("sectionid").equalTo(sectionId);
    
        final FirebaseRecyclerOptions<Lecture> options;
        options = new FirebaseRecyclerOptions.Builder<Lecture>().setQuery(filter, Lecture.class).build();
    
        adapter = new FirebaseRecyclerAdapter<Lecture, LectureViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull LectureViewHolder lectureViewHolder, int position,
                                            @NonNull Lecture lecture)
            {
                lectureViewHolder.txtLectureName.setText(lecture.getLectureName());
                Picasso.with(getBaseContext()).load(lecture.getImage()).into(lectureViewHolder.imageView);
            
                final Lecture clickedOne = lecture;
            
                lectureViewHolder.setItemClickListener(new ItemClickListener()
                {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() +
//						" Category Name: " + clickedOne.getName(), Toast.LENGTH_SHORT).show();
						/*Intent sections;
						sections = new Intent( LecturesActivity.this, LecturesActivity.class);
						
						sections.putExtra("SectionId", adapter.getRef(position).getKey());
						
						startActivity(sections);*/
                    }
                });
            }
        
            @NonNull
            @Override
            public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lecture_line_element, parent, false);
            
                return new LectureViewHolder(itemView);
            }
        };
    
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_lecture.setAdapter(adapter);
    }
}