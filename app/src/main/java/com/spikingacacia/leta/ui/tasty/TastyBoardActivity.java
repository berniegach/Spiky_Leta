/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.tasty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.charts.TrafficGraphFragment;
import com.spikingacacia.leta.ui.database.TastyBoard;
import com.spikingacacia.leta.ui.main.MainActivity;

public class TastyBoardActivity extends AppCompatActivity implements
        TastyBoardFragment.OnListFragmentInteractionListener
{
    private ExtendedFloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasty_board);

        setTitle("Tasty Board");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewTastyBoard();
            }
        });
        //check if we have the waiter logged on
        if(LoginActivity.getServerAccount().getPersona()==2)
            fab.hide();
        Fragment fragment= TastyBoardFragment.newInstance(1);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.base,fragment,"list");
        transaction.commit();
    }
    private void addNewTastyBoard()
    {
        Intent intent=new Intent(TastyBoardActivity.this, AddTastyBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
/*
* implementation of TastyBoardFragment.java
 */
    @Override
    public void onTastyBoardItemClicked(TastyBoard tastyBoard)
    {
        TastyBoardOverviewBottomSheet.newInstance(tastyBoard).show(getSupportFragmentManager(), "dialog");
    }
}