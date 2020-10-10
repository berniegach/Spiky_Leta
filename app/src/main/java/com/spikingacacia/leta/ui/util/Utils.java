/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 9/7/20 8:16 PM
 */

package com.spikingacacia.leta.ui.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Currency;
import java.util.Locale;

public class Utils
{
    private static String TAG = "utils";
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
    public static Bitmap generateQRCode(String string)
    {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try
        {
            BitMatrix bitMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e)
        {
            Log.e(TAG,""+e.getMessage());
            return null;
        }
    }

}
