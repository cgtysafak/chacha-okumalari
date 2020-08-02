package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cgty.okumalarimuser.interfaces.ItemClickListener;
import com.cgty.okumalarimuser.model.Sections;
import com.cgty.okumalarimuser.viewholder.SectionsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SectionsActivity extends AppCompatActivity
{
	// Firebase...
	FirebaseDatabase database;
	DatabaseReference sectionPath;
	FirebaseStorage storage;
	StorageReference imagePath;
	FirebaseRecyclerAdapter<Sections, SectionsViewHolder> adapter;
	// RecyclerView...
	RecyclerView recycler_sections;
	RecyclerView.LayoutManager layoutManager;
	// Get Intent...
	Sections newSection;
	String categoryId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sections);
		
		// Initializing Firebase variables...
		database = FirebaseDatabase.getInstance();
		sectionPath = database.getReference("Sections");
		storage = FirebaseStorage.getInstance();
		imagePath = storage.getReference();
		
		// Initializing Recycler variables...
		recycler_sections = findViewById(R.id.recycler_sections);
		recycler_sections.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		recycler_sections.setLayoutManager(layoutManager);
		
		if (getIntent() != null)
		{
			categoryId = getIntent().getStringExtra("CategoryId");
		}
		
		if (!categoryId.isEmpty())
		{
			uploadSections(categoryId);
		}
	}
	
	private void uploadSections(String categoryId)
	{
		Query filter;
		filter = sectionPath.orderByChild("categoryid").equalTo(categoryId);
		
		final FirebaseRecyclerOptions<Sections> secenekler;
		secenekler = new FirebaseRecyclerOptions.Builder<Sections>().setQuery(filter, Sections.class).build();
		
		adapter = new FirebaseRecyclerAdapter<Sections, SectionsViewHolder>(secenekler)
		{
			@Override
			protected void onBindViewHolder(@NonNull SectionsViewHolder sectionsViewHolder, int position, @NonNull Sections sections)
			{
				sectionsViewHolder.txtSectionName.setText(sections.getName());
				Picasso.with(getBaseContext()).load(sections.getImage()).into(sectionsViewHolder.imageView);
				
				final Sections tiklandiginda = sections;
				
				sectionsViewHolder.setItemClickListener(new ItemClickListener()
				{
					@Override
					public void onClick(View view, int position, boolean isLongClick)
					{
						//Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() + " Category Name: " + tiklandiginda.getName(), Toast.LENGTH_SHORT).show();
						Intent sections;
						sections = new Intent( SectionsActivity.this, LecturesActivity.class);
						
						sections.putExtra("SectionId", adapter.getRef(position).getKey());
						
						startActivity(sections);
					}
				});
			}
			
			@NonNull
			@Override
			public SectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
			{
				View itemView = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.section_line_element, parent, false);
				
				return new SectionsViewHolder(itemView);
			}
		};
		
		adapter.startListening();
		adapter.notifyDataSetChanged();
		recycler_sections.setAdapter(adapter);
	}
}