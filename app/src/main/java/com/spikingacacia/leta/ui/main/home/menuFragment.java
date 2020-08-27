package com.spikingacacia.leta.ui.main.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;
import com.spikingacacia.leta.ui.waiters.WaitersActivity;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A fragment representing a list of Items.
 */
public class menuFragment extends Fragment
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewMenu;
    public static  MymenuRecyclerViewAdapter mymenuRecyclerViewAdapter;
    private MymenuCategoryRecyclerViewAdapter mymenuCategoryRecyclerViewAdapter;
    private static  OnListFragmentInteractionListener mListener;
    private String TAG = "menuF";

    public menuFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static menuFragment newInstance()
    {
        menuFragment fragment = new menuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        RecyclerView recyclerViewCategories = view.findViewById(R.id.list_categories);
        mymenuCategoryRecyclerViewAdapter = new MymenuCategoryRecyclerViewAdapter(mListener, getContext());
        recyclerViewCategories.setAdapter(mymenuCategoryRecyclerViewAdapter);

        recyclerViewMenu = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (getHorizontalItemCount()<=1)
        {
            recyclerViewMenu.setLayoutManager(new LinearLayoutManager(context));
        } else
        {
            recyclerViewMenu.setLayoutManager(new GridLayoutManager(context, getHorizontalItemCount()));
        }
        mymenuRecyclerViewAdapter = new MymenuRecyclerViewAdapter(mListener, getContext(), getChildFragmentManager());
        recyclerViewMenu.setAdapter(mymenuRecyclerViewAdapter);
        recyclerViewMenu.addItemDecoration(new SpacesItemDecoration(16));
        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        new CategoriesTask().execute((Void)null);
        new MenuTask().execute((Void)null);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_menu, menu);

        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                //onAddNewItem();
                if(mListener!=null)
                    mListener.onAddNewItemClicked();
                return false;
            }
        });
        if(LoginActivity.getServerAccount().getPersona() ==2)
            add.setVisible(false);

        // Associate searchable configuration with the SearchView
        SearchView searchView =  (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                MymenuRecyclerViewAdapter adapter=( MymenuRecyclerViewAdapter) recyclerViewMenu.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                MymenuRecyclerViewAdapter adapter=( MymenuRecyclerViewAdapter) recyclerViewMenu.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });

    }


    public interface OnListFragmentInteractionListener
    {
        void onAddNewItemClicked();
        void onEditItemClicked(DMenu dMenu);

    }
    private int getHorizontalItemCount()
    {
        int screenSize = getContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 3;

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 2;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 1;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
            default:
                return 1;
        }
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            int pos = parent.getChildLayoutPosition(view);
            int items = getHorizontalItemCount();

            // Add top margin only for the first item to avoid double space between items
            if (pos < items) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }



    private class CategoriesTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        private List<Categories> list;
        @Override
        protected void onPreExecute()
        {
            Log.d("SCATEGORIES: ","starting....");
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
            String url_get_s_categories = base_url + "get_categories.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_categories,"POST",info);
            Log.d("sCategories",""+jsonObject.toString());
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    categoriesArrayList=jsonObject.getJSONArray("categories");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        Categories categories =new Categories(id,title,description,image_type,date_added,date_changed);
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
        protected void onPostExecute(final Boolean successful) {

            if (successful)
            {
                mymenuCategoryRecyclerViewAdapter.listUpdated(list);
            }
            else
            {

            }
        }
    }
    public static void editItem(DMenu dMenu)
    {
        if(mListener!=null)
            mListener.onEditItemClicked(dMenu);
    }
    public static class MenuTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_s_items = base_url + "get_seller_items.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<DMenu>list;
        @Override
        protected void onPreExecute()
        {
            Log.d("SITEMS: ","starting....");
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
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_items,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("items");
                    for(int count=0; count<itemsArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=itemsArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int category_id=jsonObjectNotis.getInt("category_id");
                        int group_id;
                        try
                        {
                            group_id=jsonObjectNotis.getInt("group_id");
                        }
                        catch (Exception e)
                        {
                            group_id = -1;
                        }
                        String linked_items = jsonObjectNotis.getString("linked_items");
                        String item=jsonObjectNotis.getString("item");
                        String description=jsonObjectNotis.getString("description");
                        String sizes = jsonObjectNotis.getString("sizes");
                        String prices = jsonObjectNotis.getString("prices");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        DMenu dMenu =new DMenu(id,category_id,group_id,linked_items, item,description,sizes, prices,image_type,date_added,date_changed);
                        list.add(dMenu);
                        MainActivity.menuLinkedHashMap.put(id,dMenu);

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


            if (successful)
            {
                mymenuRecyclerViewAdapter.listUpdated(list);
            }
            else
            {

            }
        }
    }

}