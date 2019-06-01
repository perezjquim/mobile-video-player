package com.perezjquim.ssui;

import android.content.*;
import android.content.res.*;
import android.hardware.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.MediaController;
import android.widget.VideoView;
import com.perezjquim.*;
import android.util.*;
import android.database.*;
import android.provider.*;
import java.util.*;
import java.net.*;
import org.json.*;
import java.io.*;
import static com.perezjquim.UIHelper.*;
import static com.perezjquim.SharedPreferencesHelper.*;
import android.provider.*;

public class MainActivity extends GenericActivity implements SensorEventListener
{
    private SensorHandler _sensorHandler;
    private Toolbar mTopToolbar;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private boolean isFullScreen;
    private FullScreenMediaController mediaController;
    private AudioManager audioManager;
    private SharedPreferencesHelper _prefs;

    private static final int VIDEO_SEEK_MS = 1000;
    private static final int VOLUME_CHANGE = 1;
    private static final int VIBRATION_TIME = 55;
    private static final int MAX_LIST_ITEMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _sensorHandler = new SensorHandler(this);
        _sensorHandler.handle();
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        videoView= (VideoView) findViewById(R.id.vdVw);
        setSupportActionBar(mTopToolbar);
        isFullScreen=false;
        audioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
        _prefs = new SharedPreferencesHelper(this);
        _handleIntent();
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
                openVideoFromUrl(false);
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
            int vol = _getVolume() - VOLUME_CHANGE;
            if (vol < 0)
            {
                vol = 0;
            }
            _setVolume(vol);
            System.out.println("@@ menos som @@");
            _vibrate();
    }

    public void vidSomMais()
    {
            int max = _getMaxVolume();
            int vol = _getVolume() + VOLUME_CHANGE;
            if (vol > max)
            {
                vol = max;
            }
            _setVolume(vol);
            System.out.println("@@ mais som @@");
            _vibrate();
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
        int position = videoView.getCurrentPosition();
        if (position - VIDEO_SEEK_MS >= 0)
        {
            videoView.seekTo(position - VIDEO_SEEK_MS);
        }
        System.out.println("@@ rebobinar @@");
        _vibrate();
    }

    public void vidAvancar()
    {
        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        if (position + VIDEO_SEEK_MS*2 <= duration)
        {
            videoView.seekTo(position + VIDEO_SEEK_MS*2);
        }
        System.out.println("@@ avançar @@");
        _vibrate();
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
        openProgressDialog(this, "Loading...");
        _saveHistory(uri);
        mediaController= new FullScreenMediaController(this,false);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        MainActivity me = this;
        videoView.setOnErrorListener((a,b,c)->
        {
            closeProgressDialog(me);
            return false;
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                closeProgressDialog(me);
                videoView.start();
            }
        });
    }


    private void _vibrate()
    {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26)
        {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else
        {
            vibrator.vibrate(VIBRATION_TIME);
        }
    }

    private void _saveHistory(Uri uri)
    {
        String sData = _prefs.getString(CONFIG_PREFS_KEY,HISTORY_PREFS_KEY);

        String name = _getFileName(uri);

        String scheme = uri.getScheme();
        String path = "";
        switch(scheme)
        {
            case ContentResolver.SCHEME_CONTENT:
                path = _getFilePath(uri);
                if(path.equals(""))
                {
                    toast(this,"Error while opening video!");
                    return;
                }
                break;

            case "http":
            case "https":
                path = uri.toString();
                break;

            case "file":
                path = uri.getPath();
                break;
        }

        JSONObject oData = new JSONObject();
        JSONArray aData = new JSONArray();
        try
        {
            oData.put("name", name);
            oData.put("path", path);
            if(sData != null)
            {
                aData = new JSONArray(sData);
                int length = aData.length();
                JSONObject last = aData.getJSONObject(length-1);
                String lastPath = last.getString("path");
                if(!path.equals(lastPath))
                {
                    aData.put(oData);
                }
                if(length >= MAX_LIST_ITEMS)
                {
                    int limit = aData.length() - MAX_LIST_ITEMS;
                    for(int i = 0; i < limit ; i++)
                        aData.remove(0);
                }
            }
            else
            {
                aData.put(oData);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        String sResult = aData.toString();
        _prefs.setString(CONFIG_PREFS_KEY,HISTORY_PREFS_KEY,sResult);
    }

    private String _getFileName(Uri uri)
    {
            Cursor c = getContentResolver().query(uri, null, null, null, null);
            if(c != null)
            {
                int index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                c.moveToFirst();
                String name = c.getString(index);
                c.close();
                return name;
            }
            else
            {
                return uri.toString();
            }
    }

    private void _handleIntent()
    {
        Intent i = getIntent();

        String p = i.getStringExtra("path");
        if(p != null)
        {
            Uri u = Uri.fromFile(new File(p));
            startVideo(u);
            return;
        }

        String u = i.getStringExtra("uri");
        if(u != null)
        {
            Uri uri = Uri.parse(u);
            startVideo(uri);
        }
    }

    private String _getFilePath(Uri uri)
    {
        if(uri.toString().contains("content://com.android.providers.downloads.documents/document/raw"))
        {
            return uri.getPath().replace("/document/raw:","");
        }
        else
        {

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

            if (cursor.isNull(index))
            {
                return "";
            }

            String path = cursor.getString(index);
            cursor.close();

            return path;
        }
    }
}
