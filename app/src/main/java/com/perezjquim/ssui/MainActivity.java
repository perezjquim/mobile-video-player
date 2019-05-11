package com.perezjquim.ssui;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.MediaController;
import android.widget.VideoView;
import com.perezjquim.*;
import android.util.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorHandler _sensorHandler;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // inicialização
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        PermissionChecker.init(this);
        _sensorHandler = new SensorHandler(this);
        _sensorHandler.handle();
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
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

    private boolean _isFullscreen = false;
    public void toggleFullscreen(View v)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();

        if(!_isFullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getSupportActionBar().hide(); //hide the title bar
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getSupportActionBar().show();
        }

        _isFullscreen = !_isFullscreen;

        getWindow().setAttributes(attrs);
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
                VideoView videoView =(VideoView)findViewById(R.id.vdVw);
                MediaController mediaController= new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(selectedVideoUri);
                videoView.requestFocus();
                videoView.start();
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
}
