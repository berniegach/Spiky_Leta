package com.spikingacacia.leta.ui.reports;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SRReportsSPA extends FragmentPagerAdapter
{
    private static final String[] TAB_TITLES = new String[]{"Revenue","sold"};
    //private static final String[] TAB_TITLES = new String[]{"Revenue","sold","Items Selling Rate"};
    private final Context mContext;

    public SRReportsSPA(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position==0)
            return SRSoldRateF.newInstance();
        else if(position==1)
            return SRSoldF.newInstance();
        else
            return SRCountRateF.newInstance();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount()
    {
        // Show 2 total pages.
        return 2;
    }
}