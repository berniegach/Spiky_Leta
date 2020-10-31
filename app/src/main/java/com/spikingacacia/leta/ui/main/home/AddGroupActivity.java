/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.home;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.main.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class AddGroupActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_add_group);
        //views
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);
        final ChipGroup chipGroup = findViewById(R.id.chip_group);
        final EditText editTitle = findViewById(R.id.title);
        final EditText editDescription = findViewById(R.id.description);
        Button button_add = findViewById(R.id.button_add);

        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            Chip chip = new Chip(AddGroupActivity.this);
            chip.setText(categories.getTitle());
            chip.setTag(categories.getId());
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.colorButtonBackgroundTint_1);
            chip.setCheckedIconTintResource(R.color.colorIcons);
            chipGroup.addView(chip);
        }




        //add the new item
        button_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Chip chip = chipGroup.findViewById( chipGroup.getCheckedChipId() );
                if(chip == null)
                {
                    Toast.makeText(AddGroupActivity.this,"Category needed",Toast.LENGTH_SHORT).show();
                    return;
                }
                int category_id = (int)chip.getTag();
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
                    new CreateItemTask(category_id,title,description).execute((Void)null);
                }
                catch (Exception e)
                {
                    Log.e("uploading",""+e.getMessage());
                }
            }
        });
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }

    public class CreateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_add_item = base_url+"create_seller_group.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String title;
        private String description;
        private Integer category_id;
        private String image_type;
        private Bitmap bitmap;
        private  int insert_id;
        private int success;
        CreateItemTask(int category_id,final String title, final String description)
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
                onBackPressed();
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