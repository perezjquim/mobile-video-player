package com.perezjquim.ssui;

import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.*;

import com.perezjquim.*;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /***********/
        /*** init ***/
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        PermissionChecker.init(this);
        /*** init ***/
        /***********/

        UIHelper.toast(this,"test");
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
            /********************/
            /********************/
        }
    }
}
