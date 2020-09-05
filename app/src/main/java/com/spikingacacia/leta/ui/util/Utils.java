package com.spikingacacia.leta.ui.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.util.Currency;
import java.util.Locale;

public class Utils
{
    public Utils()
    {}
    //to retrieve currency code
    public static String getCurrencyCode(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
    }
    //to retrieve currency symbol
    public static String getCurrencySymbol(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getSymbol();
    }
    /**
     * This method expands a view from a height of 0 to the set height as it transitions to been visible
     * @param v The view that needs expanding*/
    public static void expand(final View v)
    {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight=v.getMeasuredHeight();
        //older versions of android  (pre api 21) cancel animations for view with a height of 0
        v.getLayoutParams().height=1;
        v.setVisibility(View.VISIBLE);
        Animation animation=new Animation()
        {
            @Override
            public boolean willChangeBounds()
            {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                v.getLayoutParams().height=interpolatedTime==1? LinearLayout.LayoutParams.WRAP_CONTENT:(int)(targetHeight*interpolatedTime);
                v.requestLayout();
            }
        };
        animation.setDuration((int)(targetHeight/v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(animation);
    }
    /**
     * This method collapses a view from its initial height to a height of 0 as it transitions for removal from parent layout
     * @param v The view that needs collapsing*/
    public static void collapse(final View v)
    {
        final int initialHeight=v.getMeasuredHeight();
        Animation animation=new Animation()
        {
            @Override
            public boolean willChangeBounds()
            {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                if(interpolatedTime==1)
                    v.setVisibility(View.GONE);
                else
                {
                    v.getLayoutParams().height=initialHeight-(int)(initialHeight*interpolatedTime);
                    v.requestLayout();
                }
            }
        };
        animation.setDuration((int)(initialHeight/v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(animation);
    }

}
