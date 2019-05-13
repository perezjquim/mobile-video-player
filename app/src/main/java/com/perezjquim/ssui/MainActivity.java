package com.perezjquim.ssui;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.MediaController;
import android.widget.VideoView;
import com.perezjquim.*;
import android.util.*;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorHandler _sensorHandler;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private Toolbar mTopToolbar;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private boolean isFullScreen;
    private FullScreenMediaController mediaController;
    private AudioManager audioManager;

    private static final int VIDEO_SEEK_MS = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        PermissionChecker.init(this);
        _sensorHandler = new SensorHandler(this);
        _sensorHandler.handle();
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        videoView= (VideoView) findViewById(R.id.vdVw);
        setSupportActionBar(mTopToolbar);
        isFullScreen=false;
        audioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        openIntent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _sensorHandler.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _sensorHandler.onPause();
    }

    public void openIntent(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_folder:
                    openIntent();
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            // tratamento das permissões
            case PermissionChecker.REQUEST_CODE:
                PermissionChecker.restart();
                break;

            case REQUEST_TAKE_GALLERY_VIDEO:
                Uri selectedVideoUri = data.getData();
                startVideo(selectedVideoUri);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        _sensorHandler.onSensorChanged(event);
    }

    public void vidSomMenos()
    {
            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 2;
            if (vol < 0)
            {
                vol = 0;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            System.out.println("@@ menos som @@");
    }

    public void vidSomMais(){
            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 2;
            if (vol > max)
            {
                vol = max;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
        System.out.println("@@ mais som @@");
    }

    public void vidRebobinar()
    {
            videoView.seekTo(videoView.getCurrentPosition() - VIDEO_SEEK_MS);
            System.out.println("@@ rebobinar @@");
    }

    public void vidAvancar()
    {
            videoView.seekTo(videoView.getCurrentPosition() + VIDEO_SEEK_MS);
            System.out.println("@@ avançar @@");
    }

    public void fullMinScreen(boolean full){
        if(full){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            mediaController.setIsFullScreen(true);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getSupportActionBar().show();
            mediaController.setIsFullScreen(false);
        }
    }

    public void startVideo(Uri uri){
        mediaController= new FullScreenMediaController(this,false);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }
}
