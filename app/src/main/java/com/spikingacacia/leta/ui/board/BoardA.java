package com.spikingacacia.leta.ui.board;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.LoginA;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static com.spikingacacia.leta.ui.LoginA.base_url;

public class BoardA extends AppCompatActivity implements advF.OnListFragmentInteractionListener
{
    private static final int PERMISSION_REQUEST_INTERNET=255;
    private String url_add_advert= base_url+"add_advert.php";
    private RecyclerView recyclerView;
    public String title;
    public String content;
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

                final String path = getPath(uri);
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
                            Logger.setLogLevel(Logger.LogLevel.DEBUG);
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
    private boolean uploadPic(final String location, final Bitmap bitmap) {
        boolean ok=true;
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            //getting name for the image
            String name="photo";
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
                    final String tag="GOTEV";
                    String uploadId = UUID.randomUUID().toString();
                    //Creating a multi part request
                    new MultipartUploadRequest(getBaseContext(), uploadId, url_add_advert)
                            .addFileToUpload(path, "jpg") //Adding file
                            .addParameter("name", name) //Adding text parameter to the request
                            .addParameter("seller_id",String.valueOf(LoginA.sellerAccount.getId()))
                            .addParameter("title",title)
                            .addParameter("content",content)
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
                                    String response=serverResponse.getBodyAsString();
                                    //sometimes the last } is missing
                                    if(!response.endsWith("}"))
                                        response+="}";
                                    Log.d("GOTEV",response);
                                    try
                                    {
                                        JSONObject jsonObject=new JSONObject(response);
                                        if(jsonObject.getInt("success")==1)
                                        {
                                            int id=jsonObject.getInt("id");
                                            String date=jsonObject.getString("date");
                                            advRVA adapter=(advRVA) recyclerView.getAdapter();
                                            adapter.notifyChange(id,title,bitmap,content,0,0,0,date);
                                            Toast.makeText(getBaseContext(), "successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            String message=jsonObject.getString("message");
                                            Log.e(tag,""+message);
                                        }
                                    } catch (JSONException e)
                                    {
                                        Log.e(tag,"error "+e.getMessage());
                                        e.printStackTrace();
                                    }

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
                        uploadPic(location,bitmap);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}
