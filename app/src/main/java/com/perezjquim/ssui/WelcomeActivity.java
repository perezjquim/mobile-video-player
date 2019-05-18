package com.perezjquim.ssui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.perezjquim.*;
import static com.perezjquim.UIHelper.*;
import static com.perezjquim.SharedPreferencesHelper.*;
import android.view.*;

public class WelcomeActivity extends GenericActivity
{
    private SharedPreferencesHelper _prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        _prefs = new SharedPreferencesHelper(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_GALLERY_VIDEO)
        {
                Uri u = data.getData();
                Intent i = new Intent(this,MainActivity.class);
                i.putExtra("uri", u.toString());
                startActivity(i);
        }
    }

    public void onOpenLocalFile(View v)
    {
        openIntent();
    }

    public void onOpenURL(View v)
    {
        openVideoFromUrl(true);
    }
}
