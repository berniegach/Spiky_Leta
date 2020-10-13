/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.wallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.Messages;
import com.spikingacacia.leta.ui.database.Receipts;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class MyReceiptRecyclerViewAdapter extends RecyclerView.Adapter<MyReceiptRecyclerViewAdapter.ViewHolder>
{

    private List<Receipts> mValues;
    private List<Receipts> itemsCopy;

    public MyReceiptRecyclerViewAdapter()
    {
        mValues = new LinkedList<>();
        itemsCopy = new LinkedList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);
        Receipts receipts = mValues.get(position);
        String message = receipts.getReceipt()+" Confirmed. You have transferred ksh. "+receipts.getAmount()+" from your wallet to "+receipts.getNames()+" on "+receipts.getTransaction_date();
        holder.mMessageView.setText(message);
        //format the date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        PrettyTime p = new PrettyTime();
        try
        {
            holder.mDateView.setText(p.format(format.parse(mValues.get(position).getDate_added())));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mMessageView;
        public final TextView mDateView;
        public Receipts mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mMessageView = (TextView) view.findViewById(R.id.message);
            mDateView = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(Receipts receipts:itemsCopy)
            {
                if(receipts.getTransaction_date().toLowerCase().contains(text))
                    mValues.add(receipts);
            }
        }
        notifyDataSetChanged();
    }
    public void listUpdated(List<Receipts> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
}