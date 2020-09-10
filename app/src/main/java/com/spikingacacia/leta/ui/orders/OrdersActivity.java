package com.spikingacacia.leta.ui.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Orders;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class OrdersActivity extends AppCompatActivity
        implements OrdersFragment.OnListFragmentInteractionListener,
        OrderOverviewFragment.OnFragmentInteractionListener
{
    private int PERMISSION_REQUEST_INTERNET = 20;
    private String fragmentWhich = "overview";
    private String buyerEmail;
    private int orderId;
    private int orderNumber;
    private String dateAdded;
    private String TAG_SUCCESS = "success";
    private String TAG_MESSAGE = "message";
    private String TAG = "OrdersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        int which = getIntent().getIntExtra("which", 0);

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        Fragment fragment = OrdersFragment.newInstance(1, which);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base, fragment, fragmentWhich);
        transaction.commit();
    }

    /**
     * implementation of OrdersFragment.java
     * */
    public void onListFragmentInteraction(Orders item)
    {
        final int format = LoginActivity.getServerAccount().getOrderFormat();
        buyerEmail = item.getUserEmail();
        orderId = item.getId();
        orderNumber = item.getOrderNumber();
        dateAdded = item.getDateAdded();
        setTitle("Order");
        String dateAdded = item.getDateAdded();
        String[] date = dateAdded.split(" ");
        String message = date[0] + ":" + item.getOrderNumber() + ":" + item.getOrderStatus();
        int status = item.getOrderStatus();
        Fragment fragment = OrderOverviewFragment.newInstance(message, format, status == -1 ? 1 : status, item.getOrderStatus(), item.getPreOrder());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base, fragment, "order");
        transaction.addToBackStack("order");
        transaction.commit();
    }

    /**
     * implementation of OrderOverviewFragment.java
     * */
    @Override
    public void onAcceptDecline(int which, int status)
    {
        final int format = LoginActivity.getServerAccount().getOrderFormat();
        int new_status = 1;
        if (which == 1)
        {
            //accept order
            if (status == -1)
            {
                //the order is already paid so skip payment and go straight to inprogress
                new_status = 3;
            } else
                new_status = status += 1;
        } else if (which == 2)
        {
            //decline order
            new_status = 0;
        }
        new BOrdersFormatUpdateTask(new_status).execute((Void) null);
    }

    @Override
    public void gotoMaps(String location)
    {
        try
        {
            String[] location_pieces = location.split(":");
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location_pieces[0] + "," + location_pieces[1]);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(mapIntent);
            }

        } catch (Exception e)
        {
            Toast.makeText(getBaseContext(), "Wrong location", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void gotoPhone(String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        startActivity(intent);
    }
   /* private void sendFirebaseMessage()
    {
        // This registration token comes from the client FCM SDKs.
        String registrationToken = "YOUR_REGISTRATION_TOKEN";

// See documentation on defining a message payload.
        Message message = Message.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken(registrationToken)
                .build();

// Send a message to the device corresponding to the provided
// registration token.
        String response = FirebaseMessaging.getInstance().send(message);
// Response is a message ID string.
        System.out.println("Successfully sent message: " + response);
    }*/

    private class BOrdersFormatUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_order_status = base_url + "update_seller_order.php";
        int status;
        String waiter_email="";
        private JSONParser jsonParser;
        public BOrdersFormatUpdateTask(int status)
        {
            this.status=status;
            if(LoginActivity.getServerAccount().getPersona()==2)
                waiter_email= LoginActivity.getServerAccount().getWaiter_email();
            jsonParser = new JSONParser();
            Log.d(TAG,"WAITER EMAIL"+waiter_email);
            Log.d(TAG,"STATUS "+status);
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
            info.add(new BasicNameValuePair("seller_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("buyer_email",String.valueOf(buyerEmail)));
            info.add(new BasicNameValuePair("waiter_email",waiter_email));
            info.add(new BasicNameValuePair("order_id",String.valueOf(orderId)));
            info.add(new BasicNameValuePair("order_number",String.valueOf(orderNumber)));
            info.add(new BasicNameValuePair("status",String.valueOf(status)));
            info.add(new BasicNameValuePair("update_seller_total","0"));
            info.add(new BasicNameValuePair("m_message",""));
            info.add(new BasicNameValuePair("date_added",dateAdded));
            Log.d(TAG,"info is: "+info.toString());
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
            if (successful)
            {
                //update the order on the list
                String[] date_pieces=dateAdded.split(" ");
                String unique_name=date_pieces[0]+":"+orderNumber+":"+status;
                onBackPressed();
            }
            else
            {

            }
        }
    }


}
