package com.spikingacacia.leta.ui.main.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

public class EditItemActivity extends AppCompatActivity
{
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DMenu dMenu;
    private ProgressBar progressBar;
    private View mainView;
    private ImageView imageView;
    private LinearLayout layoutAddSizes;
    private String imagePath;
    private Bitmap bitmap;
    private String sizes;
    private String prices;
    private boolean bitmapChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        //get the menu
        dMenu = (DMenu) getIntent().getSerializableExtra("item");
        //views
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);
        imageView = findViewById(R.id.image);
        final Spinner spinner = findViewById(R.id.spinner);
        final EditText editItem = findViewById(R.id.item);
        final EditText editDescription = findViewById(R.id.description);
        ImageButton add_sizes_Button = findViewById(R.id.add_sizes);
        layoutAddSizes = findViewById(R.id.layout_sizes);
        Button button_edit = findViewById(R.id.button_edit);
        Button button_delete = findViewById(R.id.button_delete);

        //image
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTheImage();
            }
        });

        //categories
        final List<String> categories= getCategories();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getBaseContext(),   android.R.layout.simple_spinner_item, categories);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(getCategoryIndexInSpinner(dMenu.getCategoryId()));

        //image
        String image_url= base_url+"src/items_pics/";
        String url=image_url+String.valueOf(dMenu.getId())+'_'+dMenu.getImageType();
        Glide.with(getBaseContext()).load(url).into(imageView);
        //item
        editItem.setText(dMenu.getItem());
        //description
        editDescription.setText(dMenu.getDescription());
        //sizes and prices
        addNewSizeLayoutAndFillData();

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
        button_edit.setOnClickListener(new View.OnClickListener()
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

                int category_id = getCategoryId( categories.get(spinner.getSelectedItemPosition()) );
                //Uploading code
                try
                {
                    new UpdateItemTask(dMenu.getId(),item,description,category_id, dMenu.getLinkedItems(),".jpg").execute((Void)null);
                }
                catch (Exception e)
                {
                    Log.e("uploading",""+e.getMessage());
                }
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(EditItemActivity.this)
                        .setTitle("DELETE")
                        .setMessage("Are you sure you want to delete this item?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new DeleteItemTask(dMenu.getId(), dMenu.getImageType()).execute((Void)null);
                            }
                        }).create().show();
            }
        });
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    private void addNewSizeLayoutAndFillData()
    {
        String[] sizes = dMenu.getSizes().split(":");
        String[] prices = dMenu.getPrices().split(":");

        final EditText t_size_first = layoutAddSizes.getChildAt(0).findViewById(R.id.edit_size);
        final EditText t_price_first = layoutAddSizes.getChildAt(0).findViewById(R.id.edit_price);

        for(int c=0; c<sizes.length; c++)
        {
            if( c==0 )
            {
                t_size_first.setText(sizes[0]);
                t_price_first.setText(prices[0]);
            }
            else
            {
                final View view = getLayoutInflater().inflate(R.layout.item_dialog_sizes_prices, null);
                final EditText t_size = view.findViewById(R.id.edit_size);
                final EditText t_price = view.findViewById(R.id.edit_price);
                final ImageButton deleteButton = view.findViewById(R.id.delete);

                t_size.setText(sizes[c]);
                t_price.setText(prices[c]);
                deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        layoutAddSizes.removeView(view);
                    }
                });
                layoutAddSizes.addView(view);
            }

        }


    }
    private List<String> getCategories()
    {
        List<String> list = new ArrayList<>();
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            list.add(categories.getTitle());
        }
        return  list;
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
    private int getCategoryId(String item)
    {
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            String title = categories.getTitle();
            if(item.contentEquals(title))
                return id;
        }
        return -1;
    }
    private int getCategoryIndexInSpinner(int category_id)
    {
        int index = 0;
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            int cat_id = categories.getId();
            if(cat_id == category_id)
                return index;
            index+=1;
        }
        return -1;
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
    private void uploadBitmap(final Bitmap bitmap, final int insert_id)
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
                if(byteArrayOutputStream.toByteArray().length<=1000000)
                    return byteArrayOutputStream.toByteArray();
            }
            else
                return null;
            quality-=10;
        }

    }
    private class UpdateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_item = base_url+"update_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int item_id;
        private String item;
        private String description;
        private Integer category_id;
        private String linked_items;
        //private String selling_price;
        private String image_type;
        private int success;
        UpdateItemTask(int item_id, final String item, final String description, final Integer category_id, String linked_items, String image_type)
        {
            showProgress(true);
            this.item_id = item_id;
            this.item = item;
            this.description = description;
            this.category_id = category_id;
            this.linked_items = linked_items;
            //this.selling_price = selling_price;
            this.image_type = image_type;
            jsonParser = new JSONParser();
            Log.d("CRATEITEM"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            info.add(new BasicNameValuePair("item_id",String.valueOf(item_id)));
            info.add(new BasicNameValuePair("category_id",Integer.toString(category_id)));
            info.add(new BasicNameValuePair("group_id",Integer.toString(-1)));
            info.add(new BasicNameValuePair("linked_items",linked_items));
            info.add(new BasicNameValuePair("item",item));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("sizes",sizes));
            info.add(new BasicNameValuePair("selling_price",prices));
            info.add(new BasicNameValuePair("image_type",image_type));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_item,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
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
                    uploadBitmap(bitmap, dMenu.getId());
                else
                {
                    Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
                showProgress(false);
            }

        }
    }
    private class DeleteItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_delete_item = base_url+"delete_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int success=0;
        final private int mId;
        private String image_type;

        DeleteItemTask(final int id, String image_type)
        {
            showProgress(true);
            mId=id;
            this.image_type = image_type;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING ITEM: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("item_id",Integer.toString(mId)));
            info.add(new BasicNameValuePair("image_type",image_type));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_item,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("DELETING ITEM: ","finished....");
            if (successful)
            {
                onBackPressed();
            }
            else
            {
                Toast.makeText(getBaseContext(),"There was an error. Please try again",Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        }

    }
}