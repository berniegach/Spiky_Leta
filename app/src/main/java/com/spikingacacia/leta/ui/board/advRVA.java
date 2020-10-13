/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.board;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.board.advF.OnListFragmentInteractionListener;
import com.spikingacacia.leta.ui.board.AdsC.AdItem;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AdItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class advRVA extends RecyclerView.Adapter<advRVA.ViewHolder>
{

    private final List<AdItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    Preferences preferences;
    Context mContext;


    public advRVA(List<AdItem> items, OnListFragmentInteractionListener listener, Context context)
    {
        mValues = items;
        mListener = listener;
        mContext=context;
        preferences=new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_adv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mImageView.setImageBitmap(mValues.get(position).bitmap);
        holder.mViewsView.setText(mValues.get(position).views+" views");
        holder.mLikesView.setText(mValues.get(position).likes+ " likes");
        holder.mCommentsView.setText(mValues.get(position).comments+ " comments");
        //format the date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        PrettyTime p = new PrettyTime();
        try
        {
            holder.mDateView.setText(p.format(format.parse(mValues.get(position).date)));
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
                    mListener.onAdClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public final TextView mViewsView;
        public final TextView mLikesView;
        public final TextView mCommentsView;
        public final TextView mDateView;
        public AdItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mViewsView = (TextView) view.findViewById(R.id.views);
            mLikesView = (TextView) view.findViewById(R.id.likes);
            mCommentsView = (TextView) view.findViewById(R.id.comments);
            mDateView = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
    public void notifyChange(int id, String title, Bitmap bitmap, String content, int views, int likes, int comments, String date)
    {
        for(int c=0; c<mValues.size(); c++)
            if(mValues.get(c).id.contentEquals(String.valueOf(id)))
                return;
        AdsC content1=new AdsC();
        mValues.add(content1.createItem(String.valueOf(id), title, bitmap, content, String.valueOf(views), String.valueOf(likes), String.valueOf(comments), date));
        Collections.sort(mValues, new Comparator<AdItem>()
        {
            @Override
            public int compare(AdItem o1, AdItem o2)
            {
                return Integer.parseInt(o2.id)-Integer.parseInt(o1.id);
            }
        });
        notifyDataSetChanged();
    }
    public void add_ads(int id, String title, Bitmap bitmap, String content, int views, int likes, int comments, String date)
    {
        for(int c=0; c<mValues.size(); c++)
            if(mValues.get(c).id.contentEquals(String.valueOf(id)))
                return;
        AdsC content1=new AdsC();
        AdItem dummyItem=content1.createItem(String.valueOf(id), title, bitmap, content, String.valueOf(views), String.valueOf(likes), String.valueOf(comments), date);
        mValues.add(dummyItem);
        Collections.sort(mValues, new Comparator<AdItem>()
        {
            @Override
            public int compare(AdItem o1, AdItem o2)
            {
                return Integer.parseInt(o2.id)-Integer.parseInt(o1.id);
            }
        });
        notifyDataSetChanged();
    }
    public void clearData() {
        mValues.clear(); // clear list
        notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }


}
