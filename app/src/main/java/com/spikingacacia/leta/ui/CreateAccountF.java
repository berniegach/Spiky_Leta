package com.spikingacacia.leta.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.spikingacacia.leta.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CreateAccountF extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_VERIFY = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int mVerify;
    private String mEmailVerify;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private String url_create_account_seller =LoginA.base_url+"create_seller_account.php";
    private String url_update_password = LoginA.base_url+"update_password_seller_account.php";
    private String TAG="CreateAccountF";


    private OnFragmentInteractionListener mListener;
    private AutoCompleteTextView mEmail;
    private Button bVerifyEmail;
    private EditText mPassword;
    private EditText mVerifyPassword;

    JSONParser jsonParser;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPreferencesEditor;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegisterTaskSeller mAuthTaskC = null;


    public CreateAccountF() {
        // Required empty public constructor
    }

    public static CreateAccountF newInstance(int verify, String email) {
        CreateAccountF fragment = new CreateAccountF();
        Bundle args = new Bundle();
        args.putInt(ARG_VERIFY, verify);
        args.putString(ARG_PARAM2, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVerify = getArguments().getInt(ARG_VERIFY);
            mEmailVerify = getArguments().getString(ARG_PARAM2);
        }
        // handler=new InHandler(getContext());
        jsonParser=new JSONParser();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_create_account, container, false);
        loginPreferences=getContext().getSharedPreferences("loginPrefs",MODE_PRIVATE);
        loginPreferencesEditor=loginPreferences.edit();
        //edittexts
        mEmail = view.findViewById(R.id.email);
        bVerifyEmail=view.findViewById(R.id.verify_email);
        mPassword = view.findViewById(R.id.password);
        mVerifyPassword = view.findViewById(R.id.verifypassword);
        final Button mRegisterButton = view.findViewById(R.id.register_button);
        //set the drawablelft programmatically coz of devices under api 21
        CommonHelper.setVectorDrawable(getContext(),mEmail,R.drawable.ic_email,0,0,0);
        CommonHelper.setVectorDrawable(getContext(),mPassword,R.drawable.ic_password,0,0,0);
        CommonHelper.setVectorDrawable(getContext(),mVerifyPassword,R.drawable.ic_password,0,0,0);

        bVerifyEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    InputMethodManager inputMethodManager=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    Toast.makeText(getContext(),"Please wait",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e(TAG,"error in hiding keyboard"+e.getMessage());
                }
                if(mVerify==1)
                {
                    bVerifyEmail.setVisibility(View.GONE);
                    mPassword.setVisibility(View.VISIBLE);
                    mVerifyPassword.setVisibility(View.VISIBLE);
                    mRegisterButton.setVisibility(View.VISIBLE);
                }
                else if(mVerify==2)
                {
                    bVerifyEmail.setVisibility(View.GONE);
                    mPassword.setVisibility(View.VISIBLE);
                    mVerifyPassword.setVisibility(View.VISIBLE);
                    mRegisterButton.setVisibility(View.VISIBLE);
                    mRegisterButton.setText("Confirm");
                }
                else
                {
                    final String email = mEmail.getText().toString();
                    // Check for a valid email address.
                    if (TextUtils.isEmpty(email) || !isEmailValid(email))
                    {
                        mEmail.setError(getString(R.string.error_field_required));
                        mEmail.requestFocus();
                    }
                    else
                    {
                        verifyButtonClicked(email);
                    }
                }

            }
        });
        if(mVerify==1)
        {
            bVerifyEmail.setText("Proceed");
            mEmail.setText(mEmailVerify);
            mEmail.setEnabled(false);
        }
        else if(mVerify==2)
        {
            bVerifyEmail.setText("Change Password");
            mEmail.setText(mEmailVerify);
            mEmail.setEnabled(false);
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    InputMethodManager inputMethodManager=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    Toast.makeText(getContext(),"Please wait",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e(TAG,"error in hiding keyboard"+e.getMessage());
                }
                attemptRegister();
            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void verifyButtonClicked(final String email)
    {
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        //The link will redirect the user to this URL if the app is not installed on their device and the app was not able to be installed
                        .setUrl("https://spiking-acacia-1548914219992.firebaseapp.com")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.spikingacacia.leta",
                                true, /* installIfNotAvailable */
                                "1"    /* minimumVersion */)
                        .build();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(mEmail,"Email sent\nPlease verify the email address to continue.",Snackbar.LENGTH_LONG).show();
                            loginPreferencesEditor.putBoolean("verify",true);
                            loginPreferencesEditor.putString("email_verify",email);
                            loginPreferencesEditor.commit();
                            Log.d(TAG, "Email sent.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e(TAG,"2"+e.getMessage());
            }
        });
    }
    private void attemptRegister()
    {
        if (mAuthTaskC != null) {
            return;
        }
        //reset errors
        mEmail.setError(null);
        mPassword.setError(null);
        mVerifyPassword.setError(null);
        // Store values at the time of the login attempt.
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String vPassword=mVerifyPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }
        if(!password.contentEquals(vPassword))
        {
            mVerifyPassword.setError("The password is not the same");
            focusView=mVerifyPassword;
            cancel=true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
            {
                if(mVerify==2)
                    new UpdatePassword(email,password).execute((Void)null);
                else
                {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    String message="You are about to create an account as a Restaurant";
                    new AlertDialog.Builder(getContext())
                            .setTitle("Create The Account")
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
                                    mAuthTaskC = new RegisterTaskSeller(email, password);
                                    mAuthTaskC.execute((Void) null);
                                }
                            })
                            .create().show();
                }


        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRegisterFinished();
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegisterTaskSeller extends AsyncTask<Void, Void, Boolean>
    {
        private final String mEmail;
        private final String mPassword;
        private int success;

        RegisterTaskSeller(String email, String password) {
            mEmail = email;
            mPassword = password;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Account creation started. Please wait...",Toast.LENGTH_SHORT).show();
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
            mAuthTaskC = null;
            if (successful)
            {
                Toast.makeText(getContext(),"Account succesfully created",Toast.LENGTH_SHORT).show();
                loginPreferencesEditor.putBoolean("verify",false);
                loginPreferencesEditor.commit();
                if(mListener!=null)
                    mListener.onRegisterFinished();
            }
            else if(success==-1)
            {
                Toast.makeText(getContext(),"Unable to create the account\nEmail already registered",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Registration failed",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onCancelled() {
            mAuthTaskC = null;
        }
    }
    private boolean isEmailValid(String email)
    {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password)
    {
        return password.length() > 4;
    }
    public class UpdatePassword extends AsyncTask<Void, Void, Boolean>
    {
        private final String mEmail;
        private final String mPassword;
        private int success;

        UpdatePassword(String email, String password)
        {
            mEmail = email;
            mPassword = password;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getContext(),"Updating password. Please wait...",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair>info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("email",mEmail));
            info.add(new BasicNameValuePair("password",mPassword));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_password,"POST",info);
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
            if (successful)
            {
                Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                loginPreferencesEditor.putBoolean("reset_password",false);
                loginPreferencesEditor.commit();
                if(mListener!=null)
                    mListener.onRegisterFinished();
            }

            else
                Toast.makeText(getContext(),"Password Change failed.Please try again",Toast.LENGTH_SHORT).show();

        }
    }

}
