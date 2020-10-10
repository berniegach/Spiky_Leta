/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 6/15/20 10:34 AM
 */

package com.spikingacacia.leta.ui.main.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public NotificationsViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}