/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 8/20/20 1:26 PM
 */

package com.spikingacacia.leta.ui.main.dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;


public class DashboardFragment extends Fragment
{
    private TextView t_total_orders_count;
    private TextView t_traffic_count;
    private TextView t_categories_count;
    private TextView t_revenue_count;
    private CardView c_performance;
    private CardView c_traffic;
    private CardView c_categories;
    private OnListFragmentInteractionListener mListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        t_total_orders_count = root.findViewById(R.id.total_orders_count);
        t_traffic_count = root.findViewById(R.id.traffic);
        t_categories_count = root.findViewById(R.id.categories_count);
        t_revenue_count = root.findViewById(R.id.revenue);
        c_performance = root.findViewById(R.id.cardview_performance);
        CardView c_total_orders = root.findViewById(R.id.cardview_total_orders);
        c_traffic = root.findViewById(R.id.cardview_traffic);
        c_categories = root.findViewById(R.id.cardview_cartegories);

        c_performance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onCardviewClicked(3);
            }
        });
        c_total_orders.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onCardviewClicked(4);
            }
        });
        c_traffic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onCardviewClicked(5);
            }
        });
        c_categories.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onCardviewClicked(6);
            }
        });

        return root;
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
        new Data1Task().execute((Void)null);
    }
    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onCardviewClicked(int which);
    }

    private class Data1Task extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_data=base_url+"get_data_1.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int orders_count =0;
        private int traffic_count = 0;
        private Double revenue = 0.0;

        public Data1Task()
        {
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_email", LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_data,"POST",info);
            Log.d("data 1",""+jsonObject.toString());
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    orders_count = jsonObject.getInt("orders_count");
                    traffic_count = jsonObject.getInt("traffic_count");
                    revenue = jsonObject.getDouble("revenue");
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
                t_total_orders_count.setText(String.valueOf(orders_count));
                t_traffic_count.setText(String.valueOf(traffic_count));
                t_revenue_count.setText(String.valueOf(revenue));
            }
            else
            {

            }
        }
    }
}