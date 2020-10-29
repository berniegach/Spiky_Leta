/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.qr_code;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.spikingacacia.leta.R;

import java.io.File;
import java.io.FileOutputStream;

public class Encoder
{
    private static String TAG = "qr_encoder";
    public Encoder()
    {
    }
    public static Bitmap encode(Context context, String text, String table_number)
    {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrixTable;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                //Bitmap bitmapBack = getBitmap(context, R.drawable.ic_leta_qr_code_back);
                Bitmap bitmapBack = getBitmap(context, R.drawable.leta_qr_back);
                double W = bitmapBack.getWidth();
                bitMatrixTable = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,(int) (0.3*W),(int) (0.3*W));
            }
            else
            {
                bitMatrixTable = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,965,965);
            }

            //create the bitmaps
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmapTable = barcodeEncoder.createBitmap(bitMatrixTable);
            //get an overlaid bitmap
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                bitmapTable = overlay(context, bitmapTable, table_number);
            return bitmapTable;
        }
        catch (WriterException e)
        {
            Log.e(TAG,""+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap overlay(Context context, Bitmap bitmapTable, String table_number)
    {
        //Bitmap bitmapBack = getBitmap(context, R.drawable.ic_leta_qr_code_back);
        Bitmap bitmapBack = getBitmap(context, R.drawable.leta_qr_back);

        Bitmap bmOverlayBack = Bitmap.createBitmap(bitmapBack.getWidth(), bitmapBack.getHeight(), bitmapBack.getConfig());
        long W = bitmapBack.getWidth();
        long H = bitmapBack.getHeight();
        long x1 = W - bitmapTable.getWidth()/2*3;
        long y1 = H/2 - bitmapTable.getHeight()/2;
        Canvas canvas = new Canvas(bmOverlayBack);

        canvas.drawBitmap(bitmapBack, new Matrix(),null);
        canvas.drawBitmap(bitmapTable,x1,y1,null);
        // draw table number text
        Paint paint = new Paint();
        //canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(70);
        canvas.drawText(table_number, 30, 100, paint);
        return bmOverlayBack;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable)
    {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(Context context, int drawableId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable)
        {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable)
        {
            return getBitmap((VectorDrawable) drawable);
        }
        else
            {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}
