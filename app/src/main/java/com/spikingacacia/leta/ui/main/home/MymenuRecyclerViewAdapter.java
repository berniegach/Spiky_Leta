package com.spikingacacia.leta.ui.main.home;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.database.DMenu;

import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.main.home.menuFragment.*;


public class MymenuRecyclerViewAdapter extends RecyclerView.Adapter<MymenuRecyclerViewAdapter.ViewHolder>
{
    private List<DMenu> mValues;
    private List<DMenu>itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MymenuRecyclerViewAdapter(OnListFragmentInteractionListener listener)
    {
        mListener = listener;
        mValues = new LinkedList<>();
        itemsCopy = new LinkedList<>();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        String image_url= base_url+"src/items_pics/";
        holder.mItem = mValues.get(position);
        holder.mItemView.setText(mValues.get(position).getItem());
        holder.mDescriptionView.setText(mValues.get(position).getDescription());
        holder.mPriceView.setText(String.valueOf(mValues.get(position).getSellingPrice()));

        // image
        String url=image_url+String.valueOf(mValues.get(position).getId())+'_'+String.valueOf(mValues.get(position).getImageType());
        holder.image.setImageUrl(url, imageLoader);
        holder.mView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(holder.image.getImageAlpha()==20)
                {
                    holder.image.setImageAlpha(255);
                    holder.mItemView.setAlpha((float)0.4);
                    holder.mDescriptionView.setAlpha((float)0.0);
                }
                else
                {
                    holder.image.setImageAlpha(20);
                    holder.mItemView.setAlpha((float)1.0);
                    holder.mDescriptionView.setAlpha((float)1.0);
                }
                return false;
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
        public final NetworkImageView image;
        public final TextView mItemView;
        public final TextView mDescriptionView;
        public final TextView mPriceView;
        //public final ImageButton mEditButton;
        public DMenu mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            image = view.findViewById(R.id.image);
            mItemView = (TextView) view.findViewById(R.id.item);
            mDescriptionView = view.findViewById(R.id.description);
            mPriceView = (TextView) view.findViewById(R.id.price);
            //mEditButton = view.findViewById(R.id.edit);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mItemView.getText() + "'";
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
            for(DMenu item:itemsCopy)
            {
                if(item.getItem().toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void listUpdated(List<DMenu> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
}