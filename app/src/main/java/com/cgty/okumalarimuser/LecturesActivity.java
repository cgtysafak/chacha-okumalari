package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cgty.okumalarimuser.interfaces.ItemClickListener;
import com.cgty.okumalarimuser.model.Lecture;
import com.cgty.okumalarimuser.model.Sections;
import com.cgty.okumalarimuser.viewholder.LectureViewHolder;
import com.cgty.okumalarimuser.viewholder.SectionsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class LecturesActivity extends AppCompatActivity
{
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
    // Searching...
    FirebaseRecyclerAdapter<Lecture, LectureViewHolder> searchAdapter;
    List<String> suggestionList = new ArrayList<>();;
    MaterialSearchBar materialSearchBar;
    
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
    
        // Initializing Search variables...
        materialSearchBar = findViewById(R.id.searchBarLectures);
        materialSearchBar.setHint("Search Lectures....");
    
        showSuggestions();
    
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            
            }
        
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Displaying the suggestion list when user types.
                List<String> suggestion;
                suggestion = new ArrayList<String>();
            
                for (String search:suggestionList)  //loop at suggestion list.
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        suggestion.add(search);
                    }
                }
            
                materialSearchBar.setLastSuggestions(suggestion);
            }
        
            @Override
            public void afterTextChanged(Editable s)
            {
            
            }
        });
    
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener()
        {
            @Override
            public void onSearchStateChanged(boolean enabled)
            {
                //Loading the original adapter when the search bar deactivated.
                if (!enabled)
                {
                    recycler_lecture.setAdapter(adapter);
                }
            }
        
            @Override
            public void onSearchConfirmed(CharSequence text)
            {
                //Showing the search adapter when the searching process is done.
                startSearching(text);
            }
        
            @Override
            public void onButtonClicked(int buttonCode)
            {
            
            }
        });
    }
    
    private void startSearching(CharSequence text)
    {
        //Starting the filtering.
        Query searchQueryByName;
        searchQueryByName = lecturePath.orderByChild("lectureName").equalTo(text.toString());
    
        //Creating options by the query.
        FirebaseRecyclerOptions<Lecture> lectureOptions;
        lectureOptions = new FirebaseRecyclerOptions.Builder<Lecture>().setQuery(searchQueryByName, Lecture.class).build();
    
        searchAdapter = new FirebaseRecyclerAdapter<Lecture, LectureViewHolder>(lectureOptions)
        {
            @Override
            protected void onBindViewHolder(@NonNull LectureViewHolder lectureViewHolder, int i, @NonNull Lecture lecture)
            {
                lectureViewHolder.txtLectureName.setText(lecture.getLectureName());
                Picasso.with(getBaseContext()).load(lecture.getImage()).into(lectureViewHolder.imageView);
    
                Lecture local;
                local = lecture;
    
                lectureViewHolder.setItemClickListener(new ItemClickListener()
                {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() + " Category Name: " + tiklandiginda.getName(), Toast.LENGTH_SHORT).show();
                        Intent lecture;
                        lecture = new Intent( LecturesActivity.this, LectureDetailsActivity.class);
    
                        lecture.putExtra("LectureId", searchAdapter.getRef(position).getKey());
    
                        startActivity(lecture);
                    }
                });
            }
        
            @NonNull
            @Override
            public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View itemView;
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_line_element, parent,false);
            
                return new LectureViewHolder(itemView);
            }
        };
    
        searchAdapter.startListening();
        recycler_lecture.setAdapter(searchAdapter);  //Transferring the results to the recycler.
    }
    
    private void showSuggestions()
    {
        lecturePath.orderByChild("lectureName").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot postSnapshot:snapshot.getChildren())
                {
                    Lecture item;
                    item = postSnapshot.getValue(Lecture.class);
                
                    //assert item != null;
                    suggestionList.add(item.getLectureName()); //Adding the names of desired lectures.
                }
            
                materialSearchBar.setLastSuggestions(suggestionList);
            }
        
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            
            }
        });
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
						Intent lecture;
                        lecture = new Intent( LecturesActivity.this, LectureDetailsActivity.class);
    
                        lecture.putExtra("LectureId", adapter.getRef(position).getKey());
						
						startActivity(lecture);
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