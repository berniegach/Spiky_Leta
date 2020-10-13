/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Orders;
import com.spikingacacia.leta.ui.orders.OrdersFragment.OnListFragmentInteractionListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MyOrdersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    //////////
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Orders> mValues;
    private List<Orders> itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    public MyOrdersRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context)
    {
        mValues = new ArrayList<>();
        mListener = listener;
        itemsCopy=new ArrayList<>();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_orders, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_orders, parent, false);
        return new ViewHolder(view);*/
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof ViewHolder)
        {
            populateItemRows((ViewHolder) holder, position);
        }
        else if (holder instanceof LoadingViewHolder)
        {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }
    @Override
    public int getItemCount()
    {
        return mValues == null ? 0 : mValues.size();
    }
    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mValues.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(Orders orderItem:itemsCopy)
            {
                if(orderItem.getUsername().toLowerCase().contains(text))
                    mValues.add(orderItem);
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView image;
        public final TextView mOrderView;
        public final TextView mTableView;
        public final TextView mUsernameView;
        public final TextView mDateView;
        public Orders mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            image = view.findViewById(R.id.image);
            mOrderView = (TextView) view.findViewById(R.id.order_number);
            mTableView = (TextView) view.findViewById(R.id.table_number);
            mUsernameView = (TextView) view.findViewById(R.id.username);
            mDateView = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mUsernameView.getText() + "'";
        }
    }
    public  class LoadingViewHolder extends RecyclerView.ViewHolder
    {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView)
        {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

    }
    public void listUpdated(List<Orders> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
    public void listAddProgressBar()
    {
        mValues.add(null);
        notifyDataSetChanged();
    }
    public void listRemoveProgressBar()
    {
        mValues.remove(mValues.size()-1);
        notifyDataSetChanged();
    }
    public void listAddItems(List<Orders> newitems)
    {
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }
    private void populateItemRows(final ViewHolder holder, int position)
    {
        String image_url= LoginActivity.base_url+"src/buyers_pics/";
        holder.mItem = mValues.get(position);
        holder.mOrderView.setText("Order "+mValues.get(position).getOrderNumber());
        int table_number = mValues.get(position).getTableNumber();
        int pre_order = mValues.get(position).getPreOrder();
        if(pre_order == 1 && table_number!= -1)
            holder.mTableView.setText("P-O Table "+table_number);
        else
            holder.mTableView.setText(table_number == -1 ? "Pre - Order" : "Table "+table_number);
        holder.mUsernameView.setText(mValues.get(position).getUsername());
        //format the date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        PrettyTime p = new PrettyTime();
        try
        {
            holder.mDateView.setText(p.format(format.parse(mValues.get(position).getDateAddedLocal())));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }


        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        String url=image_url+String.valueOf(mValues.get(position).getUserId())+'_'+String.valueOf(LoginActivity.getServerAccount().getImageType());
        Glide.with(context).load(url).into(holder.image);
    }


}
