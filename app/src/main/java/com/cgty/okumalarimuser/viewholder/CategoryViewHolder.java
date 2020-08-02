package com.cgty.okumalarimuser.viewholder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cgty.okumalarimuser.R;
import com.cgty.okumalarimuser.interfaces.ItemClickListener;

public class CategoryViewHolder extends RecyclerView.ViewHolder
		implements View.OnClickListener, View.OnCreateContextMenuListener
{
	public TextView txtCategoryName;
	public ImageView imageView;
	
	private ItemClickListener itemClickListener;
	
	public CategoryViewHolder(@NonNull View itemView)
	{
		super(itemView);
		
		txtCategoryName = itemView.findViewById(R.id.textView_categoryName_categoryLineElement);
		imageView = itemView.findViewById(R.id.imageView_categoryImage_categoryLineElement);
		
		itemView.setOnClickListener(this);
		itemView.setOnCreateContextMenuListener(this);
	}
	
	public void setItemClickListener(ItemClickListener itemClickListener)
	{
		this.itemClickListener = itemClickListener;
	}
	
	@Override
	public void onClick(View v)
	{
		itemClickListener.onClick( v, getAdapterPosition(), false);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle( "Choose an action.");
		
		menu.add(0,0,getAdapterPosition(),"Update");
		menu.add(0,1,getAdapterPosition(),"Delete");
	}
}