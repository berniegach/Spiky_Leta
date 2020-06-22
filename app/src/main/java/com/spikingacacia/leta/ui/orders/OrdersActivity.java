package com.spikingacacia.leta.ui.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.database.Orders;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;

public class OrdersActivity extends AppCompatActivity
    implements  OrdersFragment.OnListFragmentInteractionListener,
        SOOrderOverviewF.OnFragmentInteractionListener
{
    private String fragmentWhich="overview";
    private String buyerEmail;
    private int orderId;
    private int orderNumber;
    private String dateAdded;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="OrdersActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
         int which = getIntent().getIntExtra("which",0);
         String title = getIntent().getStringExtra("title");
        setTitle(title);

        Fragment fragment= OrdersFragment.newInstance(1,which);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.commit();
    }

    /**
     * implementation of OrdersFragment.java
     * */
    public void onListFragmentInteraction(Orders item)
    {
        final int format= serverAccount.getOrderFormat();
        buyerEmail =item.getUserId();
        orderId=item.getId();
        orderNumber=item.getOrderNumber();
        dateAdded=item.getDateAdded();
        setTitle("Order");
        String dateAdded=item.getDateAdded();
        String[] date=dateAdded.split(" ");
        String message=date[0]+":"+item.getOrderNumber()+":"+item.getOrderStatus();
        Fragment fragment=SOOrderOverviewF.newInstance(message, format, item.getOrderStatus());
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"order");
        transaction.addToBackStack("order");
        transaction.commit();
    }
    /**
     * implementation of SOOrderOverviewF.java
     * */
    @Override
    public void onAcceptDecline(int which, int status)
    {
        final int format= serverAccount.getOrderFormat();
        int new_status=1;
        if(status==1)
            new_status=which==1?2:0;
        else
            new_status=status+=1;
        //accept order is 1 while decline is 2
        new BOrdersFormatUpdateTask( new_status ).execute((Void)null);
    }

    private class BOrdersFormatUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        int status;
        int waiter_id=0;
        private JSONParser jsonParser;
        public BOrdersFormatUpdateTask(int status)
        {
            this.status=status;
            if(serverAccount.getPersona()==1)
                waiter_id= serverAccount.getWaiter_id();
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("SORDERUPDATE: ","starting....");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_id",Integer.toString(serverAccount.getId())));
            info.add(new BasicNameValuePair("buyer_id",String.valueOf(buyerEmail)));
            info.add(new BasicNameValuePair("waiter_id",Integer.toString(waiter_id)));
            info.add(new BasicNameValuePair("order_id",String.valueOf(orderId)));
            info.add(new BasicNameValuePair("order_number",String.valueOf(orderNumber)));
            info.add(new BasicNameValuePair("status",String.valueOf(status)));
            info.add(new BasicNameValuePair("date_added",dateAdded));
            // making HTTP request
            String url_update_order_status = base_url + "update_seller_order.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_order_status,"POST",info);
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {

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
            Log.d("SORDERUPDATE: ","finished....");
            if (successful)
            {
                //set the order in the orderlist
                Iterator iterator=null;//LoginA.sOrdersList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, Orders>set=(LinkedHashMap.Entry<Integer, Orders>) iterator.next();
                    Orders bOrders=set.getValue();
                    int o_id=bOrders.getId();
                    if(o_id==orderId)
                    {
                        if(status==0)
                        {
                            //remove the order
                            iterator.remove();
                        }
                        else
                        {
                            if(serverAccount.getPersona()==1)
                                bOrders.setWaiter_names(serverAccount.getWaiter_names());
                            bOrders.setOrderStatus(status);
                            //sOrdersList.put(orderId,bOrders);
                        }
                    }
                }
               finish();
                startActivity(getIntent());
            }
            else
            {

            }
        }
    }

}
