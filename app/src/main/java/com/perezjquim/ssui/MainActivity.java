package com.perezjquim.ssui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.perezjquim.UIHelper;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);

        UIHelper.toast(this,"test");
    }
}
