package com.spikingacacia.leta.ui.orders;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.Orders;
import com.spikingacacia.leta.ui.orders.OrdersFragment.OnListFragmentInteractionListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OrderItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyOrdersRecyclerViewAdapter extends RecyclerView.Adapter<MyOrdersRecyclerViewAdapter.ViewHolder>
{

    private List<Orders> mValues;
    private List<Orders> itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;
    private  int mWhichOrder;
    Preferences preferences;

    public MyOrdersRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context, int whichOrder)
    {
        mValues = new ArrayList<>();
        mListener = listener;
        itemsCopy=new ArrayList<>();
        itemsCopy = new ArrayList<>();
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
        holder.mOrderView.setText("Order "+mValues.get(position).getOrderNumber());
        holder.mTableView.setText("Table "+mValues.get(position).getTableNumber());
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
        public final TextView mPositionView;
        public final TextView mOrderView;
        public final TextView mTableView;
        public final TextView mUsernameView;
        public final TextView mDateView;
        public Orders mItem;

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
    public void listUpdated(List<Orders> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
}
