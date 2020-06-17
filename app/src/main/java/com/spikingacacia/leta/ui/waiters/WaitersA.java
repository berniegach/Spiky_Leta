package com.spikingacacia.leta.ui.waiters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;

public class WaitersA extends AppCompatActivity
{
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_waiters);
        //preference
        preferences=new Preferences(getBaseContext());
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Waiters");

        Fragment fragment=WaiterF.newInstance(2);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"waiters");
        transaction.commit();
    }
}
