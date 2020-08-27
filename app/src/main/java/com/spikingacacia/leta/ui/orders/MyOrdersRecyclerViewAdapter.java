package com.spikingacacia.leta.ui.orders;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class MyOrdersRecyclerViewAdapter extends RecyclerView.Adapter<MyOrdersRecyclerViewAdapter.ViewHolder>
{

    private List<Orders> mValues;
    private List<Orders> itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;
    private  int mWhichOrder;
    public MyOrdersRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context, int whichOrder)
    {
        mValues = new ArrayList<>();
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy = new ArrayList<>();
        mContext=context;
        mWhichOrder=whichOrder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        String image_url= LoginActivity.base_url+"src/buyers_pics/";
        holder.mItem = mValues.get(position);
        holder.mOrderView.setText("Order "+mValues.get(position).getOrderNumber());
        int table_number = mValues.get(position).getTableNumber();
        holder.mTableView.setText(table_number == -1 ? "Pre - Order" : "Table "+table_number);
        holder.mUsernameView.setText(mValues.get(position).getUsername());
        //format the date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        PrettyTime p = new PrettyTime();
        try
        {
            holder.mDateView.setText(p.format(format.parse(mValues.get(position).getDateAdded())));
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
        Glide.with(mContext).load(url).into(holder.image);
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
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
    public void notifyChange(int position, int id, int userId, int itemId, int orderNumber, int orderStatus, String orderName, double price, String username, int tableNumber, String dateAdded, String dateChanged)
    {

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
    public void listUpdated(List<Orders> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
}
