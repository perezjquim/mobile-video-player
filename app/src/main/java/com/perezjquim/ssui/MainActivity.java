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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _stopAction();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        _stopAction();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        _stopAction();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        _stopAction();
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
            OrientationDetector.OrientationListener l = new OrientationDetector.OrientationListener()
            {
                @Override
                public void onTopSideUp()
                {
                        System.out.println("== top side up");
                        switch (_getOrientation())
                        {
                            case ORIENTATION_LANDSCAPE:
                                _doAction(() -> _forward());
                                break;
                            case ORIENTATION_LANDSCAPE_REVERSE:
                                _doAction(() -> _backward());
                                break;
                            default:
                                _stopAction();
                                break;
                        }
                }

                @Override
                public void onBottomSideUp()
                {
                        System.out.println("== btm side up");
                        switch (_getOrientation())
                        {
                            case ORIENTATION_LANDSCAPE:
                                _doAction(() -> _backward());
                                break;
                            case ORIENTATION_LANDSCAPE_REVERSE:
                                _doAction(() -> _forward());
                                break;
                            default:
                                _stopAction();
                                break;
                        }
                }

                @Override
                public void onRightSideUp()
                {
                        System.out.println("== right side up");
                        switch (_getOrientation())
                        {
                            case ORIENTATION_PORTRAIT:
                                _doAction(() -> _backward());
                                break;
                            case ORIENTATION_PORTRAIT_REVERSE:
                                _doAction(() -> _forward());
                                break;
                            default:
                                _stopAction();
                                break;
                        }
                }

                @Override
                public void onLeftSideUp()
                {
                        System.out.println("== left side up");
                        switch (_getOrientation())
                        {
                            case ORIENTATION_PORTRAIT:
                                _doAction(() -> _forward());
                                break;
                            case ORIENTATION_PORTRAIT_REVERSE:
                                _doAction(() -> _backward());
                                break;
                            default:
                                _stopAction();
                                break;
                        }
                }
            };

            Sensey.getInstance().startOrientationDetection(l);
    }

    private static Thread tAction;
    private static final int SAMPLING_RATE = 500;
    private boolean running = false;
    private void _doAction(Runnable action)
    {
        new Thread(()->
        {
            _stopAction();

            tAction = new Thread(() ->
            {
                while (running)
                {
                    action.run();
                    try
                    {
                        Thread.sleep(SAMPLING_RATE);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            running = true;

            tAction.start();
        }).start();
    }

    private void _stopAction()
    {
        if(tAction != null)
        {
            running = false;
            try
            {
                tAction.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void _increaseVolume()
    {
        System.out.println("aumentar volume");
    }

    private void _decreaseVolume()
    {
        System.out.println("baixar volume");
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
