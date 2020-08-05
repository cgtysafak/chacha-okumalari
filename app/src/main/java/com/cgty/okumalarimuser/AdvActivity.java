package com.cgty.okumalarimuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.dynamic.IFragmentWrapper;

public class AdvActivity extends AppCompatActivity implements RewardedVideoAdListener
{
	private RewardedVideoAd mRewardedVideoAd;
	Button btn_ad;
	String LectureId;
	int counter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adv);
		
		counter = 0;
		
		//getting Intent
		if (getIntent() != null)
		{
			LectureId = getIntent().getStringExtra("LectureId");
		}
		
		MobileAds.initialize(this, "ca-app-pub-8426610305622878~8001426796");  //uygulama kimligi
		// Use an activity context to get the rewarded video instance.
		mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
		mRewardedVideoAd.setRewardedVideoAdListener(this);
		//mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().addTestDevice("2E5C1BFE36D4707CC54D028594C19D17").build());
		
		loadRewardedVideoAd();
		
		btn_ad = findViewById(R.id.btn_ad);
		
		btn_ad.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				counter++;
				
				//loading ads...
				if (mRewardedVideoAd.isLoaded())
				{
					mRewardedVideoAd.show();
				}
				else if ( counter < 4)
				{
					//Toast.makeText(AdvActivity.this, "Please try again. This is your (" + counter + "). attempt.", Toast.LENGTH_SHORT).show();
					counter++;
					counter--;
				}
				else
				{
					Toast.makeText(AdvActivity.this, "No available advertisement.", Toast.LENGTH_SHORT).show();
					
					Intent lectureDetails;
					lectureDetails = new Intent(AdvActivity.this, LectureDetailsActivity.class);
					
					lectureDetails.putExtra("LectureId", LectureId);
					
					startActivity(lectureDetails);
					
					finish();  //this destroys the AdvActivity after getting the reward.
				}
			}
		});
	}
	
	private void loadRewardedVideoAd()
	{
		mRewardedVideoAd.loadAd("ca-app-pub-8426610305622878/5902819019", new AdRequest.Builder().build());  //reklam birimi kimligi
	}
	
	@Override
	public void onRewardedVideoAdLoaded()
	{
	
	}
	
	@Override
	public void onRewardedVideoAdOpened()
	{
	
	}
	
	@Override
	public void onRewardedVideoStarted()
	{
	
	}
	
	@Override
	public void onRewardedVideoAdClosed()
	{
		counter = 0;
	}
	
	@Override
	public void onRewarded(RewardItem rewardItem)
	{
		// When the ad ends
		Intent lectureDetails;
		lectureDetails = new Intent(AdvActivity.this, LectureDetailsActivity.class);
		
		lectureDetails.putExtra("LectureId", LectureId);
		
		startActivity(lectureDetails);
		
		finish();  //this destroys the AdvActivity after getting the reward.
	}
	
	@Override
	public void onRewardedVideoAdLeftApplication()
	{
	
	}
	
	@Override
	public void onRewardedVideoAdFailedToLoad(int i)
	{
		// I might add some code right here.
	}
	
	@Override
	public void onRewardedVideoCompleted()
	{
	
	}
	
	@Override
	protected void onResume()
	{
		mRewardedVideoAd.resume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		mRewardedVideoAd.pause(this);
		super.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		mRewardedVideoAd.destroy(this);
		super.onDestroy();
	}
}