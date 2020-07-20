package com.spikingacacia.leta.ui.qr_code;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.database.QrCodes;
import com.spikingacacia.leta.ui.database.Waiters;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.waiters.WaiterFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

public class QrCodeActivity extends AppCompatActivity
{
    private static final int PERMISSION_REQUEST_STORAGE=2;
    private LinkedHashMap<Integer,QrCodes> qrCodesLinkedHashMap;
    private String TAG = "qrcode_a";
    private EditText e_tables;
    private Button b_assign;
    private int tableCount = 0;
    private ProgressBar progressBar;
    private View mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setTitle("QR Codes");

        progressBar = findViewById(R.id.progress);
        mainView = findViewById(R.id.main);

        e_tables= findViewById(R.id.tables);
        Button b_scan = findViewById(R.id.scan);
        b_assign = findViewById(R.id.assign);
        Button b_print = findViewById(R.id.print);

        e_tables.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().contentEquals(""))
                    return;
                int tables = Integer.parseInt(s.toString());
                if(tables!=qrCodesLinkedHashMap.size())
                    b_assign.setVisibility(View.VISIBLE);
                else
                    b_assign.setVisibility(View.GONE);
                tableCount = tables;
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        b_print.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveQrCodes();
            }
        });
        b_assign.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assignQrCodes();
            }
        });

        qrCodesLinkedHashMap = new LinkedHashMap<>();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new QrCodeTask().execute((Void)null);
    }
    void assignQrCodes()
    {
        showProgress(true);
        //get the tables which have no assignment
        List<Integer> newTablesList = new ArrayList<>();
        for(int c=1; c<=tableCount; c++)
        {
            if(qrCodesLinkedHashMap.get(c)==null)
                newTablesList.add(c);
        }
        final List<Integer> tablesToRemove = new ArrayList<>();
        for(int c=tableCount+1; c<=qrCodesLinkedHashMap.size(); c++)
        {
            tablesToRemove.add(c);
            Log.d(TAG,"INTABLES "+c);
        }
        if(newTablesList.size()>0)
        {
            new AddQrCodesTask(newTablesList).execute((Void)null);
        }
        else if(tablesToRemove.size()>0)
        {
            new AlertDialog.Builder(QrCodeActivity.this)
                    .setTitle("Delete")
                    .setMessage("The new number of tables is less than what you had before\n" +
                            "Are you sure you want to update?")
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
                            new DeleteQrCodesTask(tablesToRemove).execute((Void)null);
                        }
                    }).create().show();
        }
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainView.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    private void saveQrCodes()
    {
        showProgress(true);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    for (LinkedHashMap.Entry<Integer, QrCodes> set : qrCodesLinkedHashMap.entrySet())
                    {
                        int id = set.getKey();
                        QrCodes qrCodes = set.getValue();
                        String table_number = "#"+qrCodes.getTableNumber();
                        String filename = "Image_Order_QR_table_" + String.valueOf(id) + ".jpg";
                        final Bitmap bitmap_qr = Encoder.encode(getBaseContext(), qrCodes.getUrlCode(), table_number);
                        save_bitmap(bitmap_qr, filename);
                    }
                }
            };
            thread.start();
        }
        //request the permission
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }
        showProgress(false);
        Toast.makeText(this,"QR Code saved", Toast.LENGTH_SHORT).show();
    }
    private void save_bitmap(Bitmap bitmap, String file_name)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/order");
        myDir.mkdirs();
        int n = 10000;
        File file = new File(myDir, file_name);
        Log.d(TAG, "" + file);
        if (file.exists())
            file.delete();
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            Log.e(TAG,"error "+e.getMessage());
        }
    }

    private class QrCodeTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_qrs = base_url + "get_qr_codes.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            qrCodesLinkedHashMap.clear();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_qrs,"POST",info);
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("info");
                    for(int count=0; count<itemsArrayList.length(); count+=1)
                    {
                        JSONObject json_object_qrs=itemsArrayList.getJSONObject(count);
                        int id=json_object_qrs.getInt("id");
                        int table_number=json_object_qrs.getInt("table_number");
                        String url_code=json_object_qrs.getString("url_code");
                        String date_added = json_object_qrs.getString("date_added");
                        String date_changed = json_object_qrs.getString("date_changed");

                        QrCodes qrCodes = new QrCodes(id,table_number,url_code,date_added,date_changed);
                        qrCodesLinkedHashMap.put(table_number,qrCodes);

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
        protected void onPostExecute(final Boolean successful)
        {


            if (successful)
            {
                e_tables.setText(String.valueOf(qrCodesLinkedHashMap.size()));
                tableCount = qrCodesLinkedHashMap.size();
            }
            else
            {

            }
        }
    }
    public class AddQrCodesTask extends AsyncTask<Void, Void, Boolean>
    {
        String url_add = base_url + "add_qr_codes.php";
        private JSONParser jsonParser;
        private String TAG_SUCCESS = "success";
        private String TAG_MESSAGE = "message";
        private String codes="";

        AddQrCodesTask(List<Integer> codesList)
        {
            Toast.makeText(getBaseContext(),"Please wait...",Toast.LENGTH_SHORT).show();
            jsonParser = new JSONParser();
            for(int c =0; c<codesList.size(); c++)
            {
                codes+=Integer.toString(codesList.get(c));
                if(c!=codesList.size()-1)
                    codes+=",";

            }
            Log.d(TAG,""+codes);
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            info.add(new BasicNameValuePair("table_numbers", codes));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add,"POST",info);
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
        protected void onPostExecute(final Boolean successful)
        {
            showProgress(false);
            if(successful)
            {

                Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show();
                new QrCodeTask().execute((Void)null);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Error. Please try again",Toast.LENGTH_SHORT).show();
            }

        }
    }
    public class DeleteQrCodesTask extends AsyncTask<Void, Void, Boolean>
    {
        String url_add = base_url + "remove_qr_codes.php";
        private JSONParser jsonParser;
        private String TAG_SUCCESS = "success";
        private String TAG_MESSAGE = "message";
        private String codes="";

        DeleteQrCodesTask(List<Integer> codesList)
        {
            Toast.makeText(getBaseContext(),"Please wait...",Toast.LENGTH_SHORT).show();
            jsonParser = new JSONParser();
            for(int c =0; c<codesList.size(); c++)
            {
                codes+=Integer.toString(codesList.get(c));
                if(c!=codesList.size()-1)
                    codes+=",";

            }
            Log.d(TAG,""+codes);
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            info.add(new BasicNameValuePair("table_numbers", codes));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add,"POST",info);
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
        protected void onPostExecute(final Boolean successful)
        {
            showProgress(false);
            if(successful)
            {

                Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show();
                new QrCodeTask().execute((Void)null);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Error. Please try again",Toast.LENGTH_SHORT).show();
            }

        }
    }
}