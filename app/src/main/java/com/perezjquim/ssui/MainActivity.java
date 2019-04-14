package com.perezjquim.ssui;

import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.*;
import android.content.res.*;
import android.view.Surface;

import com.perezjquim.*;
import com.github.nisrulz.sensey.*;
import java.lang.*;

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
        Sensey.getInstance().init(this);
        /*** init ***/
        /***********/

        _handleSensors();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Sensey.getInstance().stop();
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

    private void _handleSensors()
    {
        Context me = this;

        OrientationDetector.OrientationListener l = new OrientationDetector.OrientationListener()
        {
            @Override public void onTopSideUp()
            {
                System.out.println("===");
                switch(_getOrientation())
                {
                    case ORIENTATION_LANDSCAPE:
                        _forward();
                        break;
                    case ORIENTATION_LANDSCAPE_REVERSE:
                        _backward();
                        break;
                }
                System.out.println("===");
            }

            @Override public void onBottomSideUp()
            {
                System.out.println("===");
                switch(_getOrientation())
                {
                    case ORIENTATION_LANDSCAPE:
                         _backward();
                         break;
                    case ORIENTATION_LANDSCAPE_REVERSE:
                        _forward();
                        break;
                }
                System.out.println("===");
            }

            @Override public void onRightSideUp()
            {
                System.out.println("===");
                switch(_getOrientation())
                {
                    case ORIENTATION_PORTRAIT:
                        _backward();
                        break;
                    case ORIENTATION_PORTRAIT_REVERSE:
                        _forward();
                        break;
                }
                System.out.println("===");
            }

            @Override public void onLeftSideUp()
            {
                System.out.println("===");
                switch(_getOrientation())
                {
                    case ORIENTATION_PORTRAIT:
                        _forward();
                        break;
                    case ORIENTATION_PORTRAIT_REVERSE:
                        _backward();
                        break;
                }
                System.out.println("===");
            }
        };
        Sensey.getInstance().startOrientationDetection(l);
    }

    private void _forward()
    {
        System.out.println("avançar");
    }

    private void _backward()
    {
        System.out.println("rebobinar");
    }

    private static final int    ORIENTATION_PORTRAIT = 0,
                                            ORIENTATION_LANDSCAPE = 1,
                                            ORIENTATION_PORTRAIT_REVERSE = 2,
                                            ORIENTATION_LANDSCAPE_REVERSE = 3;
    private int _getOrientation()
    {
        int orientation = this.getResources().getConfiguration().orientation;
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();

        if(orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
            {
                return ORIENTATION_LANDSCAPE;
            }
            else
            {
                return ORIENTATION_LANDSCAPE_REVERSE;
            }
        }
        else
        {
            if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
            {
                return ORIENTATION_PORTRAIT;
            }
            else
            {
                return ORIENTATION_PORTRAIT_REVERSE;
            }
        }
    }
}
