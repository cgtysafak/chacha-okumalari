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
import com.cgty.okumalarimuser.model.Category;
import com.cgty.okumalarimuser.viewholder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Main Activity class.
 * @author Çağatay Safak
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
{
	// Firebase...
	FirebaseDatabase database;
	DatabaseReference categoryPath;
	FirebaseStorage storage;
	StorageReference imagePath;
	FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
	// RecyclerView...
	RecyclerView recycler_category;
	RecyclerView.LayoutManager layoutManager;
	// Model...
	Category newCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initializing Firebase variables...
		database = FirebaseDatabase.getInstance();
		categoryPath = database.getReference("Category");
		storage = FirebaseStorage.getInstance();
		imagePath = storage.getReference();
		
		// Initializing Recycler variables...
		recycler_category = findViewById(R.id.recycler_category);
		recycler_category.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		recycler_category.setLayoutManager(layoutManager);
		
		uploadCategory();
	}
	
	private void uploadCategory()
	{
		final FirebaseRecyclerOptions<Category> secenekler = new FirebaseRecyclerOptions.Builder<Category>()
				.setQuery(categoryPath,Category.class)
				.build();
		
		adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(secenekler)
		{
			@Override
			protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position, @NonNull Category category)
			{
				categoryViewHolder.txtCategoryName.setText(category.getName());
				Picasso.with(getBaseContext()).load(category.getImage()).into(categoryViewHolder.imageView);
				
				final Category tiklandiginda = category;
				
				categoryViewHolder.setItemClickListener(new ItemClickListener()
				{
					@Override
					public void onClick(View view, int position, boolean isLongClick)
					{
						//Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() + " Category Name: " + tiklandiginda.getName(), Toast.LENGTH_SHORT).show();
						Intent sections;
						sections = new Intent( MainActivity.this, SectionsActivity.class);
						
						sections.putExtra("CategoryId", adapter.getRef(position).getKey());
						
						startActivity(sections);
					}
				});
			}
			
			@NonNull
			@Override
			public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
			{
				View itemView = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.category_line_element, parent, false);
				
				return new CategoryViewHolder(itemView);
			}
		};
		
		adapter.startListening();
		adapter.notifyDataSetChanged();
		recycler_category.setAdapter(adapter);
		/**
		 adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(secenekler)
		 {
		 @Override
		 protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model)
		 {
		 holder.txtCategoryName.setText(model.getName());
		 Picasso.with(getBaseContext()).load(model.getImage()).into(holder.imageView);
		 
		 final Category tiklandiginda = model;
		 
		 holder.setItemClickListener(new ItemClickListener()
		 {
		 @Override
		 public void onClick(View view, int position, boolean isLongClick) {
		 {
		 //Herbir satıra tıklantığında ne yapsın
		 //						Intent turler = new Intent(MainActivity.this,TurlerActivity.class);
		 
		 //						turler.putExtra("KategoriId",adapter.getRef(position).getKey());
		 
		 //						startActivity(turler);
		 }
		 });
		 
		 adapter.startListening();
		 adapter.notifyDataSetChanged();
		 recyclerCategory.setAdapter(adapter);
		 }
		 
		 @NonNull
		 @Override
		 public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
		 {
		 View itemView;
		 itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_line_element, viewGroup, false);
		 
		 return new CategoryViewHolder(itemView);
		 }
		 };
		 */
	}
}