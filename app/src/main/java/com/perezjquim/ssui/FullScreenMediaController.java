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

public class FullScreenMediaController extends MediaController {
    private ImageButton fullScreen;
    private MainActivity context;
    private int isFullScreen;
    public FullScreenMediaController(MainActivity context,int isFullScreen) {
        super(context);
        this.context=context;
        this.isFullScreen=isFullScreen;
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        fullScreen.setImageResource(R.drawable.ic_fullscreen);

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFullScreen == 0){
                    fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
                    context.fullMinScreen(true);
                }else if(isFullScreen == 1){
                    fullScreen.setImageResource(R.drawable.ic_fullscreen);
                    context.fullMinScreen(false);
                }
            }
        });
    }

    public void setIsFullScreen(int isFullScreen)
    {
        this.isFullScreen=isFullScreen;
    }
}
