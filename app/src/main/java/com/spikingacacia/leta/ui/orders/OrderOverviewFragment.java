package com.spikingacacia.leta.ui.orders;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.Orders;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.Utils;

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
    private static final String ARG_ORDER_STATUS = "order_status";
    private static final String ARG_STATION = "station";
    private static final String ARG_PRE_ORDER = "pre_order";
    private String mOrder;
    private int mOrderFormat;
    private int mOrderStatus;
    private int mStation;
    private int mPreOrder;
    private OnFragmentInteractionListener mListener;
    //Preferences preferences;

    public OrderOverviewFragment()
    {
        // Required empty public constructor
    }
    public static OrderOverviewFragment newInstance(String order, int format, int station, int status,  int pre_order)
    {
        OrderOverviewFragment fragment = new OrderOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER, order);
        args.putInt(ARG_FORMAT, format);
        args.putInt(ARG_STATION, station);
        args.putInt(ARG_ORDER_STATUS, status);
        args.putInt(ARG_PRE_ORDER, pre_order);
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
            mOrderStatus = getArguments().getInt(ARG_ORDER_STATUS);
            mPreOrder = getArguments().getInt(ARG_PRE_ORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_overview, container, false);


        LinearLayout l_base=view.findViewById(R.id.orders_base);
        TextView t_date=view.findViewById(R.id.date);
        TextView t_username=view.findViewById(R.id.username);
        TextView t_order_number = view.findViewById(R.id.order_number);
        TextView t_table=view.findViewById(R.id.table);
        TextView t_waiter=view.findViewById(R.id.waiter);
        TextView t_status = view.findViewById(R.id.status);
        TextView t_collect_time = view.findViewById(R.id.collect_time);
        TextView t_order_type = view.findViewById(R.id.order_type);
        ImageButton button_location = view.findViewById(R.id.location);
        final ImageButton button_mobile = view.findViewById(R.id.mobile);
        TextView t_delivery_info = view.findViewById(R.id.t_delivery_info);
        ImageView image_qr_code = view.findViewById(R.id.qr_code);
        TextView t_payment_type = view.findViewById(R.id.payment_type);

        //set the buttons listeners
        Button b_accept=view.findViewById(R.id.accept);
        Button b_decline=view.findViewById(R.id.decline);
        button_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.gotoMaps((String)v.getTag());
            }
        });
        button_mobile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mListener!=null)
                    mListener.gotoPhone((String)button_mobile.getTag());
            }
        });

        String username="";
        int table=0;
        int count=0;
        double total_price=0.0;
        String date_to_show="";
        String waiter="";
        String collect_time="";
        int payment_type = -1;
        int i_order_type=0;
        String mobile="";
        String instructions = "";
        String location = "";
        String url_code_start_delivery ="";
        String url_code_end_delivery = "";
        int order_number = 0;
        for (LinkedHashMap.Entry<Integer, Orders> set : OrdersFragment.ordersLinkedHashMap.entrySet())
        {
            Orders orders = set.getValue();
            int itemId = orders.getItemId();
            order_number = orders.getOrderNumber();
            int orderStatus = orders.getOrderStatus();
            String orderName = orders.getOrderName();
            orderName = orderName.replace("_", " ");
            String size = orders.getSize();
            double price = orders.getPrice();
            String dateAdded = orders.getDateAdded();
            String dateAddedLocal = orders.getDateAddedLocal();
            String[] date = dateAdded.split(" ");
            if (!(date[0] + ":" + order_number + ":" + orderStatus).contentEquals(mOrder))
                continue;
            username = orders.getUsername();
            waiter = orders.getWaiter_names();
            table = orders.getTableNumber();
            collect_time = orders.getCollectTime();
            payment_type = orders.getPaymentType();
            i_order_type = orders.getOrderType();
            mobile = orders.getDeliveryMobile();
            instructions = orders.getDeliveryInstructions();
            location = orders.getDeliveryLocation();

            if (mOrderStatus == 3)
            {
                url_code_start_delivery = orders.getUrlCodeStartDelivery();
                url_code_end_delivery = orders.getUrlCodeEndDelivery();
            }
            //add the layouts
            //cardview
            View layout = inflater.inflate(R.layout.order_cardview_layout, null);
            TextView t_count = layout.findViewById(R.id.count);
            TextView t_item = layout.findViewById(R.id.item);
            TextView t_price = layout.findViewById(R.id.price);

            t_count.setText(String.valueOf(count + 1));
            t_item.setText(orderName);
            t_price.setText(size + " @ " + String.valueOf(price));
            l_base.addView(layout);


            count += 1;
            total_price += price;
            date_to_show = dateAddedLocal;
        }
        ((TextView)view.findViewById(R.id.total)).setText("Total "+String.valueOf(total_price));
       // l_base.addView(t_total);
        //set date text
        t_date.setText(date_to_show);
        t_collect_time.setText(collect_time);
        t_order_number.setText("Order "+String.valueOf(order_number));
        t_username.setText(username);
        if(table!=-1)
            t_table.setText("Table "+table);
        t_waiter.setText(" served by "+waiter);
        String[] order_types = new String[]{"In house", "Take away", "Delivery"};
        t_order_type.setText(order_types[i_order_type]);
        if(mPreOrder == 1)
        {
            //check if delivery
            if(i_order_type == 2)
            {
                button_location.setVisibility(View.VISIBLE);
                button_location.setClickable(true);
                button_location.setTag(location);
                button_mobile.setTag(mobile);
                t_delivery_info.setText(instructions);
            }
        }
        if(mOrderStatus == 3)
        {
            if(url_code_start_delivery.length()>10)
            {
                image_qr_code.setVisibility(View.VISIBLE);
                image_qr_code.setImageBitmap(Utils.generateQRCode(url_code_start_delivery));
            }

        }
        if(mOrderStatus == 5)
        {
            button_location.setClickable(false);
            button_mobile.setClickable(false);
        }
        //set order status
        String status;
        switch(mOrderStatus)
        {
            case -3:
                status = "Unpaid";
                break;
            case -2:
                status = "Processing payment";
                break;
            case -1:
                status = "Paid & Pending";
                break;
            case 1:
                status = "UnPaid & Pending";
                break;
            case 2:
                status = "Pending payment";
                break;
            case 3:
                status = "In progress";
                break;
            case 4:
                status = "Delivery";
                break;
            case 5:
                status = "Finished";
                break;
            default:
                status = "";
        }
        t_status.setText(status);
        //payment type
        String[] payments = new String[]{"M-Pesa","Cash"};
        if(payment_type == 0)
            t_payment_type.setText("Paid by "+payments[0]);
        else if(payment_type == 1)
        {
            if(mOrderStatus!=5)
            {
                t_payment_type.setText("TO BE PAID BY CASH");
            }
            else
                t_payment_type.setText("Paid by "+payments[1]);
        }
        //show the respective buttons and change their labels accordingly
        //for pending the buttons remain as they are
        //the order status are
        // -3 for new order, -2 = unpaid, -1 = paid, 0 = deleted, 1 = pending, 2 = ..... until 5 = finished

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
                if(payment_type == 1)
                {
                    b_accept.setText("Cash Collected");
                    b_decline.setVisibility(View.INVISIBLE);
                }
                else if(payment_type == 0)
                {
                    b_accept.setText("Delivered");
                    b_decline.setVisibility(View.INVISIBLE);
                }
            }
            else if(mStation==5)
            {
                b_accept.setVisibility(View.INVISIBLE);
                b_decline.setVisibility(View.INVISIBLE);
            }
        }
        int finalPayment_type = payment_type;
        b_accept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmation")
                        .setMessage("This action cannot be undone.\nAre you sure you want to proceed?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(mListener!=null)
                                    mListener.onAcceptDecline(1, mOrderStatus, finalPayment_type);
                            }
                        }).create().show();

            }
        });
        b_decline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmation")
                        .setMessage("This action cannot be undone.\nAre you sure you want to proceed?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(mListener!=null)
                                    mListener.onAcceptDecline(2, mOrderStatus, finalPayment_type);
                            }
                        }).create().show();

            }
        });
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
        void onAcceptDecline(int which, int status, int payment_type);
        void gotoMaps(String location);
        void gotoPhone(String number);
    }
}
