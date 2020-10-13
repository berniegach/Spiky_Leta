/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.wallet;

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
import com.spikingacacia.leta.ui.database.Receipts;
import com.spikingacacia.leta.ui.main.messages.MyMessageRecyclerViewAdapter;

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
public class ReceiptsFragment extends Fragment
{
    private RecyclerView recyclerView;
   private MyReceiptRecyclerViewAdapter myReceiptRecyclerViewAdapter;
    public ReceiptsFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReceiptsFragment newInstance(int columnCount)
    {
        ReceiptsFragment fragment = new ReceiptsFragment();
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
            myReceiptRecyclerViewAdapter = new MyReceiptRecyclerViewAdapter();
            recyclerView.setAdapter(myReceiptRecyclerViewAdapter);
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
        private String url_get_b_notifications = base_url + "get_b2c_receipts.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<Receipts> list;

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
            try
            {
                JSONArray notisArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    notisArrayList=jsonObject.getJSONArray("items");
                    for(int count=0; count<notisArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String amount = jsonObjectNotis.getString("amount");
                        String mobile = jsonObjectNotis.getString("mobile");
                        String receipt = jsonObjectNotis.getString("receipt");
                        String transaction_date = jsonObjectNotis.getString("transaction_date");
                        String names=jsonObjectNotis.getString("names");
                        String date_added=jsonObjectNotis.getString("date_added");

                        Receipts receipts = new Receipts(id, amount, mobile, receipt, transaction_date, names, date_added);
                        list.add(receipts);

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
                myReceiptRecyclerViewAdapter.listUpdated(list);
                recyclerView.scrollToPosition(myReceiptRecyclerViewAdapter.getItemCount()-1);
            }
            else
            {

            }
        }
    }
}