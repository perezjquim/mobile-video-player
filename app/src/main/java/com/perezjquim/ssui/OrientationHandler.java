package com.perezjquim.ssui;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;

public class OrientationHandler
{
    public static final int         ORIENTATION_PORTRAIT = 0,
                                            ORIENTATION_LANDSCAPE = 1,
                                            ORIENTATION_PORTRAIT_REVERSE = 2,
                                            ORIENTATION_LANDSCAPE_REVERSE = 3;
    private MainActivity _act;

    public OrientationHandler(MainActivity act)
    {
        _act = act;
    }

    public int getCurrentOrientation()
    {
        int orientation = _act.getResources().getConfiguration().orientation;
        int rotation = _act.getWindowManager().getDefaultDisplay().getRotation();

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
