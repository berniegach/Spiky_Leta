package com.spikingacacia.leta.ui.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.main.home.ItemDialog;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SIInventoryA extends AppCompatActivity
implements SICategoryF.OnListFragmentInteractionListener,
        SIGroupF.OnListFragmentInteractionListener,
        SIItemF.OnListFragmentInteractionListener
{
    private static final int PERMISSION_REQUEST_INTERNET=255;
    private static String url_upload_photos=  LoginA.base_url+"upload_inventory_pic.php";
    int whichFragment=1;
    int whichPhoto;
    int photoId;
    String previousTitle;
    Preferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_siinventory);
        //preference
        preferences=new Preferences(getBaseContext());
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get intent
        Intent intent=getIntent();
        //int which=intent.getIntExtra("which",1);
        //String title=intent.getStringExtra("title");
        //categories
        getSupportActionBar().setTitle("Categories");
        Fragment fragment=SICategoryF.newInstance(1);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"Categories");
        transaction.commit();


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(whichFragment==2)
            getSupportActionBar().setTitle("Categories");
        else if(whichFragment==3)
        {
            getSupportActionBar().setTitle(previousTitle);
            whichFragment=2;
        }

    }
    /**
     * implementation of SICategoryF.java*/
    @Override
    public void onItemClicked(SICategoryC.CategoryItem item)
    {
        whichFragment=2;
        String title=item.category;
        title=title.replace("_"," ");
        getSupportActionBar().setTitle(title);
        previousTitle=title;
        Fragment fragment=SIGroupF.newInstance(item.id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,title);
        transaction.addToBackStack(title);
        transaction.commit();
    }
    @Override
    public void onCategoryPhotoEdit(int id)
    {
        uploadPhoto(1,id);
    }
    /**
     * implementation of SIGroupF.java*/
    @Override
    public void onItemClicked(SIGroupC.GroupItem item)
    {
        whichFragment=3;
        String title=item.group;
        title=title.replace("_"," ");
        getSupportActionBar().setTitle(title);
        Fragment fragment=SIItemF.newInstance(item.category,item.id);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,title);
        transaction.addToBackStack(title);
        transaction.commit();
    }
    @Override
    public void onGroupPhotoEdit(int id)
    {
        uploadPhoto(2,id);
    }
    /**
     * implementation of SIItemF.java*/
    @Override
    public void onItemPhotoEdit(int id)
    {
        uploadPhoto(3,id);
    }
    private void uploadPhoto(int which, int id)
    {
        whichPhoto=which;
        photoId=id;
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),1);
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

                final String path = getPath(uri);
                Log.d("path",path);

                if (true)
                {
                    //final Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    //imageView.setImageBitmap(bitmap);
                    Toast.makeText(getBaseContext(), "Photo changed", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    //refresh fragment
                    /*String title=getSupportActionBar().getTitle().toString();
                    Fragment fragment=null;
                    fragment=getSupportFragmentManager().findFragmentByTag(title);
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.detach(fragment);
                    transaction.attach(fragment);
                    transaction.commit();*/
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
                            //uploadPic(path);

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
        //getFragmentManager().beginTransaction().remove(this).commit();
    }
    private String getPath(Uri uri)
    {
        if(uri==null)
            return null;
        String res=null;

        if (DocumentsContract.isDocumentUri(getBaseContext(), uri))
        {
            //emulator
            String[] path = uri.getPath().split(":");
            res = path[1];
            Log.i("debinf ProdAct", "Real file path on Emulator: "+res);
        }
        else {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getBaseContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }
    private void uploadBitmap(final Bitmap bitmap)
    {
        String url_upload_profile_pic= LoginA.base_url+"upload_inventory_pic.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url_upload_profile_pic,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        //weve uploaded the image therefore its okay to proceed with adding the new item in the server
                        int statusCode = response.statusCode;
                        //new ItemDialog.CreateItemTask(item,description,getCategoryId(category_title)).execute((Void)null);
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

}
