package com.spikingacacia.leta.ui.orders;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.orders.SOOrderF.OnListFragmentInteractionListener;
import com.spikingacacia.leta.ui.orders.SOOrderC.OrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OrderItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SOOrderRVA extends RecyclerView.Adapter<SOOrderRVA.ViewHolder>
{

    private final List<OrderItem> mValues;
    private List<OrderItem> itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;
    private  int mWhichOrder;
    Preferences preferences;

    public SOOrderRVA(List<OrderItem> items, OnListFragmentInteractionListener listener, Context context, int whichOrder)
    {
        mValues = items;
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mContext=context;
        mWhichOrder=whichOrder;
        //preference
        preferences=new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_soorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);
        holder.mPositionView.setText(mValues.get(position).position);
        holder.mOrderView.setText("Order "+mValues.get(position).orderNumber);
        holder.mTableView.setText("Table "+mValues.get(position).tableNumber);
        holder.mUsernameView.setText(mValues.get(position).username);
        holder.mDateView.setText(mValues.get(position).dateAdded);
        if(!preferences.isDark_theme_enabled())
        {
            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.secondary_background_light));
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
            for(OrderItem orderItem:itemsCopy)
            {
                if(orderItem.username.toLowerCase().contains(text))
                    mValues.add(orderItem);
            }
        }
        notifyDataSetChanged();
    }
    public void notifyChange(int position, int id, int userId, int itemId, int orderNumber, int orderStatus, String orderName, double price, String username, int tableNumber, String dateAdded, String dateChanged)
    {
        SOOrderC content=new SOOrderC(mWhichOrder);
        mValues.add(content.CreateItem(position,id,userId,itemId,orderNumber,orderStatus,orderName,price,username,tableNumber,dateAdded,dateChanged));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mPositionView;
        public final TextView mOrderView;
        public final TextView mTableView;
        public final TextView mUsernameView;
        public final TextView mDateView;
        public OrderItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mPositionView = (TextView) view.findViewById(R.id.position);
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
}
