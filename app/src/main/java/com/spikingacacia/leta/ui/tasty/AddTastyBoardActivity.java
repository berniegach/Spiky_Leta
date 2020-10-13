/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.tasty;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.main.home.AddItemActivity;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class AddTastyBoardActivity extends AppCompatActivity
{
    private ProgressBar progressBar;
    private View mainView;
    private ImageView imageView;
    private String imagePath;
    private Bitmap bitmap;
    private LinearLayout layoutAddSizes;
    private  TextView t_expiry;
    private String TAG = "add_tasty_board_a";
    private String sizeAndPrices;
    private String prices;
    private Calendar date;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasty_board);

        setTitle("Tasty Board");

        //views
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);
        imageView = findViewById(R.id.image);
        final Spinner spinner = findViewById(R.id.spinner);
        layoutAddSizes = findViewById(R.id.layout_sizes);
        final EditText e_title = findViewById(R.id.title);
        final EditText e_description = findViewById(R.id.description);
        Button b_time = findViewById(R.id.b_time);
        t_expiry = findViewById(R.id.expiry);
        Button b_post = findViewById(R.id.b_post);

        //menu items
        final List<String> menu_items= getMenuItems();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getBaseContext(),   android.R.layout.simple_spinner_item, menu_items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                DMenu dMenu = getMenuItemFromSpinner(position);
                if(dMenu!=null)
                    addNewSizeLayoutAndFillData(dMenu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //image
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTheImage();
            }
        });

        //time
        b_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setExpiryTime();
            }
        });

        //post
        b_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //upload
                if (imagePath == null)
                {
                    Toast.makeText(getBaseContext(),"Please select the image", Toast.LENGTH_SHORT).show();
                    return;
                }
                String title = e_title.getText().toString();
                if(TextUtils.isEmpty(title))
                {
                    e_title.setError("title empty");
                    return;
                }
                String description = e_description.getText().toString();
                if(TextUtils.isEmpty(description))
                {
                    e_description.setError("Description empty");
                    return;
                }
                if(!formSizesPrices())
                {
                    Toast.makeText(getBaseContext(),"Please enter valid prices", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(t_expiry.getText().toString().contentEquals("set expiry time"))
                {
                    Toast.makeText(getBaseContext(),"Please set expiry time", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Uploading code
                try
                {
                    int menu_item_id = getMenuItemId( menu_items.get(spinner.getSelectedItemPosition()));
                    new AddTastyBoardTask(title,description,menu_item_id,date.getTime().toString(),".jpg",bitmap).execute((Void)null);
                }
                catch (Exception e)
                {
                    Log.e("uploading",""+e.getMessage());
                }
            }
        });

    }
    void setExpiryTime()
    {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(AddTastyBoardActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AddTastyBoardActivity.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        t_expiry.setText(date.getTime().toString());
                    }
                },
                        currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        },
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
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
        //sizes="";
        sizeAndPrices = "";
        prices="";
        int count = layoutAddSizes.getChildCount();
        for(int c = 0; c<count; c++)
        {
            View view = layoutAddSizes.getChildAt(c);
            TextView t_size = view.findViewById(R.id.edit_size);
            TextView t_price = view.findViewById(R.id.price_new);
            String s_size = t_size.getText().toString();
            s_size = s_size.replace(" @ ",",");
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
                sizeAndPrices+=":";
                prices+=":";
            }
            sizeAndPrices+=s_size;
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
                //byte[] bitmapdata = getFileDataFromDrawable(bitmap);
                //Bitmap bitmap_local = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Log.e("bitmap", "" + e.getMessage());
            }
        }
    }
    private List<String> getMenuItems()
    {
        List<String> list = new ArrayList<>();
        Iterator iterator = MainActivity.menuLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu>set=(LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            int id=set.getKey();
            DMenu dMenu = set.getValue();
            list.add(dMenu.getItem());
        }
        return  list;
    }
    private int getMenuItemId(String item)
    {
        Iterator iterator = MainActivity.menuLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu>set=(LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            int id=set.getKey();
            DMenu dMenu = set.getValue();
            String title = dMenu.getItem();
            if(item.contentEquals(title))
                return id;
        }
        return -1;
    }
    private String getMenuItemPrice(String item)
    {
        Iterator iterator = MainActivity.menuLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu>set=(LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            int id=set.getKey();
            DMenu dMenu = set.getValue();
            String title = dMenu.getItem();
            if(item.contentEquals(title))
                return dMenu.getPrices();
        }
        return "";
    }
    private DMenu getMenuItemFromSpinner(int index)
    {
        int count = 0;
        Iterator iterator = MainActivity.menuLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu>set=(LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            int id=set.getKey();
            if(count == index)
                return set.getValue();
            count+=1;
        }
        return null;
    }
    private void addNewSizeLayoutAndFillData(DMenu dMenu)
    {
        //first remove extra sizes
        for(int c=1; c<layoutAddSizes.getChildCount(); c++)
        {
            layoutAddSizes.removeViewAt(c);
        }
        String[] sizes = dMenu.getSizes().split(":");
        final String[] prices = dMenu.getPrices().split(":");

        final TextView t_size_first = layoutAddSizes.getChildAt(0).findViewById(R.id.edit_size);
        final EditText e_price_new_first = layoutAddSizes.getChildAt(0).findViewById(R.id.price_new);
        final TextView t_discount_first = layoutAddSizes.getChildAt(0).findViewById(R.id.discount);
        for(int c=0; c<sizes.length; c++)
        {
            final int fin_c = c;
            if( c==0 )
            {
                t_size_first.setText(sizes[0]+" @ "+prices[0]);
                e_price_new_first.setText(prices[0]);
                t_discount_first.setText("0 % off");
                e_price_new_first.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        String s_price = s.toString();
                        if(s_price.length()>0)
                        {
                            double price = Double.parseDouble(s_price);
                            Double discount = ( Double.parseDouble(prices[fin_c])-price )/ Double.parseDouble(prices[fin_c]) * 100;
                            t_discount_first.setText(discount.intValue()+" % off");
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {

                    }
                });

            }
            else
            {
                final View view = getLayoutInflater().inflate(R.layout.item_dialog_sizes_prices_tasty_board, null);
                final TextView t_size = view.findViewById(R.id.edit_size);
                final EditText e_price_new = view.findViewById(R.id.price_new);
                final TextView t_discount = view.findViewById(R.id.discount);

                t_size.setText(sizes[c] +" @ "+prices[c]);
                e_price_new.setText(prices[c]);
                t_discount.setText("0 % off");
                e_price_new.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        String s_price = s.toString();
                        if(s_price.length()>0)
                        {
                            double price = Double.parseDouble(s_price);
                            Double discount = ( Double.parseDouble(prices[fin_c])-price )/ Double.parseDouble(prices[fin_c]) * 100;
                            t_discount.setText(discount.intValue()+" % off");
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {

                    }
                });

                layoutAddSizes.addView(view);
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
        String url_upload_profile_pic= LoginActivity.base_url+"upload_tasty_board_pic.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url_upload_profile_pic,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        //weve uploaded the image therefore its okay to proceed with adding the new ad in the server
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
    public class AddTastyBoardTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_add_item = base_url+"add_tasty_board_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String title;
        private String description;
        private Integer linked_item_id;
        private String expiry;
        private String image_type;
        private Bitmap bitmap;
        private  int insert_id;
        private int success;
        AddTastyBoardTask(final String title, final String description, final Integer linked_item_id,  String expiry, String image_type, Bitmap bitmap)
        {
            showProgress(true);
            this.title = title;
            this.description = description;
            this.linked_item_id = linked_item_id;
            this.expiry = expiry;
            this.image_type = image_type;
            this.bitmap = bitmap;
            jsonParser = new JSONParser();
            Log.d("CRATEITEM"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("title",title));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("linked_item_id",Integer.toString(linked_item_id)));
            info.add(new BasicNameValuePair("size_and_price",sizeAndPrices));
            info.add(new BasicNameValuePair("discount_price",prices));
            info.add(new BasicNameValuePair("expiry",expiry));
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

                Log.d("adding new title", "done...");
                uploadBitmap(bitmap, insert_id);

            }
            else if(success==-2)
            {
                showProgress(false);
                Log.e("adding title", "error");
                Toast.makeText(getBaseContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}