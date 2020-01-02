package com.spikingacacia.leta.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.SMessages;
import com.spikingacacia.leta.ui.database.SOrders;

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
import java.util.Set;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.sMessagesList;
import static com.spikingacacia.leta.ui.LoginA.sOrdersList;
import static com.spikingacacia.leta.ui.LoginA.sellerAccount;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SMenuF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SMenuF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SMenuF extends Fragment {
    private String url_get_s_notifications=base_url+"get_seller_notifications.php";
    private String url_get_s_orders=base_url+"get_seller_orders.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="SMenuF";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private JSONParser jsonParser;
    private TextView tOrdersCount;
    private TextView tMessagesCount;
    private int ordersCount=0;
    private int messagesCount=0;

    public SMenuF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SMenuF.
     */
    // TODO: Rename and change types and number of parameters
    public static SMenuF newInstance(String param1, String param2) {
        SMenuF fragment = new SMenuF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_smenu, container, false);
        tOrdersCount=view.findViewById(R.id.orders_count);
        tMessagesCount=view.findViewById(R.id.messages_count);
        //inventory
        ((LinearLayout)view.findViewById(R.id.inventory)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(1);
            }
        });
        //orders
        ((LinearLayout)view.findViewById(R.id.orders)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(2);
            }
        });
        //reports
        ((LinearLayout)view.findViewById(R.id.reports)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(3);
            }
        });
        //messages
        ((LinearLayout)view.findViewById(R.id.messages)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(4);
            }
        });
        //settings
        ((LinearLayout)view.findViewById(R.id.settings)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(5);
            }
        });
        final Handler handler=new Handler();
        final Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                if(ordersCount!=getOrdersCounts())
                {
                    ordersCount=getOrdersCounts();
                    tOrdersCount.setText(String.valueOf(ordersCount));
                    Log.d(TAG,"orders count changed");
                }
                if(messagesCount!=sMessagesList.size())
                {
                    messagesCount=sMessagesList.size();
                    tMessagesCount.setText(String.valueOf(messagesCount));
                    Log.d(TAG,"messages count changed");
                }
            }
        };
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(true)
                    {
                        sleep(10000);
                        refreshOrders();
                        refreshMessages();
                        handler.post(runnable);
                    }
                }
                catch (InterruptedException e)
                {
                    Log.e(TAG,"error sleeping "+e.getMessage());
                }
            }
        };
        thread.start();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.smenu, menu);
        final MenuItem logout=menu.findItem(R.id.action_logout);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(mListener!=null)
                    mListener.onLogOut();
                return true;
            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //set the counts
        tOrdersCount.setText(String.valueOf(getOrdersCounts()));
        tMessagesCount.setText(String.valueOf(LoginA.sMessagesList.size()));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onMenuClicked(int id);
        void onLogOut();
    }
    private int getOrdersCounts()
    {
        List<String> order_numbers=new ArrayList<>();
        Iterator iterator= sOrdersList.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
            SOrders bOrders=set.getValue();
            int order_number=bOrders.getOrderNumber();
            String date_added=bOrders.getDateAdded();
            String[] date_pieces=date_added.split(" ");
            String unique_name=date_pieces[0]+":"+order_number;
            order_numbers.add(unique_name);
        }
        Set<String> unique=new HashSet<>(order_numbers);
        return unique.size();
    }
    /**
     * Following code will get the sellers notifications
     * The returned infos are id,  classes, messages, dateadded.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private void refreshMessages()
    {
        //getting columns list
        List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
        info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
        // making HTTP request
        JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_notifications,"POST",info);
        //Log.d("sNotis",""+jsonObject.toString());
        try
        {
            JSONArray notisArrayList=null;
            int success=jsonObject.getInt(TAG_SUCCESS);
            if(success==1)
            {
                notisArrayList=jsonObject.getJSONArray("notis");
                for(int count=0; count<notisArrayList.length(); count+=1)
                {
                    JSONObject jsonObjectNotis=notisArrayList.getJSONObject(count);
                    int id=jsonObjectNotis.getInt("id");
                    int classes=jsonObjectNotis.getInt("classes");
                    String message=jsonObjectNotis.getString("messages");
                    String date=jsonObjectNotis.getString("dateadded");
                    SMessages oneSMessage=new SMessages(id,classes,message,date);
                    sMessagesList.put(String.valueOf(id),oneSMessage);
                }
            }
            else
            {
                String message=jsonObject.getString(TAG_MESSAGE);
                Log.e(TAG_MESSAGE,""+message);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON",""+e.getMessage());
        }
    }
    /**
     * Following code will get the sellers orders
     * The returned infos are id, userId, itemId, orderNumber, orderStatus, orderName, price, dateAdded, dateChanged
     * * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private void refreshOrders()
    {
        //getting columns list
        List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
        info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
        // making HTTP request
        JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_orders,"POST",info);
        //Log.d("sItems",""+jsonObject.toString());
        try
        {
            JSONArray itemsArrayList=null;
            int success=jsonObject.getInt(TAG_SUCCESS);
            if(success==1)
            {
                itemsArrayList=jsonObject.getJSONArray("items");
                for(int count=0; count<itemsArrayList.length(); count+=1)
                {
                    JSONObject jsonObjectNotis=itemsArrayList.getJSONObject(count);
                    int id=jsonObjectNotis.getInt("id");
                    int user_id=jsonObjectNotis.getInt("userid");
                    int item_id=jsonObjectNotis.getInt("itemid");
                    int order_number=jsonObjectNotis.getInt("ordernumber");
                    int orderstatus=jsonObjectNotis.getInt("orderstatus");
                    String dateadded=jsonObjectNotis.getString("dateadded");
                    String datechanged=jsonObjectNotis.getString("datechanged");
                    String item=jsonObjectNotis.getString("item");
                    double selling_price=jsonObjectNotis.getDouble("sellingprice");
                    String username=jsonObjectNotis.getString("username");
                    int table_number=jsonObjectNotis.getInt("table_number");

                    SOrders sOrders=new SOrders(id,user_id,item_id,order_number,orderstatus,item,selling_price, username,table_number,dateadded,datechanged);
                    sOrdersList.put(id,sOrders);
                }
            }
            else
            {
                String message=jsonObject.getString(TAG_MESSAGE);
                Log.e(TAG_MESSAGE,""+message);
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON",""+e.getMessage());
        }
    }
}
