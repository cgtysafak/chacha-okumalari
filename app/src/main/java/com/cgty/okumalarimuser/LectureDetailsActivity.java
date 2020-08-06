package com.cgty.okumalarimuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cgty.okumalarimuser.model.Lecture;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;

public class LectureDetailsActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";
	//Ads...
	private AdView mAdView;
	//Content
	TextView lecture_name;
	TextView lecture_malz;
	TextView lecture_notes;
	TextView lecture_idToWatch;
	ImageView lecture_image;
	FButton btn_link;
	CollapsingToolbarLayout collapsingToolbarLayout;
	String lectureId = "";
	Lecture tiklananLecture;
	//Firebase
	FirebaseDatabase database;
	DatabaseReference lecturePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture_details);
		
		//Ads...
		mAdView = findViewById(R.id.adView_lecture_details);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		//Controls
		lecture_name = findViewById(R.id.txt_lecture_name);
		lecture_malz = findViewById(R.id.txt_malz);
		lecture_notes = findViewById(R.id.txt_notes);
		lecture_idToWatch = findViewById(R.id.txt_video_id);
		lecture_image = findViewById(R.id.lectureImageDetails);
		btn_link = findViewById(R.id.btn_link);
		collapsingToolbarLayout = findViewById(R.id.collapsing);
		// Initializing Firebase variables...
		database = FirebaseDatabase.getInstance();
		lecturePath = database.getReference("Lectures");
		
		collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
		collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
		
		btn_link.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Sending the video id when clicked...
				Intent videoId;
				videoId = new Intent(LectureDetailsActivity.this, VideoActivity.class);

				videoId.putExtra("Link", lecture_idToWatch.getText().toString());
				
				startActivity(videoId);
			}
		});
		
		//Intent
		if (getIntent() != null)
		{
			lectureId = getIntent().getStringExtra("LectureId");
		}
		
		if (!lectureId.isEmpty())
		{
			getDetails();
		}
	}
	
	private void getDetails()
	{
		lecturePath.child(lectureId).addValueEventListener(new ValueEventListener()
		{
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot)
			{
				tiklananLecture = snapshot.getValue(Lecture.class);
				
				//Transferring the values...
				Picasso.with(getBaseContext()).load(tiklananLecture.getImage()).into(lecture_image);
				collapsingToolbarLayout.setTitle(tiklananLecture.getLectureName());
				lecture_name.setText(tiklananLecture.getLectureName());
				lecture_malz.setText(tiklananLecture.getMalz());
				lecture_notes.setText(tiklananLecture.getNotes());
				lecture_idToWatch.setText(tiklananLecture.getLinkToWatch());
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError error)
			{
			
			}
		});
	}
}