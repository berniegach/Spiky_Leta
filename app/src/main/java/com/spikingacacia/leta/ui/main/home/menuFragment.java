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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.database.Groups;
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
public class menuFragment extends Fragment implements ItemEditOptionsDialogFragment.EventListener,
        ItemEditOptionsDialogFragment.UpdateListener,
        MymenuRecyclerViewAdapter.OptionsListener,
        LinkedItemListDialogFragment.UpdateListener
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewGroups;
    private RecyclerView recyclerViewMenu;
    public static  MymenuRecyclerViewAdapter mymenuRecyclerViewAdapter;
    private MymenuCategoryRecyclerViewAdapter mymenuCategoryRecyclerViewAdapter;
    private MymenuGroupRecyclerViewAdapter mymenuGroupRecyclerViewAdapter;
    private static  OnListFragmentInteractionListener mListener;
    private String TAG = "menuF";
    private ChipGroup chipGroupCategeories;
    private ChipGroup chipGroupGroups;
    private static List<DMenu>list;

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
        //RecyclerView recyclerViewCategories = view.findViewById(R.id.list_categories);
        chipGroupCategeories = view.findViewById(R.id.chip_group_category);
        //RecyclerView recyclerViewGroups = view.findViewById(R.id.list_groups);
        chipGroupGroups = view.findViewById(R.id.chip_group_group);
        mymenuCategoryRecyclerViewAdapter = new MymenuCategoryRecyclerViewAdapter(mListener, getContext());
        mymenuGroupRecyclerViewAdapter = new MymenuGroupRecyclerViewAdapter(mListener, getContext());
        //recyclerViewCategories.setAdapter(mymenuCategoryRecyclerViewAdapter);
        //recyclerViewGroups.setAdapter(mymenuGroupRecyclerViewAdapter);
        Button b_add_category = view.findViewById(R.id.add_category);
        Button b_add_group = view.findViewById(R.id.add_groups);

        recyclerViewMenu = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (getHorizontalItemCount()<=1)
        {
            recyclerViewMenu.setLayoutManager(new LinearLayoutManager(context));
        } else
        {
            recyclerViewMenu.setLayoutManager(new GridLayoutManager(context, getHorizontalItemCount()));
        }
        mymenuRecyclerViewAdapter = new MymenuRecyclerViewAdapter(mListener, getContext(), getChildFragmentManager(),  this);
        recyclerViewMenu.setAdapter(mymenuRecyclerViewAdapter);
        recyclerViewMenu.addItemDecoration(new SpacesItemDecoration(16));
        b_add_category.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.onAddNewCategory();
            }
        });
        b_add_group.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.onAddNewGroup();
            }
        });
        chipGroupCategeories.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId)
            {
                Chip chip = chipGroupCategeories.findViewById( chipGroupCategeories.getCheckedChipId() );
                if(chip != null)
                {
                    int category_id = (int)chip.getTag();
                    mymenuRecyclerViewAdapter.filterCategory(category_id);
                }
                else
                    mymenuRecyclerViewAdapter.filterCategory(0);
            }
        });
        new CategoriesTask().execute((Void)null);
        new GroupsTask().execute((Void)null);
        new MenuTask().execute((Void)null);
        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        ///TODO: find out why ondettach is being called after oncreaviewview after navigating back to the fragment
       // mListener = null;
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

/*
*    implementation of LinkedItemListDialogFragment
 */
    @Override
    public void onLinkedItemUpdateDone(int menu_id, String linked_items)
    {
        for(int c=0; c<list.size(); c++)
        {
            DMenu dMenu = list.get(c);
            if(dMenu.getId() == menu_id)
                dMenu.setLinkedItems(linked_items);
        }
        mymenuRecyclerViewAdapter.listUpdated(list);

    }
    /*
     *    implementation of ItemEditOptionsDialogFragment
     */
    @Override
    public void onOptionsMenuSelected(DMenu dMenu, int menu_position, List<DMenu> dMenuList)
    {
        ItemEditOptionsDialogFragment.newInstance(dMenu, menu_position, this, dMenuList,this).show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onLinkItem(DMenu dMenu, int menu_index, List<DMenu> dMenuList)
    {
        LinkedItemListDialogFragment.newInstance(dMenu,menu_index,dMenuList,this).show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onEditItemClicked(DMenu dMenu)
    {
        if(mListener!=null)
            mListener.onEditItemClicked(dMenu);
    }

    @Override
    public void onItemAvailailabilityChanged(int menu_id, boolean changed)
    {
        for(int c=0; c<list.size(); c++)
        {
            DMenu dMenu = list.get(c);
            if(dMenu.getId() == menu_id)
                dMenu.setAvailable(changed);
        }
        mymenuRecyclerViewAdapter.listUpdated(list);

    }


    public interface OnListFragmentInteractionListener
    {
        void onAddNewItemClicked();
        void onEditItemClicked(DMenu dMenu);
        void onAddNewCategory();
        void onEditCategory(Categories category);
        void onAddNewGroup();
        void onEditGroup(Groups group);

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
    private void addCategoryChipLayouts(List<Categories>list)
    {
        for(int c=0; c<list.size(); c++)
        {
            Categories categories =list.get(c);
            Chip chip = new Chip(getContext());
            chip.setText(categories.getTitle());
            chip.setTag(categories.getId());
            chip.setClickable(true);
            chip.setCheckable(true);
            chipGroupCategeories.addView(chip);
            chip.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if(mListener!=null)
                        mListener.onEditCategory(categories);
                    return false;
                }
            });
        }
    }
    private void addGroupChipLayouts(List<Groups>list)
    {
        for(int c=0; c<list.size(); c++)
        {
            Groups groups =list.get(c);
            Chip chip = new Chip(getContext());
            chip.setText(groups.getTitle());
            chip.setTag(groups.getId());
            chip.setClickable(true);
            chip.setCheckable(true);
            chipGroupGroups.addView(chip);
            chip.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if(mListener!=null)
                        mListener.onEditGroup(groups);
                    return false;
                }
            });
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
            String url_get_s_categories = base_url + "get_seller_categories.php";
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
                    categoriesArrayList=jsonObject.getJSONArray("groups");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int category_id=jsonObjectNotis.getInt("category_id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        Groups groups =new Groups(id,category_id,title,description,image_type,date_added,date_changed);
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
                        boolean available = jsonObjectNotis.getInt("available") == 1;
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        DMenu dMenu =new DMenu(id,category_id,group_id,linked_items, item,description,sizes, prices,image_type,available,date_added,date_changed);
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