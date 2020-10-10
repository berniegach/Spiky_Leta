/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 6/27/20 9:09 AM
 */

package com.spikingacacia.leta.ui.board;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.util.GetFilePathFromDevice;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class BoardA extends AppCompatActivity implements advF.OnListFragmentInteractionListener
{
    private static final int PERMISSION_REQUEST_INTERNET=255;
    private String url_add_advert= base_url+"add_advert.php";
    private RecyclerView recyclerView;
    public String title;
    public String content;
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_board);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tasty Board");

        Fragment fragment=advF.newInstance(1);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"ads");
        transaction.commit();
    }

    @Override
    public void onAdClicked(AdsC.AdItem item)
    {
        Fragment fragment=AdOverviewF.newInstance(item.id,item.title, item.bitmap, item.content, item.views,item.likes,item.comments,item.date);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"overview");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onUploadPhoto(RecyclerView recyclerView, String title, String content)
    {
        this.recyclerView=recyclerView;
        this.title=title;
        this.content=content;
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image in jpg format"),1);
    }
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            final Uri uri = data.getData();
            try
            {

                final String path= GetFilePathFromDevice.getPath(this,uri);
                Log.d("path",path);

                if (true)
                {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), uri);
                    //upload
                    if (path == null)
                    {
                        Log.e("upload cert","its null");
                    }
                    else
                    {
                        //Uploading code
                        Log.d("uploading","1");
                        try
                        {
                            confirm_upload(path,bitmap);
                            //uploadPic(path, bitmap);

                        }
                        catch (Exception e)
                        {
                            Log.d("uploading","2");
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("bitmap", "" + e.getMessage());
            }
        }
    }
    private void uploadBitmap(final Bitmap bitmap)
    {
        String url_upload_profile_pic= LoginActivity.base_url+"upload_inventory_pic.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url_upload_profile_pic,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        //weve uploaded the image therefore its okay to proceed with adding the new item in the server
                        int statusCode = response.statusCode;
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("png", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String , String >params = new HashMap<>();
                params.put("name", "name"); //Adding text parameter to the request
                //params.put("id",String.valueOf(getCategoryId(category_title)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getBaseContext()).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void confirm_upload(final String location, final Bitmap bitmap)
    {
        final android.app.AlertDialog dialog;
        android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(this);
        builderPass.setTitle("Preview");
        //views
        final TextView t_title=new TextView(this);
        final TextView t_content=new TextView(this);
        final ImageView imageView=new ImageView(this);
        t_title.setText(title);
        t_title.setPadding(20,0,20,5);
        t_content.setText(content);
        t_content.setPadding(20,5,20,0);
        imageView.setImageBitmap(bitmap);
        imageView.setMaxHeight(100);
        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(t_title);
        layout.addView(imageView);
        layout.addView(t_content);
        builderPass.setView(layout);
        builderPass.setPositiveButton("Proceed", null);
        builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        dialog=builderPass.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button button=((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //uploadPic(location,bitmap);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}
