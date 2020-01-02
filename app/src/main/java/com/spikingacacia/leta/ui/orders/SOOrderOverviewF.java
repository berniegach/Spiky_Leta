package com.spikingacacia.leta.ui.orders;

import android.content.Context;
import android.os.Bundle;

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
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.SOrders;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SOOrderOverviewF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SOOrderOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SOOrderOverviewF extends Fragment
{
    private static final String ARG_ORDER = "order";
    private static final String ARG_FORMAT = "order_format";
    private static final String ARG_STATION = "station";
    private String mOrder;
    private int mOrderFormat;
    private int mStation;
    private OnFragmentInteractionListener mListener;

    public SOOrderOverviewF()
    {
        // Required empty public constructor
    }
    public static SOOrderOverviewF newInstance(String order,int format, int station)
    {
        SOOrderOverviewF fragment = new SOOrderOverviewF();
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
        View view= inflater.inflate(R.layout.f_soorder_overview, container, false);
        ProgressBar progressBar=view.findViewById(R.id.progress);
        LinearLayout l_base=view.findViewById(R.id.orders_base);
        TextView t_date=view.findViewById(R.id.date);
        TextView t_username=view.findViewById(R.id.username);
        TextView t_table=view.findViewById(R.id.table);
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
        Iterator iterator= LoginA.sOrdersList.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
            SOrders sOrders=set.getValue();
            int itemId=sOrders.getItemId();
            int order_number=sOrders.getOrderNumber();
            int orderStatus=sOrders.getOrderStatus();
            String orderName=sOrders.getOrderName();
            orderName=orderName.replace("_"," ");
            double price=sOrders.getPrice();
            String dateAdded=sOrders.getDateAdded();
            String[] date=dateAdded.split(" ");
            if(!(date[0]+":"+order_number+":"+orderStatus).contentEquals(mOrder))
                continue;
            username=sOrders.getUsername();
            table=sOrders.getTableNumber();
            if(count==0)
            {
                progressBar.setProgress(orderStatus);
            }
            //add the layouts
            //main layout
            LinearLayout l_main=new LinearLayout(getContext());
            l_main.setWeightSum(10);
            l_main.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            l_main.setOrientation(LinearLayout.HORIZONTAL);
            l_main.setPadding(2,2,2,2);
            //textView for count
            TextView t_count=new TextView(getContext());
            t_count.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            t_count.setText(String.valueOf(count+1));
            t_count.setGravity(Gravity.START);
            //textview for names
            TextView t_names=new TextView(getContext());
            t_names.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,6));
            t_names.setText(orderName);
            t_names.setGravity(Gravity.CENTER);
            //textview for price
            TextView t_price=new TextView(getContext());
            t_price.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,3));
            t_price.setText(String.valueOf(price));
            t_price.setGravity(Gravity.END);
            //add the layouts
            l_main.addView(t_count);
            l_main.addView(t_names);
            l_main.addView(t_price);
            l_base.addView(l_main);
            count+=1;
            total_price+=price;
            date_to_show=dateAdded;
        }
        //textview for total price
        TextView t_total=new TextView(getContext());
        t_total.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t_total.setGravity(Gravity.END);
        t_total.setText("Total: "+String.valueOf(total_price));
        l_base.addView(t_total);
        //set date text
        t_date.setText(date_to_show);
        t_username.setText(username);
        t_table.setText("Table "+table);
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
