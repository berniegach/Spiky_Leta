package com.spikingacacia.leta.ui.main.orders;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.OrdersService;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.Orders;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersOverviewFragment extends Fragment
{
    private LinkedHashMap<Integer, Orders> ordersLinkedHashMap;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "orders_overview_f";
    private  String default_notification_channel_id = "default";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Button bOrderFormat;
    private TextView tMainCount;
    private int[] pendingCount = new int[]{0,0};
    private int[] inProgressCount=new int[]{0,0};
    private int[] deliveryCount=new int[]{0,0};
    private int[] paymentCount=new int[]{0,0};
    private int[] finishedCount=new int[]{0,0};
    private LinearLayout lPending;
    private LinearLayout lInProgress;
    private LinearLayout lDelivery;
    private LinearLayout lPayment;
    private LinearLayout lFinished;
    private TextView tPendingCount;
    private TextView tInProgressCount;
    private TextView tDeliveryCount;
    private TextView tPaymentCount;
    private TextView tFinishedCount;
    private TextView tInProgressName;
    private TextView tDeliveryName;
    private TextView tPaymentName;
    private int countToShow=0;
    Preferences preferences;

    public OrdersOverviewFragment()
    {
        // Required empty public constructor
    }
    public static OrdersOverviewFragment newInstance(String param1, String param2)
    {
        OrdersOverviewFragment fragment = new OrdersOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_orders_overview, container, false);
        //preference
        preferences=new Preferences(getContext());

        //layouts
        lPending = ((LinearLayout)view.findViewById(R.id.pending));
        lInProgress = ((LinearLayout)view.findViewById(R.id.inprogress));
        lDelivery = ((LinearLayout)view.findViewById(R.id.delivery));
        lPayment = ((LinearLayout)view.findViewById(R.id.payment));
       lFinished = ((LinearLayout)view.findViewById(R.id.finished));
        //views
        bOrderFormat =view.findViewById(R.id.order_format);
        tMainCount=view.findViewById(R.id.main_count);
        tPendingCount=view.findViewById(R.id.pending_count);
        tInProgressCount=view.findViewById(R.id.inprogress_count);
        tDeliveryCount=view.findViewById(R.id.delivery_count);
        tPaymentCount=view.findViewById(R.id.payment_count);
        tFinishedCount=view.findViewById(R.id.finished_count);
        //names
        tInProgressName=view.findViewById(R.id.inprogress_name);
        tDeliveryName=view.findViewById(R.id.delivery_name);
        tPaymentName=view.findViewById(R.id.payment_name);
        //on click listeners
        bOrderFormat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeOrderFormat();
            }
        });
       onClickListeners();
        ordersLinkedHashMap = new LinkedHashMap<>();
        if(LoginActivity.getServerAccount().getPersona()==2)
        {
            bOrderFormat.setVisibility(View.GONE);
            ((CardView)view.findViewById(R.id.cardview_finished)).setVisibility(View.GONE);
        }
        new OrdersTask().execute((Void)null);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.soorders_menu, menu);

        final String[] strings_format=new String[]{"Pending", "Payment", "In Progress", "Delivery", "Finished"};
        final MenuItem menu_station=menu.findItem(R.id.station);
        menu_station.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                new AlertDialog.Builder(getContext())
                        .setItems(strings_format, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                for(int count=0; count<=4; count+=1)
                                {
                                    if(count==i)
                                    {
                                        final int[] counts=new int[]{ pendingCount[1], paymentCount[1],inProgressCount[1], deliveryCount[1], finishedCount[1]};
                                        countToShow=i;
                                        preferences.setOrder_format_to_show_count(i);
                                        tMainCount.setText(String.valueOf(counts[count]));
                                    }
                                    else
                                    {
                                       ;//
                                    }
                                }
                            }
                        }).create().show();
                return true;
            }
        });
    }
    void changeOrderFormat()
    {
        new AlertDialog.Builder(getContext())
                .setItems(new String[]{"Pay last ", "Pay first"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        new UpdateOrderFormatTask(i+1).execute((Void)null);

                    }
                }).create().show();
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
        void onChoiceClicked(int id);
    }
    private void onClickListeners()
    {
        final int format= LoginActivity.getServerAccount().getOrderFormat();
        tInProgressCount.setText(String.valueOf(paymentCount[1]));
        tDeliveryCount.setText(String.valueOf(inProgressCount[1]));
        tPaymentCount.setText(String.valueOf(deliveryCount[1]));

        lPending.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pendingCount[1]==0)
                {
                    Snackbar.make(tMainCount,"Empty",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onChoiceClicked(1);
            }
        });
        lPayment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (paymentCount[1]==0 )
                {
                    Snackbar.make(tMainCount,"Empty",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onChoiceClicked(2);
            }
        });
        lInProgress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (inProgressCount[1]==0)
                {
                    Snackbar.make(tMainCount,"Empty",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onChoiceClicked(3);
            }
        });
        lDelivery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (deliveryCount[1]==0)
                {
                    Snackbar.make(tMainCount,"Empty",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onChoiceClicked(4);
            }
        });
        lFinished.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (finishedCount[1]==0)
                {
                    Snackbar.make(tMainCount,"Empty",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mListener!=null)
                    mListener.onChoiceClicked(5);
            }
        });
    }
    private void setCounts()
    {
        /*int format= LoginActivity.serverAccount.getOrderFormat();
        List<String> order_numbers=new ArrayList<>();
        Iterator iterator= ordersLinkedHashMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Orders>set=(LinkedHashMap.Entry<Integer, Orders>) iterator.next();
            Orders bOrders=set.getValue();
            int order_number=bOrders.getOrderNumber();
            int order_status=bOrders.getOrderStatus();
            String date_added=bOrders.getDateAdded();
            String[] date_pieces=date_added.split(" ");
            String unique_name=date_pieces[0]+":"+order_number+":"+order_status;
            order_numbers.add(unique_name);
        }
        Set<String> unique=new HashSet<>(order_numbers);
        List<String> order_counts=new ArrayList<>(unique);
        Iterator<String> iterator_2=order_counts.iterator();
        while(iterator_2.hasNext())
        {
            String unique_name=iterator_2.next();
            String[] pieces=unique_name.split(":");
            if(pieces[2].contentEquals("1") || pieces[2].contentEquals("-1"))
                pendingCount[1]+=1;

            else if(pieces[2].contentEquals("2"))
                inProgressCount[1]+=1;
            else if(pieces[2].contentEquals("3"))
                deliveryCount[1]+=1;
            else if(pieces[2].contentEquals("4"))
                paymentCount[1]+=1;

            else if(pieces[2].contentEquals("5"))
                finishedCount[1]+=1;

        }*/
        pendingCount[1] = preferences.getPaid_count() + preferences.getPending_count(); //pending orders are a combination of those paid and those which are unpaid
        inProgressCount[1] = preferences.getIn_progress_count();
        deliveryCount[1] = preferences.getDelivery_count();
        paymentCount[1] = preferences.getPayment_count();
        finishedCount[1] = preferences.getFinished_count();

        try
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    updateGui();
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG,"run on ui thread");
        }

    }

    private void updateGui()
    {
        boolean countChanged = false;
        countToShow=preferences.getOrder_format_to_show_count();

        if(pendingCount[0]!=pendingCount[1])
        {
            tPendingCount.setText(String.valueOf(pendingCount[1]));
            pendingCount[0] = pendingCount[1];
            countChanged = true;
        }
        if(finishedCount[0] != finishedCount[1])
        {
            tFinishedCount.setText(String.valueOf(finishedCount[1]));
            finishedCount[0] = finishedCount[1];
            countChanged = true;
        }
        //set the counts
        if(inProgressCount[0]!=inProgressCount[1])
        {
            tInProgressCount.setText(String.valueOf(inProgressCount[1]));
            inProgressCount[0]=inProgressCount[1];
            countChanged = true;
        }
        if(deliveryCount[0]!=deliveryCount[1])
        {
            tDeliveryCount.setText(String.valueOf(deliveryCount[1]));
            deliveryCount[0] = deliveryCount[1];
            countChanged = true;
        }
        if(paymentCount[0] != paymentCount[1])
        {
            tPaymentCount.setText(String.valueOf(paymentCount[1]));
            paymentCount[0] = paymentCount[1];
        }


        final int[] counts=new int[]{ pendingCount[1], inProgressCount[1], deliveryCount[1], paymentCount[1], finishedCount[1]};
        for(int count=0; count<=4; count+=1)
        {
            if(count==countToShow && countChanged)
            {
                tMainCount.setText(String.valueOf(counts[count]));
            }
            else
            {
                ;//
            }
        }


    }

    public class UpdateOrderFormatTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_order_format = base_url + "update_seller_order_format.php";
        private JSONParser jsonParser;
        final int format;
        UpdateOrderFormatTask(int format)

        {
            Log.d("settings","update started...");
            this.format=format;
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginActivity.getServerAccount().getId())));
            info.add(new BasicNameValuePair("order_format", Integer.toString(format)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_order_format,"POST",info);
            try
            {
                String TAG_SUCCESS = "success";
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String TAG_MESSAGE = "message";
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings","finished");
            if(success)
            {
                int previous_format= LoginActivity.getServerAccount().getOrderFormat();
                LoginActivity.getServerAccount().setOrderFormat(format);
                Snackbar.make(lFinished,"Format updated",Snackbar.LENGTH_SHORT).show();
                updateGui();
            }
            else
            {
                Snackbar.make(lFinished,"Error updating format",Snackbar.LENGTH_SHORT).show();
            }

        }
    }
    private class OrdersTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_orders = base_url + "get_seller_orders_count.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email",LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_orders,"POST",info);
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    pendingCount[1] = 0;
                    itemsArrayList=jsonObject.getJSONArray("orders");
                    for(int c=0; c<itemsArrayList.length(); c+=1)
                    {
                        JSONObject jsonObjectNotis=itemsArrayList.getJSONObject(c);
                        int order_status=jsonObjectNotis.getInt("order_status");
                        int count = jsonObjectNotis.getInt("count");
                        //-2 = unpaid, -1 = paid, 0 = deleted, 1 = pending, 2 = ..... until 5 = finished
                        switch(order_status)
                        {
                            //this is pending orders that have been paid
                            case -1:
                                pendingCount[1]+=count;
                                break;
                            //these are pending orders that have not been paid
                            case 1:
                                pendingCount[1]+=count;
                            case 2:
                                paymentCount[1]= count;
                                break;
                            case 3:
                               inProgressCount[1]=count;
                                break;
                            case 4:
                               deliveryCount[1] = count;
                                break;
                            case 5:
                               finishedCount[1] = count;
                                break;
                        }

                    }
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean successful) {

            if (successful)
            {
                updateGui();
            }

        }
    }
}
