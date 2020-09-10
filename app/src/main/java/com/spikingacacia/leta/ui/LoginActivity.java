package com.spikingacacia.leta.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.database.ServerAccount;
import com.spikingacacia.leta.ui.billing.BillingManager;
import com.spikingacacia.leta.ui.billing.BillingProvider;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.skulist.AcquireFragment;
import com.spikingacacia.leta.ui.util.MyFirebaseMessagingService;

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
import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity
    implements BillingProvider,View.OnClickListener
{
    private static final int OVERLAY_PERMISSION_CODE=541;
    //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
    public static final String base_url="https://3.20.17.200/order/"; //online
    //public static final String base_url="http://10.0.2.2/leta_project/android/"; //localhost no connection for testing user accounts coz it doesnt require subscription checking

    private String TAG="LoginActivity";
    public static boolean AppRunningInThisActivity=true;//check if the app is running the in this activity
    //sellers
    private static ServerAccount serverAccount;
    public static LinkedHashMap<Integer, DMenu> sItemsList;
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
    public final static String SAVE_INSTANCE_SERVER_ACCOUNT = "save_server_account";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            sleep(5000);
        } catch (InterruptedException e)
        {
            Log.e(TAG,"failed to sleep");
        }
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        //initialize the containers
        //sellers
        serverAccount =new ServerAccount();
        sItemsList=new LinkedHashMap<>();


        //billing
        /*mContext=this;
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
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());*/
    }
    @Override
    public void onStart()
    {

        super.onStart();
        //check internet connection
        if(isNetworkAvailable())
        {
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            account = GoogleSignIn.getLastSignedInAccount(this);
            //proceed to sign in
            if(account!=null)
            {
                //Intent intent = new Intent(this, OrdersService.class);
                //startService(intent);
                proceedToLogin();
            }
            else
                showProgress(false);
        }
        else
        {
            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Error")
                    .setMessage("There is not internet connection")
                    /*.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })*/
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finishAffinity();
                        }
                    }).create().show();
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
   /* @Override
    protected void onDestroy()
    {
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }*/
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
                proceedToLogin();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    public static ServerAccount getServerAccount()
    {
        return serverAccount;
    }
    public static void setServerAccount(ServerAccount serverAccount1)
    {
        serverAccount = serverAccount1;
    }
    /*@Override
    protected void onResume()
    {
        super.onResume();
        //sellers
        if(!sItemsList.isEmpty())sItemsList.clear();
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
    }*/
    /**
     * Billing functions*/
    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }
    @Override
    public boolean isMonthlySubscribed() {
        Log.d(TAG,"subscribed monthly " +mViewController.isMonthlySubscribed());
        LoginActivity.currentSubscription="Monthly Subscribed";
        return mViewController.isMonthlySubscribed();
    }

    @Override
    public boolean isYearlySubscribed() {
        LoginActivity.currentSubscription="Yearly Subscribed";
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
        //get the account details
        if(radioAdmin.isChecked())
            new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
        else if(radioWaiter.isChecked())
            new LoginWaiterTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
        else if(preferences.getPersona()==2)
            new LoginWaiterTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
        else
            new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /*public void onSuccesfullLogin()
    {
        long days=0;
        try
        {
            String date_added= serverAccount.getDateadded();
            String date_now= serverAccount.getDateToday();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date_add= simpleDateFormat.parse(date_added);
            Date date_n=simpleDateFormat.parse(date_now);
            long difference=date_n.getTime()-date_add.getTime();
            days= TimeUnit.DAYS.convert(difference,TimeUnit.MILLISECONDS);
        }
        catch (ParseException e)
        {
            Log.e(TAG,"exception "+e.getMessage());
            showProgress(false);
            return;
        }

        //REMEMBER TO CHANGE THIS WHEN CHANGING BETWEEN ONLINE AND LOCALHOST
        //if(true)
        if(isMonthlySubscribed()||isYearlySubscribed())
            startBackgroundTasks();
        else
        {
            //check if the one month since registration has ended
            //the date added is in the form "d-m-Y H:i"

            Log.d(TAG,"days "+days);
            if(days>30 && serverAccount.getPersona()==1)
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
            else if(days>30 && serverAccount.getPersona() ==2)
            {
                String message="No subscription available";
                new AlertDialog.Builder(this)
                        .setTitle("Restaurant payment due")
                        .setMessage(message)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
            else
            {
                startBackgroundTasks();
                currentSubscription="Trial period remaining "+(30-days)+" days";
            }

            Log.d(TAG, "NO subscriptions");

        }
    }*/

    /*private void startBackgroundTasks()
    {
        Intent intent = new Intent(this, OrdersService.class);
        startService(intent);
        proceedToLogin();
    }*/
    public class RegisterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_create_account_seller = LoginActivity.base_url+"create_seller_account.php";
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
            catch (JSONException | NullPointerException e)
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
                //only the admin can register an account
                new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);
            }
            else if(success==-1)
            {
                //email already there do nothing
                Log.d(TAG,"email already there");
                //proceed to login
                // we have already set the persona so...
                new LoginTask(account.getEmail(),"pass_wjdjsdbsjdgshjg").execute((Void)null);

            }
            else
            {
                showProgress(false);
                Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>()
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
            Log.d(TAG,"persona 1");
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

                    serverAccount.setPersona(1);
                    serverAccount.setId(accountObject.getInt("id"));
                    serverAccount.setEmail(accountObject.getString("email"));
                    serverAccount.setPassword(accountObject.getString("password"));
                    serverAccount.setUsername(accountObject.getString("username"));
                    serverAccount.setOnlineVisibility(accountObject.getInt("online"));
                    serverAccount.setDeliver(accountObject.getInt("deliver"));
                    serverAccount.setDiningOptions(accountObject.getString("dining_options"));
                    serverAccount.setCountry(accountObject.getString("country"));
                    serverAccount.setLocation(accountObject.getString("location"));
                    serverAccount.setOrderRadius(accountObject.getInt("order_radius"));
                    serverAccount.setOrderFormat(accountObject.getInt("order_format"));
                    serverAccount.setNumberOfTables(accountObject.getInt("number_of_tables"));
                    serverAccount.setImageType(accountObject.getString("image_type"));
                    serverAccount.setmCode(accountObject.getString("m_code"));
                    serverAccount.setCommision(accountObject.getDouble("commision"));
                    serverAccount.setMpesaMobile(accountObject.getString("m_mobile"));
                    serverAccount.setDateadded(accountObject.getString("dateadded"));
                    serverAccount.setDatechanged(accountObject.getString("datechanged"));
                    serverAccount.setDateToday(accountObject.getString("today"));
                    serverAccount.setmFirebaseTokenId(accountObject.getString("firebase_token_id"));
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
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                //prevent this activity from flickering as we call the next one
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            else
            {
                showProgress(false);
                if(success==-2)
                {
                    //the email is not registered
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("No account")
                            .setMessage("Would you like to create a new Restaurant account with the email?")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                                    mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Log.d(TAG,"gmail signed out");
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    new RegisterTask(account.getEmail(),"null").execute((Void)null);
                                }
                            }).create().show();
                }
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
            Log.d(TAG,"persona 2");
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
            Log.d(TAG,jsonObject.toString());
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    //seccesful
                    JSONArray accountArray=jsonObject.getJSONArray("account");
                    JSONObject accountObject=accountArray.getJSONObject(0);

                    serverAccount.setPersona(2);
                    serverAccount.setId(accountObject.getInt("id"));
                    serverAccount.setEmail(accountObject.getString("email"));
                    serverAccount.setPassword(accountObject.getString("password"));
                    serverAccount.setUsername(accountObject.getString("username"));
                    serverAccount.setOnlineVisibility(accountObject.getInt("online"));
                    serverAccount.setDeliver(accountObject.getInt("deliver"));
                    serverAccount.setDiningOptions(accountObject.getString("dining_options"));
                    serverAccount.setCountry(accountObject.getString("country"));
                    serverAccount.setLocation(accountObject.getString("location"));
                    serverAccount.setOrderRadius(accountObject.getInt("order_radius"));
                    serverAccount.setOrderFormat(accountObject.getInt("order_format"));
                    serverAccount.setNumberOfTables(accountObject.getInt("number_of_tables"));
                    serverAccount.setmCode(accountObject.getString("m_code"));
                    serverAccount.setCommision(accountObject.getDouble("commision"));
                    serverAccount.setMpesaMobile(accountObject.getString("m_mobile"));
                    serverAccount.setDateadded(accountObject.getString("dateadded"));
                    serverAccount.setDatechanged(accountObject.getString("datechanged"));
                    serverAccount.setDateToday(accountObject.getString("today"));
                    //waiter information
                    serverAccount.setWaiter_id(accountObject.getInt("waiter_id"));
                    serverAccount.setWaiter_email(accountObject.getString("waiter_email"));
                    serverAccount.setWaiter_names(accountObject.getString("waiter_names"));
                    serverAccount.setWaiterImageType(accountObject.getString("waiter_image_type"));
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
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                //prevent this activity from flickering as we call the next one
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
            else
            {
                showProgress(false);
                Toast.makeText(getBaseContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>()
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





}
