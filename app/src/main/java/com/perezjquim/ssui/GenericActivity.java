package com.perezjquim.ssui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.URLUtil;
import com.perezjquim.*;
import static com.perezjquim.UIHelper.*;
import static com.perezjquim.SharedPreferencesHelper.*;

public class GenericActivity extends AppCompatActivity
{
    protected static final int REQUEST_TAKE_GALLERY_VIDEO = 2;
    protected static final String CONFIG_PREFS_KEY = "config";
    protected static final String HISTORY_PREFS_KEY = "history";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        PermissionChecker.init(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            // tratamento das permissões
            case PermissionChecker.REQUEST_CODE:
                PermissionChecker.restart();
                break;
        }
    }

    protected void openIntent()
    {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    protected void openVideoFromUrl(boolean newActivity)
    {
        GenericActivity me = this;
        askString(this,"Introduza o URL do vídeo:","",(url)->
        {
            String urlString = url.toString();
            if (URLUtil.isValidUrl(urlString))
            {
                Uri uri = Uri.parse(urlString);
                if(newActivity)
                {
                    Intent i = new Intent(me,MainActivity.class);
                    i.putExtra("uri", uri.toString());
                    startActivity(i);
                }
                else
                {
                    ((MainActivity)me).startVideo(uri);
                }
            }
        });
    }
}
