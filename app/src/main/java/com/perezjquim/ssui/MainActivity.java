package com.perezjquim.ssui;

import android.content.Intent;
import android.content.res.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.*;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.os.Bundle;
import android.support.v7.app.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.support.v7.widget.Toolbar;
import android.widget.VideoView;
import com.perezjquim.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    private SensorHandler _sensorHandler;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private Toolbar mTopToolbar;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private int isFullScreen;
    private MediaController mediaController;
    private boolean canPerformActions = false;
    private boolean performedAction = false;     // METER A TRUE QUANDO EXECUTA UM GESTO PARA UMA AÇÃO, ASSIM NÃO CANCELA APÓS 5 SEGUNDOS
    private VideoView video;
    private AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // inicialização
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        PermissionChecker.init(this);
        _sensorHandler = new SensorHandler(this);
        _sensorHandler.handle();
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        videoView= (VideoView) findViewById(R.id.vdVw);
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
        if (id == R.id.action_folder)
        {
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
                selectedVideoUri = data.getData();
               startVideo(selectedVideoUri);
                break;
        }
    }

    public void startVideo(Uri uri){
        mediaController= new FullScreenMediaController(this,0);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    public void fullMinScreen(boolean full){
        if(full){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            ((FullScreenMediaController) mediaController).setIsFullScreen(1);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getSupportActionBar().show();
            ((FullScreenMediaController) mediaController).setIsFullScreen(0);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
      int type = event.sensor.getType();
      switch(type)
      {
          case Sensor.TYPE_PROXIMITY:
              if(event.values[0] <= 4){
                  toggleActions();
                  Log.d("teste","açoes on");
              }
              break;

          default:// outros sensores sem ser o de proximidade
              if(canPerformActions){
                  _sensorHandler.onSensorChanged(event);
                  Log.d("teste","outro sensor");
              }
              break;
      }
    }

    // proximity sensor triggered
    public void toggleActions(){
        if(!canPerformActions){
            canPerformActions = true;
            Log.d("teste","açoes on");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!performedAction){
                        canPerformActions = false;
                        Log.d("teste","açoes off");
                    }
                }
            }, 5000);
        }
    }

    public boolean CanPerformActions(){
        return canPerformActions;
    }

    // After executing a gesture
    public void performedAction(){
        performedAction = true;
        canPerformActions = false;
    }

    public void vidSomMenos(View view){
        performedAction();
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 2;
        if (vol < 0) {
            vol = 0;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);

    }

    public void vidSomMais(View view){
        performedAction();
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+2;
        if(vol > max){
            vol = max;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
    }

    public void vidRebobinar(View view){
        performedAction();
        video.seekTo(video.getCurrentPosition()-1000);
    }

    public void vidAvancar(View view){
        performedAction();
        video.seekTo(video.getCurrentPosition()+1000);
    }
}
