/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 6/27/20 9:10 AM
 */

package com.spikingacacia.leta.ui.waiters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;

public class WaitersActivity extends AppCompatActivity
{
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiters);
        //toolbar
        setTitle("Waiters");

        Fragment fragment= WaiterFragment.newInstance(2);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"waiters");
        transaction.commit();
    }
}
