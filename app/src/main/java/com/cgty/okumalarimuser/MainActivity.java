package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cgty.okumalarimuser.interfaces.ItemClickListener;
import com.cgty.okumalarimuser.model.Category;
import com.cgty.okumalarimuser.viewholder.CategoryViewHolder;
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

/**
 * Main Activity class.
 * @author Çağatay Safak
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
{
	private long backPressedTime;
	private Toast backToast;
	ImageButton buttonHelp;
	// Firebase...
	FirebaseDatabase database;
	DatabaseReference categoryPath;
	FirebaseStorage storage;
	StorageReference imagePath;
	FirebaseRecyclerAdapter<Category,CategoryViewHolder> adapter;
	// RecyclerView...
	RecyclerView recycler_category;
	RecyclerView.LayoutManager layoutManager;
	// Model...
	Category newCategory;
	// Searching...
	FirebaseRecyclerAdapter<Category,CategoryViewHolder> searchAdapter;
	List<String> suggestionList = new ArrayList<>();
	MaterialSearchBar materialSearchBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buttonHelp = findViewById(R.id.buttonHelp);
		
		// Initializing Firebase variables...
		database = FirebaseDatabase.getInstance();
		categoryPath = database.getReference("Category");
		storage = FirebaseStorage.getInstance();
		imagePath = storage.getReference();
		
		// Initializing Recycler variables...
		recycler_category = findViewById(R.id.recycler_category);
		recycler_category.setHasFixedSize(true);
		layoutManager = new GridLayoutManager(this,2);
		recycler_category.setLayoutManager(layoutManager);
		
		uploadCategory();
		
		// Initializing Search variables...
		materialSearchBar = findViewById(R.id.searchBar);
		materialSearchBar.setHint("Search Categories....");
		
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
					recycler_category.setAdapter(adapter);
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
		
		buttonHelp.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent credits;
				
				credits = new Intent( MainActivity.this, CreditsActivity.class);
				
				startActivity(credits);
			}
		});
	}
	
	@Override
	public void onBackPressed()
	{
		if (backPressedTime + 2000 > System.currentTimeMillis())
		{
			backToast.cancel();
			super.onBackPressed();
			
			return;
		}
		else
		{
			backToast = Toast.makeText(getBaseContext(), "Press back again to exit.", Toast.LENGTH_SHORT);
			backToast.show();
		}
		
		backPressedTime = System.currentTimeMillis();
	}
	
	//deneme
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
		
		}
		
		return super.onKeyDown(keyCode, event);
	}*/
	
	private void startSearching( CharSequence text)
	{
		//Starting the filtering.
		Query searchQueryByName;
		searchQueryByName = categoryPath.orderByChild("name").equalTo(text.toString());
		
		//Creating options by the query.
		FirebaseRecyclerOptions<Category> categoryOptions;
		categoryOptions = new FirebaseRecyclerOptions.Builder<Category>().setQuery(searchQueryByName, Category.class).build();
		
		searchAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(categoryOptions)
		{
			@Override
			protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Category category)
			{
				categoryViewHolder.txtCategoryName.setText(category.getName());
				Picasso.with(getBaseContext()).load(category.getImage()).into(categoryViewHolder.imageView);
				
				Category local = category;
				
				categoryViewHolder.setItemClickListener(new ItemClickListener()
				{
					@Override
					public void onClick(View view, int position, boolean isLongClick)
					{
						//Her bir satira tiklandiginda ne yapsin...
//						Toast.makeText(MainActivity.this, "Category ID: " + adapter.getRef(position).getKey() + " Category Name: " + tiklandiginda.getName(), Toast.LENGTH_SHORT).show();
						Intent sections;
						sections = new Intent( MainActivity.this, SectionsActivity.class);
						
						sections.putExtra("CategoryId", searchAdapter.getRef(position).getKey());
						
						startActivity(sections);
					}
				});
			}
			
			@NonNull
			@Override
			public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
			{
				View itemView;
				itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_line_element, parent,false);
				
				return new CategoryViewHolder(itemView);
			}
		};
		
		searchAdapter.startListening();
		recycler_category.setAdapter(searchAdapter);  //Transferring the results to the recycler.
	}
	
	private void showSuggestions()
	{
		categoryPath.orderByChild("name").addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot)
			{
				for (DataSnapshot postSnapshot : snapshot.getChildren()) {
					Category item;
					item = postSnapshot.getValue(Category.class);
					
					//assert item != null;
					suggestionList.add(item.getName()); //Adding the names of desired categories.
				}
				
				materialSearchBar.setLastSuggestions(suggestionList);
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error)
			{
			
			}
		});
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