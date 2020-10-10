/*
 * Created by Benard Gachanja on 10/10/20 7:11 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/10/20 7:11 PM
 */

package com.spikingacacia.leta.ui.main.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.chip.Chip;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.Groups;
import com.spikingacacia.leta.ui.main.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class ArrangeMenuActivity extends AppCompatActivity
{
    private LinearLayout chipGroupCategeories;
    private LinearLayout chipGroupGroups;
    private ProgressBar progressBar;
    private View mainView;
    private int tasksCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_menu2);
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main_view);
        chipGroupCategeories = findViewById(R.id.chip_group_category);
        chipGroupGroups = findViewById(R.id.chip_group_group);
        showProgress(true);
        new CategoriesTask().execute((Void)null);
        new GroupsTask().execute((Void)null);
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    private void addCategoryChipLayouts(List<Categories>list)
    {
        chipGroupCategeories.removeAllViews();
        for(int c=0; c<list.size(); c++)
        {
            Categories categories =list.get(c);
            Chip chip = new Chip(this);
            chip.setText(categories.getTitle());
            chip.setTag(categories.getIdIndex());
            //chip.setClickable(true);
            //chip.setCheckable(true);
            chipGroupCategeories.addView(chip);
            chip.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    // Create a new ClipData.
                    // This is done in two steps to provide clarity. The convenience method
                    // ClipData.newPlainText() can create a plain text ClipData in one step.

                    // Create a new ClipData.Item from the ImageView object's tag
                    ClipData.Item item = new ClipData.Item(String.valueOf((Integer) v.getTag()));

                    // Create a new ClipData using the tag as a label, the plain text MIME type, and
                    // the already-created item. This will create a new ClipDescription object within the
                    // ClipData, and set its MIME type entry to "text/plain"
                    ClipData dragData = new ClipData(
                            String.valueOf((Integer)v.getTag()),
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                            item);

                    // Instantiates the drag shadow builder.
                    View.DragShadowBuilder myShadow = new MyDragShadowBuilder(chip);

                    // Starts the drag

                    v.startDrag(dragData,  // the data to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local data
                            0          // flags (not currently used, set to 0)
                    );
                    return false;
                }
            });
            chip.setOnDragListener(new View.OnDragListener()
            {
                @Override
                public boolean onDrag(View v, DragEvent event)
                {
                    // Defines a variable to store the action type for the incoming event
                    final int action = event.getAction();

                    // Handles each of the expected events
                    switch(action) {

                        case DragEvent.ACTION_DRAG_STARTED:

                            // Determines if this View can accept the dragged data
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {


                                // returns true to indicate that the View can accept the dragged data.
                                return true;

                            }

                            // Returns false. During the current drag and drop operation, this View will
                            // not receive events again until ACTION_DRAG_ENDED is sent.
                            return false;

                        case DragEvent.ACTION_DRAG_ENTERED:

                            return true;

                        case DragEvent.ACTION_DRAG_LOCATION:

                            // Ignore the event
                            return true;

                        case DragEvent.ACTION_DRAG_EXITED:
                            //
                            return true;

                        case DragEvent.ACTION_DROP:

                            // Gets the item containing the dragged data
                            ClipData.Item item = event.getClipData().getItemAt(0);

                            // Gets the text data from the item.
                            String draggedItemId = (String) item.getText();
                            String droppedOnItemId = String.valueOf((Integer)v.getTag());

                            getViewDraggedOverCategories(draggedItemId, droppedOnItemId);
                            return true;

                        case DragEvent.ACTION_DRAG_ENDED:
                            return true;

                        // An unknown action type was received.
                        default:
                            Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                            break;
                    }

                    return false;
                }
            });
        }

    }
    private void getViewDraggedOverCategories(String draggedItemId, String droppedOnItemId)
    {
        for(int c=0; c<chipGroupCategeories.getChildCount(); c++)
        {
            View view = chipGroupCategeories.getChildAt(c);
            String id = String.valueOf((Integer)view.getTag());
            if(id.contentEquals(droppedOnItemId))
            {
                new SwapCategoriesIdsTask(draggedItemId, droppedOnItemId).execute((Void)null);
                break;
            }
        }
    }
    private void getViewDraggedOverGroups(String draggedItemId, String droppedOnItemId)
    {
        for(int c=0; c<chipGroupGroups.getChildCount(); c++)
        {
            View view = chipGroupGroups.getChildAt(c);
            String id = String.valueOf((Integer)view.getTag());
            if(id.contentEquals(droppedOnItemId))
            {
                new SwapGroupsIdsTask(draggedItemId, droppedOnItemId).execute((Void)null);
                break;
            }
        }
    }
    private void addGroupChipLayouts(List<Groups>list)
    {
        chipGroupGroups.removeAllViews();
        for(int c=0; c<list.size(); c++)
        {
            Groups groups =list.get(c);
            Chip chip = new Chip(this);
            chip.setText(groups.getTitle());
            chip.setTag(groups.getIdIndex());
            //chip.setClickable(true);
            //chip.setCheckable(true);
            chipGroupGroups.addView(chip);
            chip.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    // Create a new ClipData.
                    // This is done in two steps to provide clarity. The convenience method
                    // ClipData.newPlainText() can create a plain text ClipData in one step.

                    // Create a new ClipData.Item from the ImageView object's tag
                    ClipData.Item item = new ClipData.Item(String.valueOf((Integer) v.getTag()));

                    // Create a new ClipData using the tag as a label, the plain text MIME type, and
                    // the already-created item. This will create a new ClipDescription object within the
                    // ClipData, and set its MIME type entry to "text/plain"
                    ClipData dragData = new ClipData(
                            String.valueOf((Integer)v.getTag()),
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                            item);

                    // Instantiates the drag shadow builder.
                    View.DragShadowBuilder myShadow = new MyDragShadowBuilder(chip);

                    // Starts the drag

                    v.startDrag(dragData,  // the data to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local data
                            0          // flags (not currently used, set to 0)
                    );
                    return false;
                }
            });
            chip.setOnDragListener(new View.OnDragListener()
            {
                @Override
                public boolean onDrag(View v, DragEvent event)
                {
                    // Defines a variable to store the action type for the incoming event
                    final int action = event.getAction();

                    // Handles each of the expected events
                    switch(action) {

                        case DragEvent.ACTION_DRAG_STARTED:

                            // Determines if this View can accept the dragged data
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {


                                // returns true to indicate that the View can accept the dragged data.
                                return true;

                            }

                            // Returns false. During the current drag and drop operation, this View will
                            // not receive events again until ACTION_DRAG_ENDED is sent.
                            return false;

                        case DragEvent.ACTION_DRAG_ENTERED:

                            return true;

                        case DragEvent.ACTION_DRAG_LOCATION:

                            // Ignore the event
                            return true;

                        case DragEvent.ACTION_DRAG_EXITED:
                            //
                            return true;

                        case DragEvent.ACTION_DROP:

                            // Gets the item containing the dragged data
                            ClipData.Item item = event.getClipData().getItemAt(0);

                            // Gets the text data from the item.
                            String draggedItemId = (String) item.getText();
                            String droppedOnItemId = String.valueOf((Integer)v.getTag());

                            getViewDraggedOverGroups(draggedItemId, droppedOnItemId);
                            return true;

                        case DragEvent.ACTION_DRAG_ENDED:
                            return true;

                        // An unknown action type was received.
                        default:
                            Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                            break;
                    }

                    return false;
                }
            });

        }
    }
    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
             int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth() / 2;

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight() / 2;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }
    private class CategoriesTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<Categories> list;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            list = new ArrayList<>();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email", LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            String url_get_s_categories = base_url + "get_seller_categories.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_categories,"POST",info);
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    MainActivity.categoriesLinkedHashMap.clear();
                    categoriesArrayList=jsonObject.getJSONArray("categories");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int id_index=jsonObjectNotis.getInt("id_index");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        Categories categories =new Categories(id, id_index, title,description,image_type,date_added,date_changed);
                        list.add(categories);
                        MainActivity.categoriesLinkedHashMap.put(id,categories);
                    }
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
            tasksCounter+=1;
            if(tasksCounter == 2)
            {
                showProgress(false);
                tasksCounter = 0;
            }
            if (successful)
            {
                //mymenuCategoryRecyclerViewAdapter.listUpdated(list);
                addCategoryChipLayouts(list);

            }
            else
            {

            }
        }
    }
    private class GroupsTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        private List<Groups> list;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            list = new ArrayList<>();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email",LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            String url_get_s_categories = base_url + "get_seller_groups.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_categories,"POST",info);
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    MainActivity.groupsLinkedHashMap.clear();
                    categoriesArrayList=jsonObject.getJSONArray("groups");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int id_index=jsonObjectNotis.getInt("id_index");
                        int category_id=jsonObjectNotis.getInt("category_id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        Groups groups =new Groups(id,id_index,category_id,title,description,image_type,date_added,date_changed);
                        list.add(groups);
                        MainActivity.groupsLinkedHashMap.put(id,groups);
                    }
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
            tasksCounter+=1;
            if(tasksCounter == 2)
            {
                showProgress(false);
                tasksCounter = 0;
            }
            if (successful)
            {
                //mymenuGroupRecyclerViewAdapter.listUpdated(list);
                addGroupChipLayouts(list);
            }
            else
            {

            }
        }
    }
    private class SwapCategoriesIdsTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String id_1;
        private String id_2;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            showProgress(true);
            super.onPreExecute();
        }
        public SwapCategoriesIdsTask(String id_1, String id_2)
        {
            this.id_1 = id_1;
            this.id_2 = id_2;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id_1", id_1));
            info.add(new BasicNameValuePair("id_2", id_2));
            // making HTTP request
            String url = base_url + "swap_categories_ids.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url,"POST",info);
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
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
                showProgress(true);
                new CategoriesTask().execute((Void)null);
                new GroupsTask().execute((Void)null);
            }
            else
            {

            }
        }
    }
    private class SwapGroupsIdsTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String id_1;
        private String id_2;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            showProgress(true);
            super.onPreExecute();
        }
        public SwapGroupsIdsTask(String id_1, String id_2)
        {
            this.id_1 = id_1;
            this.id_2 = id_2;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id_1", id_1));
            info.add(new BasicNameValuePair("id_2", id_2));
            // making HTTP request
            String url = base_url + "swap_groups_ids.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url,"POST",info);
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
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
                showProgress(true);
                new CategoriesTask().execute((Void)null);
                new GroupsTask().execute((Void)null);
            }
            else
            {

            }
        }
    }
}