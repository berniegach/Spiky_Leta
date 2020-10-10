/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 9/5/20 10:11 PM
 */

package com.spikingacacia.leta.ui.main.wallet;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.MpesaB2C;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WithdrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WithdrawFragment extends Fragment
{
    private static final String ARG_TOTAL = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Double mTotal = 0.0;
    private String TAG = "withdraw_f";
    private OnListFragmentInteractionListener mListener;
    private Button b_withdraw;
    private Preferences preferences;

    public WithdrawFragment()
    {
        // Required empty public constructor
    }

    public static WithdrawFragment newInstance(Double total)
    {
        WithdrawFragment fragment = new WithdrawFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mTotal = getArguments().getDouble(ARG_TOTAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        final EditText e_amount = view.findViewById(R.id.amount);
        final EditText e_mobile = view.findViewById(R.id.number);
        b_withdraw = view.findViewById(R.id.b_withdraw);

        e_mobile.setText(LoginActivity.getServerAccount().getMpesaMobile());
        b_withdraw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String amount = e_amount.getText().toString();
                final String mobile = e_mobile.getText().toString();
                if(TextUtils.isEmpty(amount))
                {
                    e_amount.setError("Please enter amount");
                    return;
                }
                if(Integer.parseInt(amount)<1)
                {
                    e_amount.setError("Amount should be more than 49");
                    return;
                }
                if(Integer.parseInt(amount)>70000)
                {
                    e_amount.setError("Amount should be less than 70000");
                    return;
                }
                if(TextUtils.isEmpty(mobile))
                {
                    e_amount.setError("No mobile number added");
                    return;
                }
                if(!mobile.startsWith("254"))
                {
                    e_mobile.setError("invalid mobile number");
                    return;
                }
                Double d_amount = Double.valueOf(amount);
                Double mpesa_fee = d_amount >1000? 22.40 : 15.27;
                Double commision = LoginActivity.getServerAccount().getCommision()/100*mTotal;
                if( (d_amount+mpesa_fee+commision)>mTotal)
                {
                    e_amount.setError("Insufficient balance");
                    return;
                }
                new AlertDialog.Builder(getContext())
                        .setTitle("Withdraw")
                        .setMessage("Are you sure you want to withdraw the specified amount")
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
                                new B2CTask(amount).execute((Void)null);
                            }
                        }).create().show();


            }
        });

        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener
    {
        void onWithdrawProcess(boolean show_progressbar);
        void withdrawFinished();
    }
    private class B2CTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update=base_url+"m_b2c.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String amount;

        public B2CTask(String amount)
        {
            this.amount = amount;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            if(mListener!=null)
                mListener.onWithdrawProcess(true);
        }



        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email", LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("amount",amount));


            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update,"POST",info);
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
            if(mListener!=null)
                mListener.onWithdrawProcess(false);
            if (successful)
            {
                Snackbar.make(b_withdraw,"Request sent",Snackbar.LENGTH_LONG).show();
                if(mListener!=null)
                    mListener.withdrawFinished();
            }
            else
            {
                Snackbar.make(b_withdraw,"Request was not successful.\nPlease try again.",Snackbar.LENGTH_LONG).show();
            }
        }
    }

}