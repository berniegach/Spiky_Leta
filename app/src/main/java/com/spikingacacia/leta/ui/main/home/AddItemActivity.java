/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.Groups;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class AddItemActivity extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 3;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private ProgressBar progressBar;
    private View mainView;
    private ImageView imageView;
    private LinearLayout layoutAddSizes;
    private String imagePath;
    private Bitmap bitmap;
    private String sizes;
    private String prices;
    private boolean bitmapChanged = false;
    private String TAG = "add_item_a";
    private  Uri imageUri;
    private String mCameraFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //views
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);
        imageView = findViewById(R.id.image);
        final ChipGroup chipGroup = findViewById(R.id.chip_group_category);
        final ChipGroup chipGroupGroup = findViewById(R.id.chip_group_group);
        final EditText editItem = findViewById(R.id.item);
        final EditText editDescription = findViewById(R.id.description);
        ImageButton add_sizes_Button = findViewById(R.id.add_sizes);
        layoutAddSizes = findViewById(R.id.layout_sizes);
        Button button_add = findViewById(R.id.button_add);
        ImageButton b_gallery = findViewById(R.id.gallery);
        ImageButton b_camera = findViewById(R.id.camera);

        //image
        b_camera.setEnabled(checkCameraHardware(this));
        b_gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTheImage();
            }
        });
        b_camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check for the camera permission before accessing the camera.  If the
                // permission is not granted yet, request permission.
                if( (ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.CAMERA)) == PackageManager.PERMISSION_GRANTED)
                    cameraIntent();
                else
                    requestCameraPermission();
            }
        });

        //categories
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            Chip chip = new Chip(AddItemActivity.this);
            chip.setText(categories.getTitle());
            chip.setTag(categories.getId());
            chip.setClickable(true);
            chip.setCheckable(true);
            chipGroup.addView(chip);
        }
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId)
            {
                chipGroupGroup.removeAllViews();
                Chip chip = chipGroup.findViewById( chipGroup.getCheckedChipId() );
                if(chip != null)
                {
                    int category_id = (int)chip.getTag();
                    Iterator iterator = MainActivity.groupsLinkedHashMap.entrySet().iterator();
                    while(iterator.hasNext())
                    {
                        LinkedHashMap.Entry<Integer, Groups>set=(LinkedHashMap.Entry<Integer, Groups>) iterator.next();
                        int id=set.getKey();
                        Groups groups = set.getValue();
                        if(groups.getCategoryId() != category_id)
                            continue;
                        Chip chip_group = new Chip(AddItemActivity.this);
                        chip_group.setText(groups.getTitle());
                        chip_group.setTag(groups.getId());
                        chip_group.setClickable(true);
                        chip_group.setCheckable(true);
                        chipGroupGroup.addView(chip_group);
                    }

                }

            }
        });

        //add sizes
        add_sizes_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewSizeLayout();
            }
        });

        //add the new item
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //upload
                /*if (imagePath == null)
                {
                    Toast.makeText(getBaseContext(),"Please select the image", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                //category
                Chip chip = chipGroup.findViewById( chipGroup.getCheckedChipId() );
                if(chip == null)
                {
                    Toast.makeText(AddItemActivity.this,"Category needed",Toast.LENGTH_SHORT).show();
                    return;
                }
                int category_id = (int)chip.getTag();
                //group
                int group_id = -1;
                Chip chip_group = chipGroupGroup.findViewById( chipGroupGroup.getCheckedChipId() );
                if(chip_group != null)
                {
                    group_id = (int)chip_group.getTag();
                }
                String item = editItem.getText().toString();
                if(TextUtils.isEmpty(item))
                {
                    editItem.setError("Item name empty");
                    return;
                }
                String description = editDescription.getText().toString();
                if(TextUtils.isEmpty(description))
                {
                    editDescription.setError("Description empty");
                    return;
                }
                if(!formSizesPrices())
                {
                    Toast.makeText(getBaseContext(),"Please enter sizes and prices", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Uploading code
                try
                {
                    new CreateItemTask(item,description,category_id, group_id,".jpg", bitmap).execute((Void)null);
                }
                catch (Exception e)
                {
                    Log.e("uploading",""+e.getMessage());
                }

            }
        });
    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.camera).setOnClickListener(listener);
        Snackbar.make(imageView, "Access to camera is needed for taking photos",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("GRANT", listener)
                .show();
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }

    private void addNewSizeLayout()
    {
        final View view = getLayoutInflater().inflate(R.layout.item_dialog_sizes_prices, null);
        final ImageButton deleteButton = view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layoutAddSizes.removeView(view);
            }
        });
        layoutAddSizes.addView(view);
        view.requestFocus();
    }

    private void getTheImage()
    {
        Toast.makeText(getBaseContext(),"Please wait",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),1);
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void cameraIntent() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");

        String newPicFile = df.format(date) + ".jpg";
        String outPath = "/sdcard/" + newPicFile;
        File outFile = new File(outPath);

        mCameraFileName = outFile.toString();
        Uri outuri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
    private boolean formSizesPrices()
    {
        sizes="";
        prices="";
        int count = layoutAddSizes.getChildCount();
        for(int c = 0; c<count; c++)
        {
            View view = layoutAddSizes.getChildAt(c);
            TextView t_size = view.findViewById(R.id.edit_size);
            TextView t_price = view.findViewById(R.id.edit_price);
            String s_size = t_size.getText().toString();
            String s_price = t_price.getText().toString();
            if(TextUtils.isEmpty(s_size))
            {
                t_size.setError("No size");
                return false;
            }
            if(TextUtils.isEmpty(s_price))
            {
                t_price.setError("No price");
                return false;
            }
            if(c != 0)
            {
                sizes+=":";
                prices+=":";
            }
            sizes+=s_size;
            prices+=s_price;

        }
        return true;
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

                imagePath = getPath(uri);
                Log.d("path",""+imagePath);

                bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                bitmapChanged = true;
            }
            catch (Exception e)
            {
                Log.e("bitmap", "" + e.getMessage());
            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                Log.d(TAG,"IMAGE IMAGE 1");
            }
            if (imageUri == null && mCameraFileName != null) {
                imageUri = Uri.fromFile(new File(mCameraFileName));
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    bitmapChanged = true;

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            File file = new File(mCameraFileName);
            if (!file.exists()) {
                file.mkdir();
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
    private void uploadBitmap(final Bitmap bitmap2, final int insert_id)
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
                        Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        //new menuFragment.MenuTask().execute((Void)null);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Toast.makeText(getBaseContext(), "There was an error. Please try again", Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("png", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String , String >params = new HashMap<>();
                params.put("name", "name"); //Adding text parameter to the request
                params.put("id",String.valueOf(insert_id));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getBaseContext()).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(final Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int quality = 100;
        while(true)
        {
            byteArrayOutputStream.reset();
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream))
            {

                //Log.e(TAG,"bytes length "+byteArrayOutputStream.toByteArray().length);
                if(byteArrayOutputStream.toByteArray().length<=500000)
                    return byteArrayOutputStream.toByteArray();
            }
            else
                return null;
            quality-=10;
        }

    }
    public class CreateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_add_item = base_url+"create_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String item;
        private String description;
        private Integer category_id;
        private Integer group_id;
        private String image_type;
        private Bitmap bitmap;
        private  int insert_id;
        private int success;
        CreateItemTask(final String item, final String description, final Integer category_id, int group_id, String image_type, Bitmap bitmap)
        {
            showProgress(true);
            this.item = item;
            this.description = description;
            this.category_id = category_id;
            this.group_id = group_id;
            this.image_type = image_type;
            this.bitmap = bitmap;
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("item",item));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("category_id",Integer.toString(category_id)));
            info.add(new BasicNameValuePair("group_id",Integer.toString(group_id)));
            info.add(new BasicNameValuePair("sizes",sizes));
            info.add(new BasicNameValuePair("prices",prices));
            info.add(new BasicNameValuePair("image_type",image_type));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_item,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    insert_id = jsonObject.getInt("id");
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {

                Log.d("adding new item", "done...");
                if(bitmapChanged)
                    uploadBitmap(bitmap, insert_id);
                else
                {
                    Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }


            }
            else if(success==-2)
            {
                showProgress(false);
                Log.e("adding item", "error");
                Toast.makeText(getBaseContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}