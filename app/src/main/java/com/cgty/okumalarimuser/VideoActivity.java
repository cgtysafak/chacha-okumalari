package com.cgty.okumalarimuser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
{
    private static final String TAG = "VideoActivity";
    //Ads...
    private AdView mAdView;
    //YouTube...
    YouTubePlayerView youTubePlayerView;
    public static String API_KEY = "AIzaSyAEDuOqYVoV7ezAKUkBPd4Js0vKBglIri0";
    String VIDEO_ID = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        
        //Ads...
        mAdView = findViewById(R.id.adView_video);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    
        youTubePlayerView = findViewById(R.id.player);
        youTubePlayerView.initialize(API_KEY,this);
        
        //Intent
        if (getIntent() != null)
        {
            VIDEO_ID = getIntent().getStringExtra("Link");
        }
    }
    
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
    {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        
        if (!b)
        {
            youTubePlayer.cueVideo(VIDEO_ID);
        }
    }
    
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
    {
        Toast.makeText(this, "An error occurred...", Toast.LENGTH_SHORT).show();
    }
    
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener()
    {
        @Override
        public void onLoading()
        {
        
        }
    
        @Override
        public void onLoaded(String s)
        {
        
        }
    
        @Override
        public void onAdStarted()
        {
        
        }
    
        @Override
        public void onVideoStarted()
        {
        
        }
    
        @Override
        public void onVideoEnded()
        {
        
        }
    
        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason)
        {
        
        }
    };
    
    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener()
    {
        @Override
        public void onPlaying()
        {
        
        }
    
        @Override
        public void onPaused()
        {
        
        }
    
        @Override
        public void onStopped()
        {
        
        }
    
        @Override
        public void onBuffering(boolean b)
        {
        
        }
    
        @Override
        public void onSeekTo(int i)
        {
        
        }
    };
}