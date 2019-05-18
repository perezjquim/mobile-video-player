package com.perezjquim.ssui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.perezjquim.*;
import static com.perezjquim.UIHelper.*;
import static com.perezjquim.SharedPreferencesHelper.*;
import android.view.*;
import org.json.*;
import java.util.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;

public class WelcomeActivity extends GenericActivity
{
    private SharedPreferencesHelper _prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        _prefs = new SharedPreferencesHelper(this);
        _listHistory();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        _listHistory();
    }

    public void onOpenLocalFile(View v)
    {
        openIntent();
    }

    public void onOpenURL(View v)
    {
        openVideoFromUrl(true);
    }

    private void _listHistory()
    {
        String sData = _prefs.getString(CONFIG_PREFS_KEY,HISTORY_PREFS_KEY);
        if(sData == null) return;
        try
        {
            JSONArray aData = new JSONArray(sData);
            int length = aData.length();
            if(length > 0)
            {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();
                for(int i = 0; i < length; i++)
                {
                    JSONObject o = aData.getJSONObject(i);
                    String name = o.getString("name");
                    String path = o.getString("path");
                    names.add(name);
                    paths.add(path);
                }

                ListView list = findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.template_listitem, names);
                list.setAdapter(adapter);

                WelcomeActivity me = this;
                list.setOnItemClickListener(new OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        String path = paths.get(position);
                        Intent i = new Intent(me,MainActivity.class);
                        if(path != null && (path.contains("http://") || path.contains("https://")))
                        {
                            i.putExtra("uri", path);
                        }
                        else
                        {
                            i.putExtra("path", path);
                        }
                        startActivity(i);
                    }
                });
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
}
