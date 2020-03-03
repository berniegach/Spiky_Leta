package com.spikingacacia.leta.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.SCategories;
import com.spikingacacia.leta.ui.database.SGroups;
import com.spikingacacia.leta.ui.database.SItems;
import com.spikingacacia.leta.ui.database.SMessages;
import com.spikingacacia.leta.ui.database.SOrders;
import com.spikingacacia.leta.ui.database.SellerAccount;
import com.spikingacacia.leta.ui.billing.BillingManager;
import com.spikingacacia.leta.ui.billing.BillingProvider;
import com.spikingacacia.leta.ui.skulist.AcquireFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.spikingacacia.leta.ui.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

public class LoginA extends AppCompatActivity
    implements BillingProvider,  SignInF.OnFragmentInteractionListener, CreateAccountF.OnFragmentInteractionListener
{
    private static final int OVERLAY_PERMISSION_CODE=541;
    //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
    //public static final String base_url="https://www.spikingacacia.com/leta_project/android/"; //online
    public static final String base_url="http://10.0.2.2/leta_project/android/"; //localhost no connection for testing user accounts coz it doesnt require subscription checking
    //public static final String base_url="http://192.168.0.10/leta_project/android/"; //localhost
    //public static final String base_url="http://192.168.43.228/leta_project/android/"; //localhost  tablet
    //sellers php files
    private String url_get_s_notifications=base_url+"get_seller_notifications.php";
    private String url_get_s_categories=base_url+"get_seller_categories.php";
    private String url_get_s_groups=base_url+"get_seller_groups.php";
    private String url_get_s_items=base_url+"get_seller_items.php";
    private String url_get_s_orders=base_url+"get_seller_orders.php";
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="LoginActivity";
    private JSONParser jsonParser;
    private Intent intentLoginProgress;
    public static int loginProgress;
    public static boolean AppRunningInThisActivity=true;//check if the app is running the in this activity
    //whenever you add a background asynctask make sure to update the finalprogress variables accordingly
    public static int sFinalProgress=5;
    //sellers
    public static SellerAccount sellerAccount;
    public static LinkedHashMap<String, SMessages> sMessagesList;
    public static LinkedHashMap<Integer, SCategories> sCategoriesList;
    public static LinkedHashMap<Integer, SGroups> sGroupsList;
    public static LinkedHashMap<Integer, SItems> sItemsList;
    public static LinkedHashMap<Integer, SOrders>sOrdersList;
    public static int who;
    public static String currentSubscription="Non";
    //billing
    //subscription information
    public static Context mContext;
    private BillingManager mBillingManager;
    private AcquireFragment mAcquireFragment;
    private MainViewController mViewController;
    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";

    // Default sample's package name to check if you changed it
    private static final String DEFAULT_PACKAGE_PREFIX = "com.example";
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        //preference
        preferences=new Preferences(getBaseContext());
        //dark theme prefernce
        View main_view=findViewById(R.id.main);
        if(!preferences.isDark_theme_enabled())
        {
            setTheme(R.style.AppThemeLight);
            main_view.setBackgroundColor(getResources().getColor(R.color.main_background_light));
            findViewById(R.id.sec_main).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            ((TextView)findViewById(R.id.who)).setTextColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.text_light));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.main_background_light));
        }

        //background intent
        intentLoginProgress=new Intent(LoginA.this,ProgressView.class);
        loginProgress=0;
        jsonParser=new JSONParser();
        //initialize the containers
        //sellers
        sellerAccount=new SellerAccount();
        sMessagesList=new LinkedHashMap<>();
        sCategoriesList=new LinkedHashMap<>();
        sGroupsList=new LinkedHashMap<>();
        sItemsList=new LinkedHashMap<>();
        sOrdersList=new LinkedHashMap<>();

        //firebase links
        if(preferences.isVerify_password() || preferences.isReset_password())
        {
            Toast.makeText(getBaseContext(),"Please wait",Toast.LENGTH_SHORT).show();
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null)
                            {
                                deepLink = pendingDynamicLinkData.getLink();
                                if( preferences.isVerify_password())
                                {
                                    setTitle("Sign Up");
                                    Fragment fragment=CreateAccountF.newInstance(1,preferences.getEmail_to_verify());
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }
                                else if(preferences.isReset_password())
                                {
                                    setTitle("Reset Password");
                                    Fragment fragment=CreateAccountF.newInstance(2,preferences.getEmail_to_reset_password());
                                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.loginbase,fragment,"createnewaccount");
                                    transaction.addToBackStack("createaccount");
                                    transaction.commit();
                                }

                            }


                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "getDynamicLink:onFailure", e);
                        }
                    });
        }

        //fragment manager
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                int count=getSupportFragmentManager().getBackStackEntryCount();
                if(count==0)
                    setTitle("Sign In");
            }
        });
        setTitle("Sign In");
        Fragment fragment=SignInF.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"signin");
        transaction.commit();

        //billing
        mContext=this;
        mViewController = new MainViewController(this);

        if (getPackageName().startsWith(DEFAULT_PACKAGE_PREFIX)) {
            throw new RuntimeException("Please change the sample's package name!");
        }

        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            mAcquireFragment = (AcquireFragment) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG);
        }

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());
    }
    @Override
    protected void onDestroy()
    {
        //super.onDestroy();
        if(intentLoginProgress!=null)
            stopService(intentLoginProgress);
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //clear the variables . if not done youll find some list contents add up on top of the previous ones
        loginProgress=0;
        //sellers
        if(!sMessagesList.isEmpty())sMessagesList.clear();
        if(!sCategoriesList.isEmpty())sCategoriesList.clear();
        if(!sGroupsList.isEmpty())sGroupsList.clear();
        if(!sItemsList.isEmpty())sItemsList.clear();
        if(!sOrdersList.isEmpty())sOrdersList.clear();
        AppRunningInThisActivity=true;
        //billing
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==OVERLAY_PERMISSION_CODE)
        {
            if(Settings.canDrawOverlays(this))
            {
                startService(intentLoginProgress);
            }
            startBackgroundTasks();
        }
    }
    /**
     * Billing functions*/
    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }
    @Override
    public boolean isMonthlySubscribed() {
        Log.d(TAG,"subscribed monthly " +mViewController.isMonthlySubscribed());
        LoginA.currentSubscription="Monthly Subscribed";
       /* if(mViewController.isMonthlySubscribed() && LoginActivity.AppRunningInThisActivity)
        {
            Log.d(TAG," Monthly, proceed to menu ");
            Intent intent=new Intent();
            intent.putExtra("allow",true);
            setResult(BILLING_REQUEST_CODE,intent);
            finish();
        }*/
        /*else if(proceedToSubscriptionPurchase==false)
        {
            proceedToSubscriptionPurchase=true;
        }
        else
            onSubscriptionPurchase();*/
        return mViewController.isMonthlySubscribed();
    }

    @Override
    public boolean isYearlySubscribed() {
        LoginA.currentSubscription="Yearly Subscribed";
        Log.d(TAG,"subscribed monthly " +mViewController.isYearlySubscribed());
        /*if(mViewController.isYearlySubscribed() && LoginActivity.AppRunningInThisActivity)
        {
            Log.d(TAG," yearly, proceed to menu ");
            Intent intent=new Intent();
            intent.putExtra("allow",true);
            setResult(BILLING_REQUEST_CODE,intent);
            finish();
        }*/
        /*else if(proceedToSubscriptionPurchase==false)
        {
            proceedToSubscriptionPurchase=true;
        }
        else
            onSubscriptionPurchase();*/
        return mViewController.isYearlySubscribed();
    }
    /**
     * User clicked the "Buy Gas" button - show a purchase dialog with all available SKUs
     */
    public void onSubscriptionPurchase() {
        Log.d(TAG, "Purchase button clicked.");

        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            getSupportFragmentManager().beginTransaction().add(mAcquireFragment,DIALOG_TAG).commit();
            //mAcquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);

            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode()
                    > BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this);
            }
        }
    }

    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        //setWaitScreen(false);
        //updateUi();
        if (mAcquireFragment != null) {
            mAcquireFragment.refreshUI();
        }
    }
    public void onBillingManagerSetupFinished() {
        if (mAcquireFragment != null) {
            mAcquireFragment.onManagerReady(this);
        }
    }
    public boolean isAcquireFragmentShown() {
        return mAcquireFragment != null && mAcquireFragment.isVisible();
    }

    /** Implementation of SignInFragment.java**/
    @Override
    public void onSuccesfull()
    {
        //start the floating service
        if(Build.VERSION.SDK_INT>=23)
        {
            if(!Settings.canDrawOverlays(this))
            {
                //open permissions page
                Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:"+getPackageName()));
                startActivityForResult(intent,OVERLAY_PERMISSION_CODE);
                //return;
            }
            else
            {
                //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
                //if(true)
                if(isMonthlySubscribed()||isYearlySubscribed())
                    startBackgroundTasks();
                else
                {
                    try
                    {
                        //check if the one month since registration has ended
                        //the date added is in the form "d-m-Y H:i"
                        String date_added=sellerAccount.getDateadded();
                        String date_now=sellerAccount.getDateToday();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        Date date_add= simpleDateFormat.parse(date_added);
                        Date date_n=simpleDateFormat.parse(date_now);
                        long difference=date_n.getTime()-date_add.getTime();
                        long days= TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
                        Log.d(TAG,"days "+days);
                        if(days>30)
                        {
                            String message="Your trial period of 30 days has ended.\nWould you like to proceed to the subscriptions purchase?";
                            new AlertDialog.Builder(this)
                                    .setTitle("Kazi 1 month trial")
                                    .setMessage(message)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            onSubscriptionPurchase();
                                        }
                                    })
                                    .create().show();
                        }
                        else
                        {
                            startBackgroundTasks();
                            currentSubscription="Trial period remaining "+(30-days)+" days";
                        }
                    }
                    catch (ParseException e)
                    {
                        Log.e(TAG,"exception "+e.getMessage());
                    }


                    Log.d(TAG, "NO subscriptions");

                }

            }
        }
        else
        {
            if(isMonthlySubscribed()||isYearlySubscribed())
                startBackgroundTasks();
            else
            {
                try
                {
                    //check if the one month since registration has ended
                    //the date added is in the form "d-m-Y H:i"
                    String date_added=sellerAccount.getDateadded();
                    String date_now=sellerAccount.getDateToday();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date_add= simpleDateFormat.parse(date_added);
                    Date date_n=simpleDateFormat.parse(date_now);
                    long difference=date_n.getTime()-date_add.getTime();
                    long days= TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
                    if(days>30)
                    {
                        String message="Your trial period of 30 days has ended.\nWould you like to proceed to the subscriptions purchase?";
                        new AlertDialog.Builder(this)
                                .setTitle("Kazi 1 month trial")
                                .setMessage(message)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        onSubscriptionPurchase();
                                    }
                                })
                                .create().show();
                    }
                    else
                    {
                        startBackgroundTasks();
                        currentSubscription="Trial period remaining "+(30-days)+" days";
                    }
                }
                catch (ParseException e)
                {
                    Log.e(TAG,"exception "+e.getMessage());
                }


                Log.d(TAG, "NO subscriptions");
            }

        }
    }
    @Override
    public void purchaseSubscription()
    {
        onSubscriptionPurchase();
    }
    private void startBackgroundTasks()
    {
        startService(intentLoginProgress);
        new SMessagesTask().execute((Void)null);
        new SCategoriesTask().execute((Void)null);
        new SGroupsTask().execute((Void)null);
        new SItemsTask().execute((Void)null);
        new SOrdersTask().execute((Void)null);
        Intent intent=new Intent(this, SMenuA.class);
        // intent.putExtra("NOTHING","nothing");
        startActivity(intent);
    }
    @Override
    public void createAccount()
    {
        setTitle("Sign Up");
        Fragment fragment=CreateAccountF.newInstance(0,"");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginbase,fragment,"createnewaccount");
        transaction.addToBackStack("createaccount");
        transaction.commit();
    }
    /** Implementation of CreateAccountF.java**/
    @Override
    public  void onRegisterFinished()
    {
        setTitle("Sign In");
        onBackPressed();
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
    private class SMessagesTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("SNOTIFICATIONS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_notifications,"POST",info);
            Log.d("sNotis",""+jsonObject.toString());
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
            Log.d("SNOTIFICATIONS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == sFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the sellers categories
     * The returned infos are id,  categories, descriptions, dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class SCategoriesTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("SCATEGORIES: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_categories,"POST",info);
            Log.d("sCategories",""+jsonObject.toString());
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    categoriesArrayList=jsonObject.getJSONArray("categories");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String category=jsonObjectNotis.getString("category");
                        String description=jsonObjectNotis.getString("description");
                        String dateadded=jsonObjectNotis.getString("dateadded");
                        String datechanged=jsonObjectNotis.getString("datechanged");
                        SCategories sCategories=new SCategories(id,category,description,dateadded,datechanged);
                        sCategoriesList.put(id,sCategories);
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
            Log.d("SCATEGORIES: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == sFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the sellers groups
     * The returned infos are id,  categories, groups, descriptions, dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class SGroupsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("SGROUPS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_groups,"POST",info);
            Log.d("sGroups",""+jsonObject.toString());
            try
            {
                JSONArray groupsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    groupsArrayList=jsonObject.getJSONArray("groups");
                    for(int count=0; count<groupsArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=groupsArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int category=jsonObjectNotis.getInt("category");
                        String group=jsonObjectNotis.getString("group");
                        String description=jsonObjectNotis.getString("description");
                        String dateadded=jsonObjectNotis.getString("dateadded");
                        String datechanged=jsonObjectNotis.getString("datechanged");
                        SGroups sGroups=new SGroups(id,category,group,description,dateadded,datechanged);
                        sGroupsList.put(id,sGroups);
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
            Log.d("SGROUPS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == sFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }
    /**
     * Following code will get the sellers groups
     * The returned infos are id,  categories, groups, items, descriptions, sellingprice, available, dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * success==1 successful get
     * success==0 for id argument missing
     **/
    private class SItemsTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("SITEMS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_items,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
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
                        int category=jsonObjectNotis.getInt("category");
                        int group=jsonObjectNotis.getInt("group");
                        String item=jsonObjectNotis.getString("item");
                        String description=jsonObjectNotis.getString("description");
                        double selling_price=jsonObjectNotis.getDouble("sellingprice");
                        int available=jsonObjectNotis.getInt("available");
                        String dateadded=jsonObjectNotis.getString("dateadded");
                        String datechanged=jsonObjectNotis.getString("datechanged");
                        SItems sItems=new SItems(id,category,group,item,description,selling_price,available,dateadded,datechanged);
                        sItemsList.put(id,sItems);
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
            Log.d("SITEMS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == sFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
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
    private class SOrdersTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            Log.d("BORDERS: ","starting....");
            if(!sOrdersList.isEmpty())sOrdersList.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(sellerAccount.getId())));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_orders,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
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
            Log.d("BORDERS: ","finished...."+"progress: "+String.valueOf(loginProgress));
            loginProgress+=1;
            if (loginProgress == sFinalProgress)
                stopService(intentLoginProgress);

            if (successful)
            {

            }
            else
            {

            }
        }
    }


}
