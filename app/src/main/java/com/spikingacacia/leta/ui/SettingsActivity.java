package com.spikingacacia.leta.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.ServerAccount;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

public class SettingsActivity extends AppCompatActivity
{
    private UpdateAccount updateTask;
    public static boolean settingsChanged;
    public static ServerAccount tempServerAccount;
    static private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ///
        tempServerAccount =new ServerAccount();
        tempServerAccount = serverAccount;
        updateTask=new UpdateAccount();
        settingsChanged=false;
        context=this;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            EditTextPreference preference_est=findPreference("username");
            EditTextPreference preference_est_type=findPreference("email");
            final Preference preference_password=findPreference("password");
            final SeekBarPreference pref_order_radius=findPreference("order_radius");
            final Preference preference_tables=findPreference("number_of_tables");
            final EditTextPreference mpesa_till_number_preference = findPreference("mpesa_code");
            final Preference preference_subscription=findPreference("subscription");
            //check if we have the waiter logged on
            if(serverAccount.getPersona()==2)
            {
                preference_est.setEnabled(false);
                pref_order_radius.setVisible(false);
                //preference_tables.setVisible(false);
                mpesa_till_number_preference.setEnabled(false);
            }
            //feedback preference click listener

            preference_est.setText(serverAccount.getUsername());
            preference_est.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String name = o.toString();
                    tempServerAccount.setUsername(name);
                    settingsChanged=true;
                    preference.setTitle(name);
                    return false;
                }
            });
            //you cannot change the email

            preference_est_type.setText(serverAccount.getPersona()==1?serverAccount.getEmail(): serverAccount.getWaiter_email());
            //order radius

            pref_order_radius.setValue(serverAccount.getOrderRadius());
            pref_order_radius.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    int range=(int)newValue;
                    pref_order_radius.setValue(range);
                    tempServerAccount.setOrderRadius(range);
                    settingsChanged=true;
                    return false;
                }
            });
            //number of tables change

            /*preference_tables.setSummary(String.valueOf(serverAccount.getNumberOfTables()));
            preference_tables.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    final AlertDialog dialog;
                    AlertDialog.Builder builderPass=new AlertDialog.Builder(context);
                    builderPass.setTitle("Table Number");
                    final NumberPicker numberPicker=new NumberPicker(context);
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(500);
                    numberPicker.setValue(serverAccount.getNumberOfTables());
                    builderPass.setView(numberPicker);
                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    builderPass.setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            int tableNumber=numberPicker.getValue();
                            if(tableNumber!= serverAccount.getNumberOfTables())
                            {
                                preference_tables.setSummary(String.valueOf(tableNumber));
                                tempServerAccount.setNumberOfTables(tableNumber);
                                settingsChanged=true;
                            }
                        }
                    });
                    dialog=builderPass.create();
                    dialog.show();
                    return false;
                }
            });*/



            //subscription change

            preference_subscription.setSummary(LoginActivity.currentSubscription);
            ///LOCATION
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //location
            String[] pos= serverAccount.getLocation().split(",");
            final Preference pref_location=findPreference("location");
            pref_location.setSummary(pos.length==4?pos[2]:"Please set your location");

            //mpesa tillnumber
            if(pos.length == 4)
                if(pos[3].contentEquals("KE"))
                    mpesa_till_number_preference.setVisible(true);
            mpesa_till_number_preference.setText(serverAccount.getmCode());
            mpesa_till_number_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    String till_number = newValue.toString();
                    tempServerAccount.setmCode(till_number);
                    settingsChanged = true;
                    ((EditTextPreference)preference).setText(till_number);
                    return false;
                }
            });

            //visible online
           int online= serverAccount.getOnlineVisibility();
            final SwitchPreference pref_visible_online= (SwitchPreference) findPreference("online_visibility");
            pref_visible_online.setChecked(online==1);
            pref_visible_online.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_visible_online.isChecked())
                    {
                        pref_visible_online.setChecked(false);
                        serverAccount.setOnlineVisibility(0);
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_visible_online.setChecked(true);
                        serverAccount.setOnlineVisibility(1);
                        settingsChanged=true;
                    }
                    return false;
                }
            });
            //deliver
            int deliver= serverAccount.getDeliver();
            final SwitchPreference pref_deliver= (SwitchPreference) findPreference("online_delivery");
            pref_deliver.setChecked(deliver==1);
            pref_deliver.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    if(pref_deliver.isChecked())
                    {
                        pref_deliver.setChecked(false);
                        serverAccount.setDeliver(0);
                        settingsChanged=true;
                    }
                    else
                    {
                        pref_deliver.setChecked(true);
                        serverAccount.setDeliver(1);
                        settingsChanged=true;
                    }
                    return false;
                }
            });
            if(serverAccount.getPersona()==2)
            {
                pref_location.setEnabled(false);
                pref_visible_online.setEnabled(false);
                pref_deliver.setEnabled(false);
            }


        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (settingsChanged)
                {
                    updateTask.execute((Void)null);
                }
            }
        }).start();
        super.onDestroy();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_about,rootKey);
        }

    }
    public class UpdateAccount extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_account= LoginActivity.base_url+"update_seller_account.php";
        private JSONParser jsonParser;
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        UpdateAccount()
        {
            Log.d("settings","update started...");
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(tempServerAccount.getId())));
            info.add(new BasicNameValuePair("password", tempServerAccount.getPassword()));
            info.add(new BasicNameValuePair("username", tempServerAccount.getUsername()));
            info.add(new BasicNameValuePair("online", Integer.toString(tempServerAccount.getOnlineVisibility())));
            info.add(new BasicNameValuePair("deliver", Integer.toString(tempServerAccount.getDeliver())));
            info.add(new BasicNameValuePair("country", tempServerAccount.getCountry()));
            info.add(new BasicNameValuePair("location", tempServerAccount.getLocation()));
            info.add(new BasicNameValuePair("order_range", Integer.toString(tempServerAccount.getOrderRadius())));
            info.add(new BasicNameValuePair("number_of_tables", Integer.toString(tempServerAccount.getNumberOfTables())));
            info.add(new BasicNameValuePair("image_type", tempServerAccount.getImageType()));
            info.add(new BasicNameValuePair("m_code", tempServerAccount.getmCode()));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_account,"POST",info);
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings","finished");
            if(success)
            {
                Log.d("settings", "update done");
                serverAccount = tempServerAccount;
                settingsChanged=false;
            }
            else
            {
                Log.e("settings", "error");
                Toast.makeText(context,"Your Account was not updated",Toast.LENGTH_LONG).show();
            }

        }
    }
    public class DeleteAccount extends AsyncTask<Void, Void, Boolean>
    {
        private String url_delete_account= LoginActivity.base_url+"delete_seller_account.php";
        private JSONParser jsonParser;
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        DeleteAccount()
        {
            // setDialog(true);
            Log.d("DELETINGACCOUNT","delete started started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",String.valueOf(serverAccount.getId())));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_account,"POST",info);
            Log.d("jsonaccountdelete",jsonObject.toString());
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
        protected void onPostExecute(final Boolean success)
        {
            Log.d("settings permissions","finished");
            //setDialog(false);
            if(success)
            {
                Toast.makeText(context,"Account deleted",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
            else
            {

            }

        }
    }
}