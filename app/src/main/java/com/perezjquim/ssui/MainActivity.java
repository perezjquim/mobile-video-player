package com.perezjquim.ssui;

import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.content.*;
import android.content.res.*;
import android.view.Surface;
import android.hardware.*;
import com.perezjquim.*;
import java.lang.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    /***                         ***/
    /*** OVERRIDES ***/
    /***                         ***/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // inicialização
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        PermissionChecker.init(this);

        _handleSensors();
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
        _sensors.forEach((key,sensor) -> _sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _sensorManager.unregisterListener(this);
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
            case Sensor.TYPE_ACCELEROMETER:
                _handleAccelerometer(event);
                break;
        }
    }

    /***                                        ***/
    /*** IMPLEMENTAÇÕES ***/
    /***                                       ***/
    private SensorManager _sensorManager;
    private HashMap<String,Sensor> _sensors = new HashMap<>();
    private void _handleSensors()
    {
        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _sensors.put("accelerometer", _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private static final int DOWN_THRESHOLD = 9;
    private static final int UP_THRESHOLD = 4;
    private void _handleAccelerometer(SensorEvent event)
    {
        int orientation = _getOrientation();
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
//        System.out.println("===");
//        System.out.println("x: "+x);
//        System.out.println("y:" +y);
//        System.out.println("z: "+z);

        switch(orientation)
        {
            // PORTRAIT
            case ORIENTATION_PORTRAIT:
                if(isTilting(event))
                {
                    if (Math.abs(x) > Math.abs(y))
                    {
                        if (x < 0)
                        {
                            _handleTiltRight();
                        }
                        else if (x > 0)
                        {
                            _handleTiltLeft();
                        }
                    }
                    else
                    {
                        if (y < UP_THRESHOLD)
                        {
                            _handleTiltUp();
                        }
                        else if (y > DOWN_THRESHOLD)
                        {
                            _handleTiltDown();
                        }
                    }
                }
                break;

            // PORTRAIT (INVERSO)
            case ORIENTATION_PORTRAIT_REVERSE:
                if(isTilting(event))
                {
                    if (Math.abs(x) > Math.abs(y))
                    {
                        if (x < 0)
                        {
                            _handleTiltLeft();
                        }
                        else if (x > 0)
                        {
                            _handleTiltRight();
                        }
                    }
                    else
                    {
                        if (y < -DOWN_THRESHOLD)
                        {
                            _handleTiltDown();
                        }
                        else if (y > UP_THRESHOLD)
                        {
                            _handleTiltUp();
                        }
                    }
                }
                break;

            // LANDSCAPE
            case ORIENTATION_LANDSCAPE:
                if(isTilting(event))
                {
                    if (Math.abs(x) > Math.abs(y))
                    {
                        if (x < UP_THRESHOLD)
                        {
                            _handleTiltUp();
                        }
                        else if (x > DOWN_THRESHOLD)
                        {
                            _handleTiltDown();
                        }
                    }
                    else
                    {
                        if (y < 0)
                        {
                            _handleTiltLeft();
                        }
                        else if (y > 0)
                        {
                            _handleTiltRight();
                        }
                    }
                }
                break;

            // LANDSCAPE (INVERSO)
            case ORIENTATION_LANDSCAPE_REVERSE:
                if(isTilting(event))
                {
                    if (Math.abs(x) > Math.abs(y))
                    {
                        if (x < -DOWN_THRESHOLD)
                        {
                            _handleTiltDown();
                        }
                        else if (x > UP_THRESHOLD)
                        {
                            _handleTiltUp();
                        }
                    }
                    else
                    {
                        if (y < 0)
                        {
                            _handleTiltRight();
                        }
                        else if (y > 0)
                        {
                            _handleTiltLeft();
                        }
                    }
                }
                break;
        }
    }

    private void _handleTiltUp()
    {
        System.out.println(">> up");
    }
    private void _handleTiltDown()
    {
        System.out.println(">> down");
    }
    private void _handleTiltLeft()
    {
        System.out.println(">> left");
    }
    private void _handleTiltRight()
    {
        System.out.println(">> right");
    }

    private static final int TILTING_THRESHOLD = 2;
    private boolean isTilting(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        return !(
                x > (-TILTING_THRESHOLD)
                &&
                x < (TILTING_THRESHOLD)
                &&
                y > (-TILTING_THRESHOLD)
                &&
                y < (TILTING_THRESHOLD));
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
