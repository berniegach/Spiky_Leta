/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.spikingacacia.leta.R;

/**
 * Created by $USER_NAME on 10/13/2018.
 **/
public class ProgressView extends Service
{
    private WindowManager mWindowManager;
    private ProgressBar mProgressBar;
    public void onCreate()
    {
        super.onCreate();
        mProgressBar=new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
        mProgressBar.setIndeterminate(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setBackgroundResource(R.mipmap.ic_launcher);
        mWindowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            params=new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity= Gravity.TOP;
            params.x=0;
            params.y=100;
        }
        else
        {
            params=new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity= Gravity.TOP;
            params.x=0;
            params.y=100;
        }

        mWindowManager.addView(mProgressBar,params);
        try
        {
            mProgressBar.setOnTouchListener(new View.OnTouchListener()
            {
                WindowManager.LayoutParams layoutParams=params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            initialX=layoutParams.x;
                            initialY=layoutParams.y;
                            initialTouchX=event.getRawX();
                            initialTouchY=event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            layoutParams.x=initialX+(int)(event.getRawX()-initialTouchX);
                            layoutParams.y=initialY+(int)(event.getRawY()-initialTouchY);
                            mWindowManager.updateViewLayout(v,layoutParams);
                            break;
                    }
                    return false;
                }
            });
        }
        catch (Exception e)
        {
            Log.e("ProgressView",""+e.getMessage());
        }
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mProgressBar!=null)
            mWindowManager.removeView(mProgressBar);
    }
}
