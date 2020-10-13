/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.messages;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Messages;

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
 */
public class MessagesFragment extends Fragment
{
    private RecyclerView recyclerView;
   private MyMessageRecyclerViewAdapter myMessageRecyclerViewAdapter;
    public MessagesFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MessagesFragment newInstance(int columnCount)
    {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myMessageRecyclerViewAdapter = new MyMessageRecyclerViewAdapter();
            recyclerView.setAdapter(myMessageRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new MessagesTask().execute((Void)null);
    }

    private class MessagesTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_b_notifications = base_url + "get_notifications.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<Messages> list;

        @Override
        protected void onPreExecute()
        {
            Log.d("BNOTIFICATIONS: ","starting....");
            super.onPreExecute();
            jsonParser = new JSONParser();
            list = new LinkedList<>();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email", LoginActivity.getServerAccount().getEmail()));

            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_b_notifications,"POST",info);
            Log.d("bNotis",""+jsonObject.toString());
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("notis");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int persona=jsonObjectNotis.getInt("persona");
                        int status=jsonObjectNotis.getInt("status");
                        String message=jsonObjectNotis.getString("message");
                        String date_added=jsonObjectNotis.getString("date_added");
                        Messages oneMessage=new Messages(id,persona,status,message,date_added);
                        list.add(oneMessage);

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
                myMessageRecyclerViewAdapter.listUpdated(list);
                recyclerView.scrollToPosition(myMessageRecyclerViewAdapter.getItemCount()-1);
            }
            else
            {

            }
        }
    }
}