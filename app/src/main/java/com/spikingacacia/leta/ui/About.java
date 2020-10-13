/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import android.util.AttributeSet;

import com.spikingacacia.leta.R;

public class About extends Preference
{
    private Preferences preferences;
    private Context context;
    public About(Context context)
    {
        super(context);
        setLayoutResource(R.layout.about);
        this.context=context;
        preferences = new Preferences(context);
    }

    public About(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.about);
        this.context=context;
        preferences = new Preferences(context);
    }
    public About(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.about);
        this.context=context;
        preferences = new Preferences(context);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder view)
    {
        super.onBindViewHolder(view);
    }
}
