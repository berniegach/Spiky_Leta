package com.spikingacacia.leta.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.main.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;


public class OrdersService extends Service
{
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private String TAG = "orders_service";
    private String default_notification_channel_id = "default";
    public static  Preferences preferences;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler
    {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            while(true)
            {
                try
                {
                    new OrdersTask().execute((Void)null);
                    Thread.sleep(5000);
                } catch (InterruptedException e)
                {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        preferences = new Preferences(this);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG,"starting..");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "You won't get orders notification anymore", Toast.LENGTH_SHORT).show();
    }
    private void play_notification(String message)
    {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        createNotificationChannel();
        Uri alarmSound =  RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (this, alarmSound);
        mp.start();
        vibrate_on_click();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, default_notification_channel_id )
                        .setSmallIcon(R.mipmap.ic_launcher )
                        .setContentTitle( "New Order" )
                        .setContentText( message )
                        .setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context. NOTIFICATION_SERVICE );
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build());
    }
    private void vibrate_on_click()
    {
        Vibrator vibrator = (Vibrator) (this).getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator == null)
            Log.e(TAG,"No vibrator");
        else
            vibrator.vibrate(100);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_1";
            String description = "get recent orders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel( default_notification_channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private class OrdersTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_orders = base_url + "get_seller_orders_count.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email",LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_orders,"POST",info);
            //Log.d("sItems",""+jsonObject.toString());
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("orders");
                    for(int c=0; c<itemsArrayList.length(); c+=1)
                    {
                        JSONObject jsonObjectNotis=itemsArrayList.getJSONObject(c);
                        int order_status=jsonObjectNotis.getInt("order_status");
                        int count = jsonObjectNotis.getInt("count");
                        //Log.d(TAG," status: "+order_status+" count: "+count);
                        String message;
                        boolean show = false;
                        //-2 = unpaid, -1 = paid, 0 = deleted, 1 = pending, 2 = ..... until 5 = finished
                        switch(order_status)
                        {
                            case -2:
                                message = "There is a new order that requires payment";
                                if(preferences.getUnpaid_count()!=count)
                                {
                                    if(preferences.getUnpaid_count()<count)
                                        show = true;
                                    preferences.setUnpaid_count(count);
                                }
                                break;
                                //this is pending orders that have been paid
                            case -1:
                                message = "Payment for an order applied";
                                if(preferences.getPaid_count()!=count)
                                {
                                    if(preferences.getPaid_count()<count)
                                        show = true;
                                    preferences.setPaid_count(count);
                                }
                                break;
                                //these are pending orders that have not been paid
                            case 1:
                                message = "There is a new pending order";
                                if(preferences.getPending_count()!=count)
                                {
                                    if(preferences.getPending_count()<count)
                                        show = true;
                                    preferences.setPending_count(count);
                                }
                                break;
                            case 2:
                                message = "There is a new order that requires payment";
                                if(preferences.getPayment_count()!=count)
                                {
                                    if(preferences.getPayment_count()<count)
                                    {
                                        show = true;
                                    }
                                    preferences.setPayment_count(count);
                                }
                                break;
                            case 3:
                                message = "There is a new order in progress";
                                if(preferences.getIn_progress_count()!=count)
                                {
                                    if(preferences.getIn_progress_count()<count)
                                    {
                                        show = true;
                                        //one order moved from payment count to in progress
                                        //preferences.setPaid_count(preferences.getPaid_count()-1);
                                    }
                                    preferences.setIn_progress_count(count);

                                }
                                break;
                            case 4:
                                message = "There is a new order been delivered";
                                if(preferences.getDelivery_count()!=count)
                                {
                                    if(preferences.getDelivery_count()<count)
                                    {
                                        show = true;
                                        //one order moved from in progress count to delivery count
                                        //preferences.setIn_progress_count(preferences.getIn_progress_count()-1);
                                    }
                                    preferences.setDelivery_count(count);
                                }
                                break;
                            case 5:
                                message = "Thanks for using order.";
                                if(preferences.getFinished_count()!=count)
                                {
                                    if(preferences.getFinished_count()<count)
                                    {
                                        show = false;
                                        //one order moved from delivery to finished count
                                        //preferences.setDelivery_count(preferences.getDelivery_count()-1);
                                    }
                                    preferences.setFinished_count(count);

                                }
                                break;
                            default:
                                message = "There is a new order";
                        }
                        if(show)
                            play_notification(message);

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


            }
            else
            {

            }
        }
    }
}
