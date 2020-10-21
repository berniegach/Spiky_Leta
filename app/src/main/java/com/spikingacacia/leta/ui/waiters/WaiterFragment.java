/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.waiters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Waiters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WaiterFragment extends Fragment implements WaiterDialogBottomSheet.AddWaiterInterface
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;
    private String TAG = "WaiterFragment";
    private MyWaiterRecyclerViewAdapter myWaiterRecyclerViewAdapter;
    public static List<Waiters> waitersList;
    private ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WaiterFragment()
    {
    }

    @SuppressWarnings("unused")
    public static WaiterFragment newInstance(int columnCount)
    {
        WaiterFragment fragment = new WaiterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_waiter_list, container, false);

        progressBar = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.list);
        Context context = view.getContext();
        mColumnCount = getHorizontalItemCount();
        if (mColumnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        myWaiterRecyclerViewAdapter = new MyWaiterRecyclerViewAdapter(mListener, context);
        recyclerView.setAdapter(myWaiterRecyclerViewAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));

        waitersList = new LinkedList<>();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new WaitersTask().execute((Void)null);
    }
/*
*  implementation of WaiterDialogBottomSheet.java
 */
    @Override
    public void onWaiterAdd(String email)
    {
        new CreateWaiterTask(email).execute((Void)null);
    }

    public interface OnListFragmentInteractionListener
    {
        void onListFragmentInteraction(Waiters item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.waiters_menu, menu);
        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                WaiterDialogBottomSheet.newInstance(WaiterFragment.this).show(getChildFragmentManager(), "dialog");
                //showDialog();
                return true;
            }
        });

        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                MyWaiterRecyclerViewAdapter adapter=(MyWaiterRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                MyWaiterRecyclerViewAdapter adapter=(MyWaiterRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });
    }
    private void showDialog()
    {
        String title = "Enter the Waiter's Email";
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(title)
                .setView(R.layout.item_dialog_waiter)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Dialog dialog1 = (Dialog) dialog;
                        EditText editText = dialog1.findViewById(R.id.edittext);
                        String str = editText.getText().toString();
                        //check if waiter is already added
                        boolean error = false;
                        for(int c=0; c<waitersList.size(); c++)
                            if(str.contentEquals(waitersList.get(c).getEmail()))
                            {
                                Toast.makeText(getContext(),"The waiter is already added", Toast.LENGTH_SHORT).show();
                                editText.setError("Already a waiter");
                                error = true;
                            }
                        if(!error)
                        {
                            new CreateWaiterTask(str).execute((Void)null);
                            dialog.dismiss();
                        }
                    }
                }).create().show();
    }
    private int getHorizontalItemCount()
    {
        int screenSize = getContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 4;

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 3;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 2;

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
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        recyclerView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    public class CreateWaiterTask extends AsyncTask<Void, Void, Boolean>
    {
        String url_add_waiter = base_url + "add_waiter.php";
        private String email;
        private int success;
        private int id=-1;
        private String waiter_name="";
        private JSONParser jsonParser;

        @Override
        protected void onPreExecute()
        {
            showProgress(true);
            super.onPreExecute();
            //check whether the waiter is already available
        }

        CreateWaiterTask(final String name)
        {
            Toast.makeText(getContext(),"Please wait...",Toast.LENGTH_SHORT).show();
            jsonParser = new JSONParser();
            this.email =name;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_id",Integer.toString(LoginActivity.getServerAccount().getId())));
            info.add(new BasicNameValuePair("waiter_username", email));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_waiter,"POST",info);
            Log.d(TAG,""+jsonObject.toString());
            try
            {
                String TAG_SUCCESS = "success";
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    id=jsonObject.getInt("id");
                    waiter_name=jsonObject.getString("waiter_name");
                    return true;
                }
                else
                {
                    String TAG_MESSAGE = "message";
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
            showProgress(false);
            if(successful)
            {

                Log.d("adding new waiter", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                new WaitersTask().execute((Void)null);
            }
            else
            {
                Log.e("adding waiter", "error");
                if(success == -4)
                    Toast.makeText(getContext(),"The runner is already registered.",Toast.LENGTH_SHORT).show();
                else if(success == -1)
                    Toast.makeText(getContext(),"Wrong email. Make sure the user is registered as an order customer first.",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(),"Error adding the runner",Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class WaitersTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_s_waiters = base_url + "get_waiters.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        private List<Waiters> list;
        @Override
        protected void onPreExecute()
        {
            showProgress(true);
            jsonParser = new JSONParser();
            list = new LinkedList<>();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_id",Integer.toString(LoginActivity.getServerAccount().getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_waiters,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("waiters");
                    for(int count=0; count<itemsArrayList.length(); count+=1)
                    {
                        JSONObject json_object_waiters=itemsArrayList.getJSONObject(count);
                        int id=json_object_waiters.getInt("id");
                        String email=json_object_waiters.getString("email");
                        String username=json_object_waiters.getString("username");
                        String image_type = json_object_waiters.getString("image_type");

                        Waiters waiter=new Waiters(id,email,username,0, image_type);
                        list.add(waiter);
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
                Log.e(TAG+"JSON"," waiter"+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean successful)
        {
            showProgress(false);
            if (successful)
            {
                myWaiterRecyclerViewAdapter.listUpdated(list);
                waitersList = list;
            }
            else
            {

            }
        }
    }


}
