package com.spikingacacia.leta.ui.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.SOrders;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.sOrdersList;
import static com.spikingacacia.leta.ui.LoginA.sellerAccount;

public class SOOrdersA extends AppCompatActivity
    implements SOOverviewF.OnFragmentInteractionListener, SOOrderF.OnListFragmentInteractionListener,
        SOOrderOverviewF.OnFragmentInteractionListener
{
    private String url_update_order_status=base_url+"update_seller_order.php";
    private String fragmentWhich="overview";
    private int buyerId;
    private int orderId;
    private int orderNumber;
    private String dateAdded;
    private int mWhichOrder=0;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="SOOrdersA";
    private JSONParser jsonParser;
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_soorders);
        jsonParser=new JSONParser();

        //set actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Orders");
        //preference
        preferences=new Preferences(getBaseContext());
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            toolbar.setTitleTextColor(getResources().getColor(R.color.text_light));
            toolbar.setPopupTheme(R.style.AppThemeLight_PopupOverlayLight);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.getContext().setTheme(R.style.AppThemeLight_AppBarOverlayLight);
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.main).setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }
        //set the first base fragment
        Fragment fragment=SOOverviewF.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"");
        transaction.commit();
        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Orders");
                else if(count==1)
                    setTitle(fragmentWhich);
            }
        });
    }
    /**
     * implementation of SOOverviewF.java*/
    @Override
    public void onChoiceClicked(int which)
    {
        mWhichOrder=which;
        final int format= sellerAccount.getOrderFormat();
        switch(which)
        {
            case 1:
                fragmentWhich="Pending";
                break;
            case 2:
                fragmentWhich= format==1?"In Progress":"Payment";
                break;
            case 3:
                fragmentWhich= format==1?"Delivery":"In Progress";
                break;
            case 4:
                fragmentWhich= format==1?"Payment":"Delivery";
                break;
            case 5:
                fragmentWhich="Finished";
        }
        setTitle(fragmentWhich);
        Fragment fragment=SOOrderF.newInstance(1,which);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();
    }
    /**
     * implementation of SOOrderF.java
     * */
    public void onListFragmentInteraction(SOOrderC.OrderItem item)
    {
        final int format= sellerAccount.getOrderFormat();
        buyerId=item.userId;
        orderId=item.id;
        orderNumber=item.orderNumber;
        dateAdded=item.dateAdded;
        setTitle("Order");
        String dateAdded=item.dateAdded;
        String[] date=dateAdded.split(" ");
        String message=date[0]+":"+item.orderNumber+":"+item.orderStatus;
        Fragment fragment=SOOrderOverviewF.newInstance(message, format, item.orderStatus);
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
        final int format= sellerAccount.getOrderFormat();
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
        public BOrdersFormatUpdateTask(int status)
        {
            this.status=status;
            if(sellerAccount.getPersona()==1)
                waiter_id=sellerAccount.getWaiter_id();
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
            info.add(new BasicNameValuePair("seller_id",Integer.toString(sellerAccount.getId())));
            info.add(new BasicNameValuePair("buyer_id",String.valueOf(buyerId)));
            info.add(new BasicNameValuePair("waiter_id",Integer.toString(waiter_id)));
            info.add(new BasicNameValuePair("order_id",String.valueOf(orderId)));
            info.add(new BasicNameValuePair("order_number",String.valueOf(orderNumber)));
            info.add(new BasicNameValuePair("status",String.valueOf(status)));
            info.add(new BasicNameValuePair("date_added",dateAdded));
            // making HTTP request
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
                Iterator iterator=LoginA.sOrdersList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
                    SOrders bOrders=set.getValue();
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
                            if(sellerAccount.getPersona()==1)
                                bOrders.setWaiter_names(sellerAccount.getWaiter_names());
                            bOrders.setOrderStatus(status);
                            sOrdersList.put(orderId,bOrders);
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
