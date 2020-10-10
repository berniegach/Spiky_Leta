
/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 8/27/20 3:59 PM
 */

////////////////////////////////////////////////////////////////////
/*
    The following are used for testing
    Shortcode 1	601362 This uses PartyA
    Initiator Name (Shortcode 1)	apitest362
    Security Credential (Shortcode 1)	Safaricom111!
    Shortcode 2	600000 This uses PartyB
    Test MSISDN	254708374149
    ExpiryDate	2020-05-30T19:03:51+03:00
    Lipa Na Mpesa Online Shortcode:	174379
    Lipa Na Mpesa Online Passkey:  bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919
 */
///////////////////////////////////////////////////////////////////
package com.spikingacacia.leta.ui.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.spikingacacia.leta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MpesaB2C
{
    private static String TAG ="Mpesa_utils";
    private static String appKey = "05qwmFjyPrRBJO2SjOAv1xShwXsVSFW8";
    private static String appSecret = "CRfouVSGJUN3AoZF";
    private static String appKeyProduction = "nGGTta6YgrDKVbpcc4m7GlN2KF3hH9XJ";
    private static String appSecretProduction = "5Cq0xmFUxHWmZI7R";
    private static String accessToken;
    private static String urlOAuth2Token =  "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    private static String urlOAuth2TokenProduction =  "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    private static String urlB2CProduction = "https://api.safaricom.co.ke/mpesa/b2c/v1/paymentrequest";
    private static String urlB2C = "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest";
    public MpesaB2C(){
    }

    private static String authenticate() throws IOException, JSONException
    {

        String appKeySecret = appKeyProduction + ":" + appSecretProduction;
        byte[] bytes = appKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP | Base64.URL_SAFE);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlOAuth2TokenProduction)
                .get()
                .addHeader("authorization", "Basic "+encoded)
                .addHeader("cache-control", "no-cache")

                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        JSONObject jsonObject=new JSONObject(response.body().string());
        accessToken = jsonObject.getString("access_token");
        return accessToken;
    }

    public static JSONObject B2CRequest( String initiatorName, String securityCredential,String commandID, String  amount, String partyA,String partyB, String remarks, String queueTimeOutURL, String resultURL, String occassion) throws IOException, JSONException
    {
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("InitiatorName", initiatorName);
        jsonObject.put("SecurityCredential", securityCredential);
        jsonObject.put("CommandID", commandID);
        jsonObject.put("Amount", amount);
        jsonObject.put("PartyA", partyA);
        jsonObject.put("PartyB", partyB);
        jsonObject.put("Remarks", remarks);
        jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
        jsonObject.put("ResultURL", resultURL);
        //jsonObject.put("Occassion", occassion);


        jsonArray.put(jsonObject);

        String requestJson=jsonArray.toString().replaceAll("[\\[\\]]","");


        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestJson);
        Request request = new Request.Builder()
                .url( urlB2CProduction)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer "+authenticate())
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        String jsonData = response.body().string();
        return new JSONObject(jsonData);
    }



}
