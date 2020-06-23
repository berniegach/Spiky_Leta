package com.spikingacacia.leta.ui.orders;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.Orders;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderOverviewFragment extends Fragment
{
    private static final String ARG_ORDER = "order";
    private static final String ARG_FORMAT = "order_format";
    private static final String ARG_STATION = "station";
    private String mOrder;
    private int mOrderFormat;
    private int mStation;
    private OnFragmentInteractionListener mListener;
    Preferences preferences;

    public OrderOverviewFragment()
    {
        // Required empty public constructor
    }
    public static OrderOverviewFragment newInstance(String order, int format, int station)
    {
        OrderOverviewFragment fragment = new OrderOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER, order);
        args.putInt(ARG_FORMAT, format);
        args.putInt(ARG_STATION, station);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mOrder = getArguments().getString(ARG_ORDER);
            mOrderFormat = getArguments().getInt(ARG_FORMAT);
            mStation = getArguments().getInt(ARG_STATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_overview, container, false);
        //preference
        preferences=new Preferences(getContext());

        ProgressBar progressBar=view.findViewById(R.id.progress);
        LinearLayout l_base=view.findViewById(R.id.orders_base);
        TextView t_date=view.findViewById(R.id.date);
        TextView t_username=view.findViewById(R.id.username);
        TextView t_table=view.findViewById(R.id.table);
        TextView t_waiter=view.findViewById(R.id.waiter);
        //set the buttons listeners
        Button b_accept=view.findViewById(R.id.accept);
        Button b_decline=view.findViewById(R.id.decline);
        b_accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.onAcceptDecline(1, mStation);
            }
        });
        b_decline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.onAcceptDecline(2, mStation);
            }
        });
        //show the respective buttons and change their labels accordingly
        //for pending the buttons remain as they are
        if(mOrderFormat==1)
        {
            //pending--in progress--delivery--payment--finished
            if(mStation==2)
            {
                b_accept.setText("Ready");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==3)
            {
                b_accept.setText("Delivered");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==4)
            {
                b_accept.setText("Paid");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==5)
            {
                b_accept.setVisibility(View.INVISIBLE);
                b_decline.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            //pending--payment--in progress--delivery--finished
            if(mStation==2)
            {
                b_accept.setText("Paid");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==3)
            {
                b_accept.setText("Ready");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==4)
            {
                b_accept.setText("Delivered");
                b_decline.setVisibility(View.INVISIBLE);
            }
            else if(mStation==5)
            {
                b_accept.setVisibility(View.INVISIBLE);
                b_decline.setVisibility(View.INVISIBLE);
            }
        }
        String username="";
        int table=0;
        int count=0;
        double total_price=0.0;
        String date_to_show="";
        String waiter="";
        Iterator iterator= OrdersFragment.ordersLinkedHashMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Orders>set=(LinkedHashMap.Entry<Integer, Orders>) iterator.next();
            Orders orders =set.getValue();
            int itemId= orders.getItemId();
            int order_number= orders.getOrderNumber();
            int orderStatus= orders.getOrderStatus();
            String orderName= orders.getOrderName();
            orderName=orderName.replace("_"," ");
            double price= orders.getPrice();
            String dateAdded= orders.getDateAdded();
            String[] date=dateAdded.split(" ");
            if(!(date[0]+":"+order_number+":"+orderStatus).contentEquals(mOrder))
                continue;
            username= orders.getUsername();
            waiter= orders.getWaiter_names();
            table= orders.getTableNumber();
            if(count==0)
            {
                progressBar.setProgress(orderStatus);
            }
            //add the layouts
            //cardview
            View layout = inflater.inflate(R.layout.order_cardview_layout,null);
            TextView t_count = layout.findViewById(R.id.count);
            TextView t_item = layout.findViewById(R.id.item);
            TextView t_price = layout.findViewById(R.id.price);

            t_count.setText(String.valueOf(count+1));
            t_item.setText(orderName);
            t_price.setText(String.valueOf(price));
            l_base.addView(layout);


            count+=1;
            total_price+=price;
            date_to_show=dateAdded;
        }
        ((TextView)view.findViewById(R.id.total)).setText("Total "+String.valueOf(total_price));
       // l_base.addView(t_total);
        //set date text
        t_date.setText(date_to_show);
        t_username.setText(username);
        t_table.setText("Table "+table);
        t_waiter.setText(waiter);
        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener
    {
        void onAcceptDecline(int which, int status);
    }
}
