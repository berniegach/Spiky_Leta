package com.spikingacacia.leta.ui.orders;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Orders;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class OrdersFragment extends Fragment
{
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_WHICH_ORDER = "which-order";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private int mWhichOrder=0;
    private  RecyclerView recyclerView;
    private MyOrdersRecyclerViewAdapter myOrdersRecyclerViewAdapter;
    public static LinkedHashMap<Integer,Orders> ordersLinkedHashMap;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrdersFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OrdersFragment newInstance(int columnCount, int whichOrder)
    {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_WHICH_ORDER,whichOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mWhichOrder = getArguments().getInt(ARG_WHICH_ORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        ordersLinkedHashMap = new LinkedHashMap<>();

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myOrdersRecyclerViewAdapter = new MyOrdersRecyclerViewAdapter(mListener,getContext(),mWhichOrder);
            recyclerView.setAdapter(myOrdersRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new OrdersTask().execute((Void)null);
    }

    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Orders item);
    }
    private class OrdersTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_s_orders = base_url + "get_seller_orders.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<Orders> ordersList;
        private LinkedHashMap<String,Orders> uniqueOrderLinkedHashMap;
        @Override
        protected void onPreExecute()
        {
            Log.d("BORDERS: ","starting....");
            ordersList = new LinkedList<>();
            uniqueOrderLinkedHashMap = new LinkedHashMap<>();
            jsonParser = new JSONParser();
            ordersLinkedHashMap.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email", LoginActivity.getServerAccount().getEmail()));
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
                        int user_id = jsonObjectNotis.getInt("user_id");
                        String user_email=jsonObjectNotis.getString("user_email");
                        int item_id=jsonObjectNotis.getInt("item_id");
                        int order_number=jsonObjectNotis.getInt("order_number");
                        int order_status=jsonObjectNotis.getInt("order_status");
                        if(mWhichOrder == 1 && order_status == -1)
                            ; //this is a pending order which has been paid
                        else if(mWhichOrder!=order_status)
                            continue;

                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");
                        String date_added_local=jsonObjectNotis.getString("date_added_local");
                        String item=jsonObjectNotis.getString("item");
                        String size = jsonObjectNotis.getString("size");
                        double selling_price=jsonObjectNotis.getDouble("price");
                        String username=jsonObjectNotis.getString("username");
                        String waiter_names=jsonObjectNotis.getString("waiter_names");
                        int table_number=jsonObjectNotis.getInt("table_number");
                        int pre_order = jsonObjectNotis.getInt("pre_order");
                        String collect_time = jsonObjectNotis.getString("collect_time");
                        int payment_type = jsonObjectNotis.getInt("payment_type");
                        int order_type = jsonObjectNotis.getInt("order_type");
                        String delivery_mobile = jsonObjectNotis.getString("delivery_mobile");
                        String delivery_instructions = jsonObjectNotis.getString("delivery_instructions");
                        String delivery_location = jsonObjectNotis.getString("delivery_location");
                        String url_code_start_delivery = jsonObjectNotis.getString("url_code_start_delivery");
                        String url_code_end_delivery = jsonObjectNotis.getString("url_code_end_delivery");

                        Orders orders =new Orders(id,user_id,user_email,item_id,order_number,order_status,item,size,selling_price, username,waiter_names,table_number, pre_order, collect_time, payment_type, order_type,
                                delivery_mobile, delivery_instructions, delivery_location, url_code_start_delivery, url_code_end_delivery,
                                date_added,date_changed, date_added_local);
                        ordersLinkedHashMap.put(id,orders);
                        ordersList.add(orders);
                        String[] date_pieces=date_added.split(" ");
                        String unique_name=date_pieces[0]+":"+order_number+":"+order_status;
                        uniqueOrderLinkedHashMap.put(unique_name,orders);
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
                List<Orders> unique_order= new ArrayList<>();
                Iterator iterator = uniqueOrderLinkedHashMap.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<String, Orders> value = ( LinkedHashMap.Entry<String, Orders>) iterator.next();
                    unique_order.add(value.getValue());
                }
                myOrdersRecyclerViewAdapter.listUpdated(unique_order);
            }
            else
            {

            }
        }
    }
}
