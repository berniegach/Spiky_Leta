package com.spikingacacia.leta.ui.reports;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Revenue");
        //preference
        preferences=new Preferences(getBaseContext());
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        //set the first base fragment
        Fragment fragment=SRSoldRateF.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"");
        transaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.srreports_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.revenue:
            {
                setTitle("Revenue");
                Fragment fragment = SRSoldRateF.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.base, fragment, "");
                transaction.commit();
                return true;
            }

            case R.id.sold:
            {
                setTitle("Sold");
                Fragment fragment = SRSoldF.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.base, fragment, "");
                transaction.commit();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}