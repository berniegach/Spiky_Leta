package com.spikingacacia.leta.ui;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by $bernie gach on 4/25/2018.
 **/
public class JSONParser
{
    static InputStream is=null;
    static JSONObject JObj=null;
    static String json=" ";
    String TAG="JSONPARSER";
    //constructor
    public JSONParser()
    {

    }
    /**
     * fucntion get json from url by making HTTP POST or GET method
     */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair>params)
    {
        //making a HTTP request
        try
        {
            //check for request method
            if(method=="POST")
            {
                DefaultHttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse=httpClient.execute(httpPost);
                HttpEntity httpEntity=httpResponse.getEntity();
                is=httpEntity.getContent();
            }
            else if(method=="GET")
            {
                DefaultHttpClient httpClient=new DefaultHttpClient();
                String paramString= URLEncodedUtils.format(params,"utf-8");
                url+="?"+paramString;
                HttpGet httpGet=new HttpGet(url);

                HttpResponse httpResponse=httpClient.execute(httpGet);
                HttpEntity httpEntity=httpResponse.getEntity();
                is=httpEntity.getContent();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e(TAG,""+e.getMessage()); e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            Log.e(TAG,""+e.getMessage()); e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e(TAG,""+e.getMessage()); e.printStackTrace();
        }

        try
        {
            BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder builder=new StringBuilder();
            String line=null;
            while((line=reader.readLine())!=null)
            {
                builder.append(line+"\n");
            }
            is.close();
            json=builder.toString();
        }
        catch (Exception e)
        {
            Log.e(TAG,""+e.getMessage()); e.printStackTrace();
        }
        //try parse the string to json object
        try
        {
            JObj=new JSONObject(json);
        }
        catch (JSONException e)
        {
            Log.e(TAG,""+e.getMessage()); e.printStackTrace(); Log.e(TAG,json);
        }
        return JObj;
    }
}
