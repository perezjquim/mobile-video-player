package com.perezjquim.ssui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.graphics.*;

public class FullScreenMediaController extends MediaController
{
    private ImageButton fullScreen;
    private MainActivity context;
    private boolean isFullScreen;
    private static final int FULLSCREEN_BUTTON_RIGHT_MARGIN = 80;

    public FullScreenMediaController(MainActivity context, boolean isFullScreen)
    {
        super(context);
        this.context=context;
        this.isFullScreen=isFullScreen;
    }

    @Override
    public void setAnchorView(View view)
    {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton(context);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = FULLSCREEN_BUTTON_RIGHT_MARGIN;
        addView(fullScreen, params);

        fullScreen.setImageResource(R.drawable.ic_fullscreen);
        fullScreen.setColorFilter(getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);
        fullScreen.setBackground(null);

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isFullScreen){
                    fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
                    context.fullMinScreen(true);
                }else {
                    fullScreen.setImageResource(R.drawable.ic_fullscreen);
                    context.fullMinScreen(false);
                }
            }
        });
    }

    public void setIsFullScreen(boolean isFullScreen)
    {
        this.isFullScreen=isFullScreen;
    }
}

// test
