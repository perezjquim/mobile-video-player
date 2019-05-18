package com.perezjquim.ssui;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.MediaController;
import android.widget.VideoView;
import com.perezjquim.*;
import android.util.*;

import java.util.Timer;
import java.util.TimerTask;

import static com.perezjquim.UIHelper.askString;
import static com.perezjquim.UIHelper.toast;

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
    private static final int VOLUME_CHANGE = 2;

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
        //openIntent();
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

            case R.id.paste_url:
                openVideoFromUrl();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openVideoFromUrl()
    {
        askString(this,"Introduza o URL do vídeo:","",(url)->
        {
            String urlString = url.toString();
            //
             if (URLUtil.isValidUrl(urlString)) {
                Uri uri = Uri.parse(urlString);
                startVideo(uri);
            }
            //toast(this,"Inserted phrase:"+url);
        });
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

    public void openIntent(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void vidSomMenos()
    {
            int vol = _getVolume() - VOLUME_CHANGE;
            if (vol < 0)
            {
                vol = 0;
            }
            _setVolume(vol);
            System.out.println("@@ menos som @@");
    }

    public void vidSomMais(){
            int max = _getMaxVolume();
            int vol = _getVolume() + VOLUME_CHANGE;
            if (vol > max)
            {
                vol = max;
            }
            _setVolume(vol);
        System.out.println("@@ mais som @@");
    }

    private int _getMaxVolume()
    {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private int _getVolume()
    {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void _setVolume(int vol)
    {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
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
        Window w = this.getWindow();
        ActionBar b = this.getSupportActionBar();
        if(full){
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            b.hide();
            mediaController.setIsFullScreen(true);
        }else{
            w.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            b.show();
            mediaController.setIsFullScreen(false);
        }
    }

    public void startVideo(Uri uri){
        mediaController= new FullScreenMediaController(this,false);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                toast(MainActivity.this,"ABCD");
                videoView.start();
            }
        });
        //videoView.start();
    }
}
