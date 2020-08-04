package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cgty.okumalarimuser.interfaces.ItemClickListener;
import com.cgty.okumalarimuser.model.Category;
import com.cgty.okumalarimuser.model.Sections;
import com.cgty.okumalarimuser.viewholder.CategoryViewHolder;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
	// Searching...
	FirebaseRecyclerAdapter<Sections, SectionsViewHolder> searchAdapter;
	List<String> suggestionList = new ArrayList<>();
	MaterialSearchBar materialSearchBar;
	
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
		
		//getting Intent
		if (getIntent() != null)
		{
			categoryId = getIntent().getStringExtra("CategoryId");
		}
		
		if (!categoryId.isEmpty())
		{
			uploadSections(categoryId);
		}
		
		// Initializing Search variables...
		materialSearchBar = findViewById(R.id.searchBarSections);
		materialSearchBar.setHint("Search Sections....");
		
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
					recycler_sections.setAdapter(adapter);
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
		searchQueryByName = sectionPath.orderByChild("name").equalTo(text.toString());
		
		//Creating options by the query.
		FirebaseRecyclerOptions<Sections> sectionOptions;
		sectionOptions = new FirebaseRecyclerOptions.Builder<Sections>().setQuery(searchQueryByName, Sections.class).build();
		
		searchAdapter = new FirebaseRecyclerAdapter<Sections, SectionsViewHolder>(sectionOptions)
		{
			@Override
			protected void onBindViewHolder(@NonNull SectionsViewHolder sectionsViewHolder, int i, @NonNull Sections sections)
			{
				sectionsViewHolder.txtSectionName.setText(sections.getName());
				Picasso.with(getBaseContext()).load(sections.getImage()).into(sectionsViewHolder.imageView);
				
				Sections local = sections;
				
				sectionsViewHolder.setItemClickListener(new ItemClickListener()
				{
					@Override
					public void onClick(View view, int position, boolean isLongClick)
					{
						//Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() + " Category Name: " + tiklandiginda.getName(), Toast.LENGTH_SHORT).show();
						Intent sections;
						sections = new Intent( SectionsActivity.this, LecturesActivity.class);
						
						sections.putExtra("SectionId", searchAdapter.getRef(position).getKey());
						
						startActivity(sections);
					}
				});
			}
			
			@NonNull
			@Override
			public SectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
			{
				View itemView;
				itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_line_element, parent,false);
				
				return new SectionsViewHolder(itemView);
			}
		};
		
		searchAdapter.startListening();
		recycler_sections.setAdapter(searchAdapter);  //Transferring the results to the recycler.
	}
	
	private void showSuggestions()
	{
		sectionPath.orderByChild("name").equalTo(categoryId).addValueEventListener(new ValueEventListener()  //.equalTo(categoryId).addValue...
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot)
			{
				for (DataSnapshot postSnapshot:snapshot.getChildren())
				{
					Sections item = postSnapshot.getValue(Sections.class);
					
					suggestionList.add(item.getName()); //Adding the names of desired sections.
				}
				
				materialSearchBar.setLastSuggestions(suggestionList);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error)
			{
			
			}
		});
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