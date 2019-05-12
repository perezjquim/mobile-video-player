package com.perezjquim.ssui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.*;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.perezjquim.*;


public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private Toolbar mTopToolbar;
    private Uri selectedVideoUri;
    private VideoView videoView;
    private int isFullScreen;
    private MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /***********/
        /*** init ***/
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        //PermissionChecker.init(this);
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        videoView= (VideoView) findViewById(R.id.vdVw);
        setSupportActionBar(mTopToolbar);
        isFullScreen=0;
        openIntent();
    }

    public void openIntent(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_folder) {
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
            /*** permissões ***/
            /*********************/
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
}
