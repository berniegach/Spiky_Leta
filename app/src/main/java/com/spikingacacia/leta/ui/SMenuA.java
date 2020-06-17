package com.spikingacacia.leta.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.board.BoardA;
import com.spikingacacia.leta.ui.inventory.SIInventoryA;
import com.spikingacacia.leta.ui.messages.SMMessageListActivity;
import com.spikingacacia.leta.ui.orders.SOOrdersA;
import com.spikingacacia.leta.ui.qr_code.Encoder;
import com.spikingacacia.leta.ui.reports.SRReportsA;
import com.spikingacacia.leta.ui.waiters.WaitersA;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.currentSubscription;
import static com.spikingacacia.leta.ui.LoginA.loginProgress;
import static com.spikingacacia.leta.ui.LoginA.sFinalProgress;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;

public class SMenuA extends AppCompatActivity
implements SMenuF.OnFragmentInteractionListener{
    private static final int PERMISSION_REQUEST_INTERNET=2;
    private static final int PERMISSION_REQUEST_STORAGE=2;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String url_get_restaurants=base_url+"get_near_restaurants.php";
    private boolean runRate=true;
    Preferences preferences;
    TextView tWho;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String TAG="SMenuA";
    private JSONParser jsonParser;
    public static boolean within_location=false;
    private final static String default_notification_channel_id = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_smenu);

        jsonParser=new JSONParser();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        tWho=((TextView)findViewById(R.id.who));
        //preference
        preferences=new Preferences(getBaseContext());
        //set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.collapsingToolbar);
        final Typeface tf= ResourcesCompat.getFont(this,R.font.amita);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        setSupportActionBar(toolbar);
        setTitle("Menu");

        Fragment fragment=SMenuF.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"menu");
        transaction.commit();

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //set the welcome text
        //we set it in onResume to factor in the possibility of the username changing in the settings
        if(LoginA.serverAccount.getUsername().length()<2 || LoginA.serverAccount.getUsername().contentEquals("null"))
        {
            tWho.setText("Please go to settings and set the business name...");
        }
        else
            if(serverAccount.getPersona()==1)
                tWho.setText(serverAccount.getUsername()+" : "+ serverAccount.getWaiter_names());
            else
                tWho.setText(serverAccount.getUsername());
        if(currentSubscription.length()>10)
            ((TextView)findViewById(R.id.trial)).setText(currentSubscription);
        else
            ((TextView)findViewById(R.id.trial)).setVisibility(View.GONE);
        checkFields();
        if(runRate)
        {
            AppRater.app_launched(this);
            runRate=false;
        }
        if(serverAccount.getPersona()==1)
            check_if_within_location();

    }

    /**implementation of CMenuFragment.java**/
    @Override
    public void onMenuClicked(int id)
    {
        if(id==1)
        {
            //inventory
            Intent intent=new Intent(SMenuA.this, SIInventoryA.class);
            intent.putExtra("which",1);
            intent.putExtra("title","Category");
            startActivity(intent);
        }
        else if(id==2)
        {
            //orders
            Intent intent=new Intent(SMenuA.this, SOOrdersA.class);
            startActivity(intent);

        }

        else if(id==3)
        {
            //reports
            Intent intent=new Intent(SMenuA.this, SRReportsA.class);
            startActivity(intent);
        }

        else if(id==4)
        {
            //messages
            Intent intent=new Intent(SMenuA.this, SMMessageListActivity.class);
            startActivity(intent);
        }

        else if(id==5)
        {
            //settings
            Intent intent=new Intent(SMenuA.this,SSettingsA.class);
            startActivity(intent);
        }
    }
    @Override
    public void onWaitersClicked()
    {
        Intent intent=new Intent(SMenuA.this, WaitersA.class);
        startActivity(intent);
    }

    @Override
    public void onMyBoardClicked()
    {
        Intent intent=new Intent(SMenuA.this, BoardA.class);
        startActivity(intent);
    }

    @Override
    public void onQRClicked()
    {
        show_qr();
    }

    @Override
    public void onLogOut()
    {
        new AlertDialog.Builder(SMenuA.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SharedPreferences loginPreferences=getBaseContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
                        SharedPreferences.Editor loginPreferencesEditor =loginPreferences.edit();
                        loginPreferencesEditor.putBoolean("rememberme",false);
                        loginPreferencesEditor.commit();
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                        //intent.addCategory(Intent.CATEGORY_HOME);
                        // startActivity(intent);
                    }
                }).create().show();


    }
    private void checkFields()
    {
        if (loginProgress >= sFinalProgress)
        {
            String message="";
           /* if(LoginA.sCategoriesList.size()==0)
                message="Please create the major categories eg food, drinks etc.\nGo to "+
                        "\u279e Inventory  \u279e Options(upper right corner) \u279e add";
            else if(LoginA.sGroupsList.size()==0)
                message="Please create the groups eg pizza, burgers etc.\nGo to "+
                        "\u279e Inventory  \u279e  category \u279e Options(upper right corner) \u279e add";
            else if(LoginA.sItemsList.size()==0)
                message="Please create the items eg pepperoni, cheese burger etc.\nGo to "+
                        " \u279e Inventory  \u279e  category \u279e  group \u279e Options(upper right corner) \u279e add";*/
            if(!message.contentEquals(""))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Missing information")
                        .setMessage(message)
                        .setPositiveButton("Ok",null)
                        .create().show();
            }
        }
    }
    private void check_if_within_location()
    {
        final Handler handler=new Handler();
        final Runnable runnable=new Runnable()
        {
            @Override
            public void run()
            {
                //within_location=true;
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
                        sleep(2000);
                        getCurrentLocation();
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
    }
    private void getCurrentLocation()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //get the users location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            //Get last known location. In some rare situations this can be null
                            if(location!=null)
                            {
                                double latitude=location.getLatitude();
                                double longitude=location.getLongitude();
                                //get addresses
                                Geocoder geocoder=new Geocoder(SMenuA.this, Locale.getDefault());
                                List<Address> addresses;
                                try
                                {
                                    addresses=geocoder.getFromLocation(latitude,longitude,10);
                                    new RestaurantsTask(String.valueOf(latitude),String.valueOf(longitude),addresses.get(0).getLocality()).execute((Void)null);
                                    for(int c=0; c<addresses.size(); c+=1)
                                        Log.d("loc: ",addresses.get(c).getLocality()+"\n");
                                }
                                catch (IOException e)
                                {
                                    Snackbar.make(tWho,"Error getting your location.\nPlease try again.",Snackbar.LENGTH_SHORT).show();
                                    Log.e("address",""+e.getMessage());
                                }
                            }

                        }
                    });
        }
        //request the permission
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_INTERNET);
        }

    }
    /**
     * Following code will all personnel tasks info from boss tasks table.
     * The returned columns are id, titles, descriptions, startings, endings, repetitions, locations, positions, geofence dateadded, datechanged.
     * Arguments are:
     * id==boss id.
     * Returns are:
     * tasks rows
     * success==1 successful get
     * success==0 for missing certificates info
     * success==0 for id argument missing
     **/
    private class RestaurantsTask extends AsyncTask<Void, Void, Boolean>
    {
        //final private String country;
        final private String latitude;
        final private String longitude;
        final private String location;

        public RestaurantsTask( String latitude, String longitude, String location)
        {
            //this.country=country;
            this.latitude=latitude;
            this.longitude=longitude;
            this.location=location;
        }
        @Override
        protected void onPreExecute()
        {

            Log.d("CRESTAUNRANTS: ","starting....");
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            //info.add(new BasicNameValuePair("country",country));
            info.add(new BasicNameValuePair("latitude",latitude));
            info.add(new BasicNameValuePair("longitude",longitude));
            info.add(new BasicNameValuePair("location",location));
            info.add(new BasicNameValuePair("which","1"));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_restaurants,"POST",info);
            Log.d("cTasks",""+jsonObject.toString());
            try
            {
                JSONArray restArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    restArrayList=jsonObject.getJSONArray("restaurants");
                    restArrayList=restArrayList.getJSONArray(0);
                    for(int count=0; count<restArrayList.length(); count+=1)
                    {
                        JSONObject jsonObject_restaurants=restArrayList.getJSONObject(count);
                        int id=jsonObject_restaurants.getInt("id");
                        String names=jsonObject_restaurants.getString("username");
                        double distance=jsonObject_restaurants.getDouble("distance");
                        double latitude=jsonObject_restaurants.getDouble("latitude");
                        double longitude=jsonObject_restaurants.getDouble("longitude");
                        String locality=jsonObject_restaurants.getString("locality");
                        String country=jsonObject_restaurants.getString("country");
                        int order_radius=jsonObject_restaurants.getInt("order_radius");
                        int tables = jsonObject_restaurants.getInt("number_of_tables");

                        if(id== serverAccount.getId())
                            return true;
                    }
                    return false;
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
                within_location=true;
            }
            else
            {
                Log.e(TAG,"my lat:long "+latitude+":"+longitude);
                within_location=false;
            }
        }
    }
    @Override
    public void play_notification()
    {
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (getBaseContext(), alarmSound);
        mp.start();
       /* NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(SMenuA.this, default_notification_channel_id )
                        .setSmallIcon(R.mipmap.ic_launcher )
                        .setContentTitle( "New Order" )
                        .setContentText( "a new order has arrived" ) ;
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context. NOTIFICATION_SERVICE );
        mNotificationManager.notify(( int ) System. currentTimeMillis () ,
                mBuilder.build());*/
    }
    private void show_qr()
    {
        final Bitmap bitmap_qr = Encoder.encode(serverAccount.getEmail());
        ImageView imageView=new ImageView(this);
        imageView.setImageBitmap(bitmap_qr);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(600,600));
        new AlertDialog.Builder(this)
                .setTitle("My QR Code")
                .setView(imageView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        save_bitmap(bitmap_qr);
                    }
                }).create().show();
    }
    public void save_bitmap(Bitmap bitmap)
    {
        Log.d(TAG, "in ssave bitmap" );
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/leta");
            myDir.mkdirs();
            int n = 10000;
            String fname = "Image_Leta_QR"  + ".jpg";
            File file = new File(myDir, fname);
            Log.d(TAG, "" + file);
            if (file.exists())
                file.delete();
            try
            {
                Toast.makeText(this,"QR Code saved", Toast.LENGTH_SHORT).show();
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                Log.e(TAG,"error "+e.getMessage());
            }

            /////

            /////
        }
        //request the permission
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }
        //////
    }

}
