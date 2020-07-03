package com.spikingacacia.leta.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.charts.OrdersGraphFragment;
import com.spikingacacia.leta.ui.charts.PerformanceGraphFragment;
import com.spikingacacia.leta.ui.charts.TrafficGraphFragment;


public class HostActivity extends AppCompatActivity
{
    public static String ARG_WHICH_FRAGMENT = "which_fragment";
    public static String ARG_STORE_INVENTORY = "store_inventory";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        int which_fragment = getIntent().getIntExtra(ARG_WHICH_FRAGMENT, 1);

        if( which_fragment == 3)
        {
            setTitle("Dashboard");
            Fragment fragment= PerformanceGraphFragment.newInstance("","");
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.base,fragment,"performance_graph");
            transaction.commit();
        }
        else if( which_fragment == 4)
        {
            setTitle("Dashboard");
            Fragment fragment= OrdersGraphFragment.newInstance();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.base,fragment,"orders_graph");
            transaction.commit();
        }
        else if( which_fragment == 5)
        {
            setTitle("Dashboard");
            Fragment fragment= TrafficGraphFragment.newInstance();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.base,fragment,"traffic_graph");
            transaction.commit();
        }
    }

}