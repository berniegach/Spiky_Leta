package com.spikingacacia.leta.ui.reports;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;

public class SRReportsA extends AppCompatActivity
{
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_srreports);
        //preference
        preferences=new Preferences(getBaseContext());
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        SRReportsSPA sectionsPagerAdapter = new SRReportsSPA(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }
}