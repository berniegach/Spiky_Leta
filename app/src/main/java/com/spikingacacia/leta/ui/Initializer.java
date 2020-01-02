package com.spikingacacia.leta.ui;

import android.app.Application;

import com.spikingacacia.leta.BuildConfig;

import net.gotev.uploadservice.UploadService;

/**
 * Created by $USER_NAME on 5/23/2018.
 * initialize gotev subclass
 **/
public class Initializer extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        //setup the boradcast action namespace string which will be used totify upload status
        UploadService.NAMESPACE= BuildConfig.APPLICATION_ID;
    }
}
