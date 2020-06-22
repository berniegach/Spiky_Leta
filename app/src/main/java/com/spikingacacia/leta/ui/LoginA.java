package com.spikingacacia.leta.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.database.SMessages;
import com.spikingacacia.leta.ui.database.Orders;
import com.spikingacacia.leta.ui.database.ServerAccount;
import com.spikingacacia.leta.ui.billing.BillingManager;
import com.spikingacacia.leta.ui.billing.BillingProvider;
import com.spikingacacia.leta.ui.database.WaitersD;
import com.spikingacacia.leta.ui.main.MainActivity;
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
    implements BillingProvider,View.OnClickListener
{
    private static final int OVERLAY_PERMISSION_CODE=541;
    //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
    //public static final String base_url="https://www.spikingacacia.com/leta_project/android/"; //online
    public static final String base_url="http://10.0.2.2/leta_project/android/"; //localhost no connection for testing user accounts coz it doesnt require subscription checking

    private String TAG="LoginActivity";
    private Intent intentLoginProgress;
    public static int loginProgress;
    public static boolean AppRunningInThisActivity=true;//check if the app is running the in this activity
    //whenever you add a background asynctask make sure to update the finalprogress variables accordingly
    public static int sFinalProgress=6;
    //sellers
    public static ServerAccount serverAccount;
    public static LinkedHashMap<String, SMessages> sMessagesList;
    public static LinkedHashMap<Integer, DMenu> sItemsList;
    public static LinkedHashMap<Integer, WaitersD> waitersList;
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
    public static GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 21;
    static public GoogleSignInAccount account;
    private RadioButton radioAdmin;
    private RadioButton radioWaiter;
    private ProgressBar progressBar;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        setTitle("Login");
        preferences = new Preferences(getBaseContext());
        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.container);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        radioAdmin = findViewById(R.id.radio_admin);
        radioWaiter = findViewById(R.id.radio_waiter);

        //background intent
        intentLoginProgress=new Intent(LoginA.this,ProgressView.class);
        loginProgress=0;
        //initialize the containers
        //sellers
        serverAccount =new ServerAccount();
        sMessagesList=new LinkedHashMap<>();
        sItemsList=new LinkedHashMap<>();
        waitersList=new LinkedHashMap<>();


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
    public void onStart()
    {

        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        //proceed to sign in
        if(account!=null)
        {
            showProgress(true);
            new RegisterTask(account.getEmail(),"null").execute((Void)null);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if(account!=null)
            {
                Log.d(TAG, "email: " + account.getEmail());
                new RegisterTask(account.getEmail(),"null").execute((Void)null);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //clear the variables . if not done youll find some list contents add up on top of the previous ones
        loginProgress=0;
        //sellers
        if(!sMessagesList.isEmpty())sMessagesList.clear();
        if(!sItemsList.isEmpty())sItemsList.clear();
        if(!waitersList.isEmpty())waitersList.clear();
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
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    private void proceedToLogin()
    {
        Intent intent=new Intent(LoginA.this, MainActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    public void onSuccesfullLogin()
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
                String date_added= serverAccount.getDateadded();
                String date_now= serverAccount.getDateToday();
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
                showProgress(false);
            }
            showProgress(false);

            Log.d(TAG, "NO subscriptions");

        }
    }

    private void startBackgroundTasks()
    {
        showProgress(false);
        //new SMessagesTask().execute((Void)null);
        //new SOrdersTask().execute((Void)null);
        //new WaitersTask().execute((Void)null);
        proceedToLogin();
    }
    public class RegisterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_create_account_seller =LoginA.base_url+"create_seller_account.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private final String mEmail;
        private final String mPassword;
        private int success;

        RegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            jsonParser = new JSONParser();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("email",mEmail));
            info.add(new BasicNameValuePair("password",mPassword));
            //getting the json object using post method
            JSONObject jsonObject=jsonParser.makeHttpRequest(url_create_account_seller,"POST",info);
            Log.d("Create response",""+jsonObject.toString());
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                    return true;
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
                Toast.makeText(getBaseContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                //proceed to login
                if(radioAdmin.isClickable())
                    new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
                else
                    new LoginWaiterTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
            }
            else if(success==-1)
            {
                //email already there do nothing
                Log.d(TAG,"email already there");
                //proceed to login
                // we have already set the persona so...
                if(preferences.getPersona()==2)
                    new LoginWaiterTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
                else
                    new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);

            }
            else
            {
                showProgress(false);
                Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginA.this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.d(TAG,"gmail signed out");
                    }
                });
            }

        }
    }
    public  class LoginTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_account_seller =base_url+"get_seller_account.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private final String mEmail;
        private  JSONParser jsonParser;
        private final String mPassword;
        private int success=0;

        LoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // logIn=handler.LogInContractor(mEmail,mPassword);

            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("email",mEmail));
            info.add(new BasicNameValuePair("password",mPassword));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_account_seller,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    //seccesful
                    JSONArray accountArray=jsonObject.getJSONArray("account");
                    JSONObject accountObject=accountArray.getJSONObject(0);

                    serverAccount.setPersona(0);
                    serverAccount.setId(accountObject.getInt("id"));
                    serverAccount.setEmail(accountObject.getString("email"));
                    serverAccount.setPassword(accountObject.getString("password"));
                    serverAccount.setUsername(accountObject.getString("username"));
                    serverAccount.setOnlineVisibility(accountObject.getInt("online"));
                    serverAccount.setDeliver(accountObject.getInt("deliver"));
                    serverAccount.setCountry(accountObject.getString("country"));
                    serverAccount.setLocation(accountObject.getString("location"));
                    serverAccount.setOrderRadius(accountObject.getInt("order_radius"));
                    serverAccount.setOrderFormat(accountObject.getInt("order_format"));
                    serverAccount.setNumberOfTables(accountObject.getInt("number_of_tables"));
                    serverAccount.setImageType(accountObject.getString("image_type"));
                    serverAccount.setDateadded(accountObject.getString("dateadded"));
                    serverAccount.setDatechanged(accountObject.getString("datechanged"));
                    serverAccount.setDateToday(accountObject.getString("today"));
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
        protected void onPostExecute(final Boolean successfull)
        {

            if (successfull) {
                Log.d(TAG,"successful login admin");
                preferences.setPersona(1);
                onSuccesfullLogin();
            }
            else
            {
                showProgress(false);
                Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginA.this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.d(TAG,"gmail signed out");
                    }
                });
            }
        }

    }
    public  class LoginWaiterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_account_seller_waiter =base_url+"get_seller_account_waiter.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private final String mEmail;
        private  JSONParser jsonParser;
        private final String mPassword;
        private int success=0;

        LoginWaiterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // logIn=handler.LogInContractor(mEmail,mPassword);

            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("email",mEmail));
            info.add(new BasicNameValuePair("password",mPassword));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_account_seller_waiter,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    //seccesful
                    JSONArray accountArray=jsonObject.getJSONArray("account");
                    JSONObject accountObject=accountArray.getJSONObject(0);

                    serverAccount.setPersona(1);
                    serverAccount.setId(accountObject.getInt("id"));
                    serverAccount.setEmail(accountObject.getString("email"));
                    serverAccount.setPassword(accountObject.getString("password"));
                    serverAccount.setUsername(accountObject.getString("username"));
                    serverAccount.setOnlineVisibility(accountObject.getInt("online"));
                    serverAccount.setDeliver(accountObject.getInt("deliver"));
                    serverAccount.setCountry(accountObject.getString("country"));
                    serverAccount.setLocation(accountObject.getString("location"));
                    serverAccount.setOrderRadius(accountObject.getInt("order_radius"));
                    serverAccount.setOrderFormat(accountObject.getInt("order_format"));
                    serverAccount.setNumberOfTables(accountObject.getInt("number_of_tables"));
                    serverAccount.setDateadded(accountObject.getString("dateadded"));
                    serverAccount.setDatechanged(accountObject.getString("datechanged"));
                    serverAccount.setDateToday(accountObject.getString("today"));
                    //waiter information
                    serverAccount.setWaiter_id(accountObject.getInt("waiter_id"));
                    serverAccount.setWaiter_names(accountObject.getString("waiter_names"));
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
        protected void onPostExecute(final Boolean successfull) {

            if (successfull) {
                Log.d(TAG,"successful login waiter");
                preferences.setPersona(2);
                onSuccesfullLogin();

            }
            else
            {
                showProgress(false);
                Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginA.this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.d(TAG,"gmail signed out");
                    }
                });
            }
        }

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
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        @Override
        protected void onPreExecute()
        {
            Log.d("SNOTIFICATIONS: ","starting....");
            jsonParser = new JSONParser();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("id",Integer.toString(serverAccount.getId())));
            // making HTTP request
            //sellers php files
            String url_get_s_notifications = base_url + "get_seller_notifications.php";
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


    private class WaitersTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        @Override
        protected void onPreExecute()
        {
            Log.d("BORDERS: ","starting....");
            if(!waitersList.isEmpty())waitersList.clear();
            jsonParser = new JSONParser();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_id",Integer.toString(serverAccount.getId())));
            // making HTTP request
            String url_get_s_waiters = base_url + "get_waiters.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_waiters,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("waiters");
                    for(int count=0; count<itemsArrayList.length(); count+=1)
                    {
                        JSONObject json_object_waiters=itemsArrayList.getJSONObject(count);
                        int id=json_object_waiters.getInt("id");
                        String email=json_object_waiters.getString("email");
                        String username=json_object_waiters.getString("username");

                        WaitersD waiter=new WaitersD(id,email,username,0);
                        waitersList.put(id,waiter);
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
                Log.e(TAG+"JSON"," waiter"+e.getMessage());
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
