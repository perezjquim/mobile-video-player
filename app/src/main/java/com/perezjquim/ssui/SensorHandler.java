package com.perezjquim.ssui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.perezjquim.ssui.*;

import java.util.HashMap;

public class SensorHandler
{
    private HashMap<String,Sensor> _sensors = new HashMap<>();
    private SensorManager _sensorManager;
    private OrientationHandler _orientationHandler;
    private MainActivity _act;
    private static final int DOWN_THRESHOLD = 8;
    private static final int UP_THRESHOLD = 2;


    public SensorHandler(MainActivity act)
    {
        _act = act;
        _orientationHandler = new OrientationHandler(act);
    }

    public void handle()
    {
        _sensorManager = (SensorManager) _act.getSystemService(Context.SENSOR_SERVICE);
        _sensors.put("accelerometer", _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        _sensors.put("proximity", _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

    public void onPause()
    {
        _sensorManager.unregisterListener(_act);
    }

    public void onResume()
    {
        _sensors.forEach((key,sensor) -> _sensorManager.registerListener(_act, sensor, SensorManager.SENSOR_DELAY_NORMAL));
    }

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

    private void _handleAccelerometer(SensorEvent event)
    {
        int orientation = _orientationHandler.getCurrentOrientation();
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
            case OrientationHandler.ORIENTATION_PORTRAIT:
                if(_isTilting(event))
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
            case OrientationHandler.ORIENTATION_PORTRAIT_REVERSE:
                if(_isTilting(event))
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
            case OrientationHandler.ORIENTATION_LANDSCAPE:
                if(_isTilting(event))
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
            case OrientationHandler.ORIENTATION_LANDSCAPE_REVERSE:
                if(_isTilting(event))
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
            _act.performedAction();
            _act.vidRebobinar(null);
        }
        private void _handleTiltDown()
        {
            System.out.println(">> down");
            _act.performedAction();
            _act.vidAvancar(null);
        }
        private void _handleTiltLeft()
        {
            System.out.println(">> left");
            _act.performedAction();
            _act.vidSomMenos(null);
        }
        private void _handleTiltRight()
        {
            System.out.println(">> right");
            _act.performedAction();
            _act.vidSomMais(null);
        }

    private static final int TILTING_THRESHOLD = 2;
    private boolean _isTilting(SensorEvent event)
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
}
