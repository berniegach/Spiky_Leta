package com.spikingacacia.leta.ui;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.preference.Preference;

import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceViewHolder;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.main.home.ItemDialog;
import com.spikingacacia.leta.ui.util.GetFilePathFromDevice;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * Created by $USER_NAME on 9/20/2018.
 **/
public class SPreferencePic extends Preference
{
    private static final int PERMISSION_REQUEST_INTERNET=2;
    private static String url_upload_profile_pic= LoginA.base_url+"upload_profile_pic_s.php";
    public static ImageView imageView;
    public static TextView textView;
    private static Context context;
    private static JSONParser jsonParser;
    private static String TAG_SUCCESS="success";
    private static String TAG_MESSAGE="message";
    private FragmentManager fragmentManager;
    private Preferences preferences;
    public SPreferencePic(Context context)
    {
        super(context);
        setLayoutResource(R.layout.ssettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);
    }

    public SPreferencePic(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.ssettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);
    }
    public SPreferencePic(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.ssettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);

    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder view)
    {
        super.onBindViewHolder(view);
        imageView=(ImageView)view.findViewById(R.id.image);
        //get the profile pic
        imageView.setImageBitmap(SSettingsA.profilePic);

        view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                final FragmentManager fragmentManager=((AppCompatActivity)context).getFragmentManager();
                Fragment fragment= GetPicture.newInstance();
                fragmentManager.beginTransaction().add(fragment,"AB").commit();
                fragmentManager.executePendingTransactions();
                Intent intent=new Intent();
                //show only images
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fragment.startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),1);
                notifyChanged();
            }
        });

    }

    public static class GetPicture extends Fragment
    {
        public static GetPicture newInstance()
        {
            GetPicture getPicture=new GetPicture();
            return getPicture;

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

                    //final String path = getPath(uri);
                    final String path= GetFilePathFromDevice.getPath(context,uri);
                    Log.d("path",path);

                if (true)
                {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(context, "Profile pic changed", Toast.LENGTH_SHORT).show();
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
            getFragmentManager().beginTransaction().remove(this).commit();
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
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
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
            Volley.newRequestQueue(context).add(volleyMultipartRequest);
        }
        public byte[] getFileDataFromDrawable(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        private String makeName(int id)
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
}
