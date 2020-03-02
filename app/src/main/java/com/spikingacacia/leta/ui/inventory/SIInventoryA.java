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
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.Preferences;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

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
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight_NoActionBarLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }

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
                            Logger.setLogLevel(Logger.LogLevel.DEBUG);
                            uploadPic(path);

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
    private boolean uploadPic(final String location) {
        boolean ok=true;
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            //getting name for the image
            String name;
            if(whichPhoto==1)
                name="c_"+photoId;
            else if(whichPhoto==2)
                name="g_"+photoId;
            else
                name="i_"+photoId;
            //getting the actual path of the image
            // String path=getPath(certUri[index]);
            String path=location;
            if (path == null)
            {
                Log.e("upload cert","its null");
            }
            else
            {
                //Uploading code
                try
                {
                    String uploadId = UUID.randomUUID().toString();
                    //Creating a multi part request
                    new MultipartUploadRequest(getBaseContext(), uploadId, url_upload_photos)
                            .addFileToUpload(path, "jpg") //Adding file
                            .addParameter("name", name) //Adding text parameter to the request
                            .addParameter("id",String.valueOf(LoginA.sellerAccount.getId()))
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate()
                            {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo)
                                {
                                    Log.d("GOTEV",uploadInfo.toString());
                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception)
                                {
                                    Log.e("GOTEV",uploadInfo.toString()+"\n"+exception.toString()+"\n");
                                }

                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
                                {
                                    //JSONObject result = new JSONObject(serverResponse);
                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo)
                                {
                                    Log.d("GOTEV","cancelled"+uploadInfo.toString());
                                }
                            })
                            .startUpload(); //Starting the upload
                }
                catch (Exception e)
                {
                    Log.e("image upload",""+e.getMessage());
                    e.printStackTrace();
                    ok=false;
                }
            }
        }
        //request the permission
        else
        {
            Log.d("fjhgsdjfgd","jsjgdjsgds");
            ok=false;
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {

            }
            else
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_INTERNET);
        }

        return ok;
    }

}
