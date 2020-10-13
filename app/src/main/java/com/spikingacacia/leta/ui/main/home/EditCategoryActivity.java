/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.home;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class EditCategoryActivity extends AppCompatActivity
{
    private ProgressBar progressBar;
    private View mainView;
    private ImageView imageView;
    private LinearLayout layoutAddSizes;
    private String imagePath;
    private Bitmap bitmap;
    private String sizes;
    private String prices;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        Categories category = (Categories) getIntent().getSerializableExtra("category");
        //views
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);
        final EditText editTitle = findViewById(R.id.title);
        final EditText editDescription = findViewById(R.id.description);
        Button b_edit = findViewById(R.id.button_edit);
        Button b_delete = findViewById(R.id.button_delete);

        editTitle.setText(category.getTitle());
        editDescription.setText(category.getDescription());



        //add the new item
        b_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String title = editTitle.getText().toString();
                if(TextUtils.isEmpty(title))
                {
                    editTitle.setError("Item name empty");
                    return;
                }
                String description = editDescription.getText().toString();
                if(TextUtils.isEmpty(description))
                {
                    description = "No description";
                }


                //Uploading code
                try
                {
                    new EditItemTask(category.getId(),title,description).execute((Void)null);
                }
                catch (Exception e)
                {
                    Log.e("uploading",""+e.getMessage());
                }
            }
        });
        b_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new MaterialAlertDialogBuilder(EditCategoryActivity.this)
                        .setTitle("DELETE")
                        .setMessage("Are you sure you want to delete this category?")
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
                                new DeleteItemTask(category.getId()).execute((Void)null);
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

    public class EditItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_add_item = base_url+"update_seller_category.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String title;
        private String description;
        private Integer category_id;
        private int success;
        EditItemTask(int category_id, final String title, final String description)
        {
            showProgress(true);
            this.category_id = category_id;
            this.title = title;
            this.description = description;
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("category_id",String.valueOf(category_id)));
            info.add(new BasicNameValuePair("title",title));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("image_type","none"));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_item,"POST",info);
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

                Log.d("editing item", "done...");
                onBackPressed();
            }
            else if(success==-2)
            {
                showProgress(false);
                Log.e("editting item", "error");
                Toast.makeText(getBaseContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class DeleteItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_delete_item = base_url+"delete_seller_category.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int success=0;
        private int category_id;

        DeleteItemTask(int category_id)
        {
            showProgress(true);
            this.category_id = category_id;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("category_id",String.valueOf(category_id)));
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