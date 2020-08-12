package com.spikingacacia.leta.ui.main.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Transactions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

public class WalletActivity extends AppCompatActivity
{
    private String TAG = "wallet_a";
    private Double amount = 0.0;
    private double commision = 12;
    private String currencyCode="";
    public static List<Transactions> transactionsList;
    private TextView t_amount;
    private TextView t_commision;
    private Button b_available;
    private Button b_total;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setTitle("Wallet");
        t_amount= findViewById(R.id.amount);
        t_commision = findViewById(R.id.commision);
        b_available = findViewById(R.id.b_available);
        b_total = findViewById(R.id.b_total);
        Button b_withdraw = findViewById(R.id.b_withdraw);
        Button b_transaction = findViewById(R.id.b_transactions);

        b_available.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                ((Button)v).setTextColor(getResources().getColor(android.R.color.white));
                b_total.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                b_total.setTextColor(getResources().getColor(android.R.color.black));
                t_commision.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                t_commision.setText("-"+commision+"% commision fee");
                t_amount.setText(String.format("%s %.2f",currencyCode,amount-(commision/100*amount)));
            }
        });
        b_total.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                ((Button)v).setTextColor(getResources().getColor(android.R.color.white));
                b_available.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                b_available.setTextColor(getResources().getColor(android.R.color.black));
                t_commision.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                t_commision.setText("+"+commision+"% commision fee");
                t_amount.setText(String.format("%s %.2f",currencyCode,amount));
            }
        });
        thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        new PaymentsTask().execute((Void) null);
                        sleep(60000);
                    }
                } catch (InterruptedException e)
                {
                    Log.e(TAG, "error sleeping " + e.getMessage());
                }
            }
        };
        transactionsList = new LinkedList<>();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        formCurrencyCode();
        thread.start();

    }
    @Override
    public void onBackPressed()
    {
        thread.interrupt();
        super.onBackPressed();
    }


    //to retrieve currency code
    private void formCurrencyCode()
    {
        String[] location_pieces = serverAccount.getLocation().split(",");
        if(location_pieces.length==4)
            currencyCode=getCurrencyCode(location_pieces[3]);
    }
    private void updateGui()
    {
        b_available.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        b_available.setTextColor(getResources().getColor(android.R.color.white));
        b_total.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        b_total.setTextColor(getResources().getColor(android.R.color.black));
        t_commision.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        t_commision.setText("-"+commision+"% commision fee");
        t_amount.setText(String.format("%s %.2f",currencyCode,amount-(commision/100*amount)));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WalletGraphFragment fragment = new WalletGraphFragment();
        fragmentTransaction.add(R.id.graph, fragment);
        fragmentTransaction.commit();
    }

    private String getCurrencyCode(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
    }
    private class PaymentsTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_payments=base_url+"get_m_sales.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<Transactions> transactionsListLocal;
        public PaymentsTask()
        {
            jsonParser = new JSONParser();
            transactionsListLocal = new LinkedList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));

            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_payments,"POST",info);
            try
            {
                int success=jsonObject.getInt(TAG_SUCCESS);
                JSONArray jsonArray=null;
                if(success==1)
                {
                    jsonArray = jsonObject.getJSONArray("items");
                    Log.d(TAG,jsonArray.toString());
                    for (int count = 0; count < jsonArray.length(); count += 1)
                    {
                        JSONObject jsonObjectItems = jsonArray.getJSONObject(count);
                        int id = jsonObjectItems.getInt("id");
                        String order_number = jsonObjectItems.getString("order_number");
                        String order_date_added = jsonObjectItems.getString("order_date_added");
                        String balance = jsonObjectItems.getString("balance");
                        String log_type = jsonObjectItems.getString("log_type");
                        String log = jsonObjectItems.getString("log");
                        String mobile_number = jsonObjectItems.getString("mobile_number");
                        String date_added = jsonObjectItems.getString("date_added");

                        Transactions transactions = new Transactions(id,order_number,order_date_added,balance,log_type,log,mobile_number,date_added);
                        transactionsListLocal.add(transactions);
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
        protected void onPostExecute(final Boolean successful)
        {

            if (successful)
            {
                if(transactionsListLocal.size()!=transactionsList.size())
                {
                    transactionsList = transactionsListLocal;
                    amount = Double.valueOf(transactionsList.get(transactionsList.size()-1).getBalance());
                    updateGui();
                }

            }

        }
    }
}