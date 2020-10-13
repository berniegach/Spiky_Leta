/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Created by $USER_NAME on 1/17/2019.
 **/
public class CommonHelper
{
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * @param dp A value in dp(density indipendent pixels) unit. which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     * */
    public static float convertDpToPixels(float dp, Context context)
    {
        return dp *((float)context.getResources().getDisplayMetrics().densityDpi/DisplayMetrics.DENSITY_DEFAULT);
    }
    /**
     * This method converts device specific pixels to density specific pixels.
     * @param px A value in px(pixels) unit. which we need to convert into dp.
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     * */
    public static float convertPixelsToDp(float px, Context context)
    {
        return px/((float)context.getResources().getDisplayMetrics().densityDpi/DisplayMetrics.DENSITY_DEFAULT);
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
    /**
     * This function set a view's vector icon
     * @param context context to get resources from
     * @param view the autocompleteview which needs an icon set
     * @param drawableLeft the resource id of the drawable to be set on the left of the view
     * @param drawableTop the resource id of the drawable to be set on the top of the view
     * @param drawableRight the resource id of the drawable to be set on the right of the view
     * @param  drawableBottom the resource id of the drawable to be set on the bottom of the view*/
    public static void setVectorDrawable(Context context, AutoCompleteTextView view, int drawableLeft, int drawableTop, int drawableRight, int drawableBottom)
    {
          if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable drawable_left=drawableLeft!=0? AppCompatResources.getDrawable(context,drawableLeft):null;
            Drawable drawable_top=drawableTop!=0? AppCompatResources.getDrawable(context,drawableTop):null;
            Drawable drawable_right=drawableRight!=0? AppCompatResources.getDrawable(context,drawableRight):null;
            Drawable drawable_bottom=drawableBottom!=0? AppCompatResources.getDrawable(context,drawableBottom):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
        else
        {
            Drawable drawable_left=drawableLeft!=0? VectorDrawableCompat.create(context.getResources(),drawableLeft,null):null;
            Drawable drawable_top=drawableTop!=0? VectorDrawableCompat.create(context.getResources(),drawableTop,null):null;
            Drawable drawable_right=drawableRight!=0? VectorDrawableCompat.create(context.getResources(),drawableRight,null):null;
            Drawable drawable_bottom=drawableBottom!=0? VectorDrawableCompat.create(context.getResources(),drawableBottom,null):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
    }
    /**
     * This function set a view's vector icon
     * @param context context to get resources from
     * @param view the edittextview which needs an icon set
     * @param drawableLeft the resource id of the drawable to be set on the left of the view
     * @param drawableTop the resource id of the drawable to be set on the top of the view
     * @param drawableRight the resource id of the drawable to be set on the right of the view
     * @param  drawableBottom the resource id of the drawable to be set on the bottom of the view*/
    public static void setVectorDrawable(Context context, EditText view, int drawableLeft, int drawableTop, int drawableRight, int drawableBottom)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable drawable_left=drawableLeft!=0? AppCompatResources.getDrawable(context,drawableLeft):null;
            Drawable drawable_top=drawableTop!=0? AppCompatResources.getDrawable(context,drawableTop):null;
            Drawable drawable_right=drawableRight!=0? AppCompatResources.getDrawable(context,drawableRight):null;
            Drawable drawable_bottom=drawableBottom!=0? AppCompatResources.getDrawable(context,drawableBottom):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
        else
        {
            Drawable drawable_left=drawableLeft!=0? VectorDrawableCompat.create(context.getResources(),drawableLeft,null):null;
            Drawable drawable_top=drawableTop!=0? VectorDrawableCompat.create(context.getResources(),drawableTop,null):null;
            Drawable drawable_right=drawableRight!=0? VectorDrawableCompat.create(context.getResources(),drawableRight,null):null;
            Drawable drawable_bottom=drawableBottom!=0? VectorDrawableCompat.create(context.getResources(),drawableBottom,null):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
    }
    /**
     * This function set a view's vector icon
     * @param context context to get resources from
     * @param view the button which needs an icon set
     * @param drawableLeft the resource id of the drawable to be set on the left of the view
     * @param drawableTop the resource id of the drawable to be set on the top of the view
     * @param drawableRight the resource id of the drawable to be set on the right of the view
     * @param  drawableBottom the resource id of the drawable to be set on the bottom of the view*/
    public static void setVectorDrawable(Context context, Button view, int drawableLeft, int drawableTop, int drawableRight, int drawableBottom)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable drawable_left=drawableLeft!=0? AppCompatResources.getDrawable(context,drawableLeft):null;
            Drawable drawable_top=drawableTop!=0? AppCompatResources.getDrawable(context,drawableTop):null;
            Drawable drawable_right=drawableRight!=0? AppCompatResources.getDrawable(context,drawableRight):null;
            Drawable drawable_bottom=drawableBottom!=0? AppCompatResources.getDrawable(context,drawableBottom):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
        else
        {
            Drawable drawable_left=drawableLeft!=0? VectorDrawableCompat.create(context.getResources(),drawableLeft,null):null;
            Drawable drawable_top=drawableTop!=0? VectorDrawableCompat.create(context.getResources(),drawableTop,null):null;
            Drawable drawable_right=drawableRight!=0? VectorDrawableCompat.create(context.getResources(),drawableRight,null):null;
            Drawable drawable_bottom=drawableBottom!=0? VectorDrawableCompat.create(context.getResources(),drawableBottom,null):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
    }
    /**
     * This function set a view's vector icon
     * @param context context to get resources from
     * @param view the textview which needs an icon set
     * @param drawableLeft the resource id of the drawable to be set on the left of the view
     * @param drawableTop the resource id of the drawable to be set on the top of the view
     * @param drawableRight the resource id of the drawable to be set on the right of the view
     * @param  drawableBottom the resource id of the drawable to be set on the bottom of the view*/
    public static void setVectorDrawable(Context context, TextView view, int drawableLeft, int drawableTop, int drawableRight, int drawableBottom)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Drawable drawable_left=drawableLeft!=0? AppCompatResources.getDrawable(context,drawableLeft):null;
            Drawable drawable_top=drawableTop!=0? AppCompatResources.getDrawable(context,drawableTop):null;
            Drawable drawable_right=drawableRight!=0? AppCompatResources.getDrawable(context,drawableRight):null;
            Drawable drawable_bottom=drawableBottom!=0? AppCompatResources.getDrawable(context,drawableBottom):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
        else
        {
            Drawable drawable_left=drawableLeft!=0? VectorDrawableCompat.create(context.getResources(),drawableLeft,null):null;
            Drawable drawable_top=drawableTop!=0? VectorDrawableCompat.create(context.getResources(),drawableTop,null):null;
            Drawable drawable_right=drawableRight!=0? VectorDrawableCompat.create(context.getResources(),drawableRight,null):null;
            Drawable drawable_bottom=drawableBottom!=0? VectorDrawableCompat.create(context.getResources(),drawableBottom,null):null;

            view.setCompoundDrawablesWithIntrinsicBounds(drawable_left,drawable_top,drawable_right,drawable_bottom);
        }
    }
    public static String makeName(int id)
    {
        String letters=String.valueOf(id);
        char[] array=letters.toCharArray();
        String name="";
        for(int count=0; count<array.length; count++)
        {
            switch (array[count])
            {
                case '0':
                    name+="zero";
                    break;
                case '1':
                    name+="one";
                    break;
                case '2':
                    name+="two";
                    break;
                case '3':
                    name+="three";
                    break;
                case '4':
                    name+="four";
                    break;
                case '5':
                    name+="five";
                    break;
                case '6':
                    name+="six";
                    break;
                case '7':
                    name+="seven";
                    break;
                case '8':
                    name+="eight";
                    break;
                case '9':
                    name+="nine";
                    break;
                default :
                    name+="NON";
            }
        }
        return name;
    }
}
