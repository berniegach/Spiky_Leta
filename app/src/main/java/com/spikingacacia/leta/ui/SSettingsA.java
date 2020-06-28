package com.spikingacacia.leta.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;

import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;

import java.util.Locale;

public class SSettingsA extends AppCompatActivity
        implements    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    private static final String TITLE_TAG = "Settings";

    public static boolean permissionsChanged;
    public static String permissions;
    public static Bitmap profilePic;
    Preferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else
        {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener()
                {
                    @Override
                    public void onBackStackChanged()
                    {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                        {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //get the profile pic
        String url= LoginActivity.base_url+"src/sellers/"+String.format("%s/pics/prof_pic",makeName(LoginActivity.serverAccount.getId()))+".jpg";
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        profilePic=response;
                        //imageView.setImageBitmap(response);
                        Log.d("volley","succesful");
                    }
                }, 0, 0, null,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("voley",""+e.getMessage()+e.toString());
                    }
                });
        RequestQueue request2 = Volley.newRequestQueue(getBaseContext());
        request2.add(request);

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }
    @Override
    public boolean onSupportNavigateUp()
    {
        if (getSupportFragmentManager().popBackStackImmediate())
        {
            return true;
        }
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref)
    {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }
    public static class HeaderFragment extends PreferenceFragmentCompat
    {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_sheaders, rootKey);
            Preference preference_location=findPreference("location");
            Preference preference_account=findPreference("account");
            if(LoginActivity.serverAccount.getPersona()==1)
            {
                preference_location.setVisible(false);
                preference_account.setVisible(false);
            }
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
    {
        private Preferences preferences;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_sgeneral, rootKey);





            //password change

            /*preference_password.setSummary(passwordStars(LoginActivity.serverAccount.getPassword()));
            preference_password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    final AlertDialog dialog;
                    AlertDialog.Builder builderPass=new AlertDialog.Builder(context);
                    builderPass.setTitle("Change Password");
                    TextInputLayout lOld=new TextInputLayout(context);
                    lOld.setGravity(Gravity.CENTER);
                    final EditText oldPassword=new EditText(context);
                    oldPassword.setPadding(20,10,20,10);
                    oldPassword.setTextSize(14);
                    oldPassword.setPadding(5,0,5,0);
                    lOld.addView(oldPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lNew=new TextInputLayout(context);
                    lNew.setGravity(Gravity.CENTER);
                    final EditText newPassword=new EditText(context);
                    newPassword.setPadding(20,10,20,10);
                    newPassword.setTextSize(14);
                    newPassword.setPadding(5,0,5,0);
                    lNew.addView(newPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextInputLayout lConfirm=new TextInputLayout(context);
                    lConfirm.setGravity(Gravity.CENTER);
                    final EditText confirmPassword=new EditText(context);
                    confirmPassword.setPadding(20,10,20,10);
                    confirmPassword.setTextSize(14);
                    confirmPassword.setPadding(5,0,5,0);
                    lConfirm.addView(confirmPassword,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    oldPassword.setHint("Old Password");
                    newPassword.setHint("New Password");
                    confirmPassword.setHint("Confirm Password");
                    oldPassword.setError(null);
                    newPassword.setError(null);
                    confirmPassword.setError(null);
                    LinearLayout layoutPassword=new LinearLayout(context);
                    layoutPassword.setOrientation(LinearLayout.VERTICAL);
                    layoutPassword.addView(lOld);
                    layoutPassword.addView(lNew);
                    layoutPassword.addView(lConfirm);
                    builderPass.setView(layoutPassword);
                    builderPass.setPositiveButton("Change", null);
                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog=builderPass.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialogInterface)
                        {
                            Button button=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    String old=oldPassword.getText().toString();
                                    String newPass=newPassword.getText().toString();
                                    String confirm=confirmPassword.getText().toString();
                                    if(!old.contentEquals(LoginActivity.serverAccount.getPassword()))
                                    {
                                        oldPassword.setError("Incorrect old password");
                                    }
                                    else if(newPass.length()<4)
                                    {
                                        newPassword.setError("New password too short");
                                    }
                                    else if(!newPass.contentEquals(confirm))
                                    {
                                        confirmPassword.setError("Password not the same");
                                    }
                                    else
                                    {
                                        tempServerAccount.setPassword(newPass);
                                        settingsChanged=true;
                                        preference_password.setSummary(passwordStars(newPass));
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                    return false;
                }
            });*/




        }
        private String passwordStars(String pass)
        {
            String name="";
            for(int count=0; count<pass.length(); count+=1)
                name+="*";
            return name;
        }
    }
    /**
     * This fragment shows location preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LocationPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_slocation,rootKey);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            //countries
            /*String countryCode= LoginActivity.serverAccount.getCountry();
            ListPreference pref_countries=( findPreference("countries"));
            pref_countries.setEntries(getCountriesList());
            pref_countries.setEntryValues(getCountriesListValues());
            pref_countries.setSummary(countryCode.contentEquals("null")?"Please set the country":getCountryFromCode(countryCode));
            pref_countries.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    String s=o.toString();
                    tempServerAccount.setCountry(s);
                    settingsChanged=true;
                    preference.setSummary(getCountryFromCode(s));
                    return false;
                }
            });*/



        }


        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }
    /**
     * This fragment shows feedback preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FeedBackPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_sfeedback,rootKey);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            final Preference pref_ask= (Preference) findPreference("feedback_ask");
            final Preference pref_report= (Preference) findPreference("feedback_report");
            pref_ask.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("New feature");
                    return true;
                }
            });
            pref_report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    composeEmail("Reporting a problem");
                    return true;
                }
            });

        }


        private void composeEmail(String subject)
        {
            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));//only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"spikingacacia@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            /*if(intent.resolveActivity(getPackageManager())!=null)
            {
                startActivity(intent);
            }*/
            startActivity(intent);
        }




    }
    /**
     * This fragment shows account preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_saccount,rootKey);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            /*final Preference pref_del= (Preference) findPreference("account_delete");
            pref_del.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    TextInputLayout textInputLayout=new TextInputLayout(context);
                    textInputLayout.setGravity(Gravity.CENTER);
                    final EditText password=new EditText(context);
                    password.setPadding(20,10,20,10);
                    password.setTextSize(14);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setHint("Password");
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setError(null);
                    textInputLayout.addView(password,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    new AlertDialog.Builder(context)
                            .setTitle("Enter the password")
                            .setView(textInputLayout)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    String pass=password.getText().toString();
                                    if(!pass.contentEquals(LoginActivity.serverAccount.getPassword()))
                                    {
                                        password.setError("Incorrect password");
                                        Toast.makeText(context,"Incorrect password.",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        new AlertDialog.Builder(context)
                                                .setTitle("DELETE ACCOUNT")
                                                .setMessage("Are you sure you want to delete this account?" +
                                                        "\nThis will permanently remove your information")
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        new DeleteAccount().execute((Void)null);
                                                    }
                                                }).create().show();
                                    }
                                }
                            }).create().show();

                    return true;
                }
            });*/

        }


    }
    /**
     * This fragment shows help preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HelpPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_shelp,rootKey);
        }





    }

    /**
     * This fragment shows location preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrivilegesPreferenceFragment extends PreferenceFragmentCompat
    {
         @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_cprivileges);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("online_visibility"));
            //bindPreferenceSummaryToValue(findPreference("online_delivery"));
            

        }


        private CharSequence[] getCountriesList()
        {
            CharSequence[] countriesList;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesList=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                countriesList[count]=countryName;
            }
            return countriesList;
        }
        private  CharSequence[] getCountriesListValues()
        {
            CharSequence[] countriesListValues;
            String[] isoCountryCodes= Locale.getISOCountries();
            countriesListValues=new CharSequence[isoCountryCodes.length];
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                countriesListValues[count]=countryCode;
            }
            return countriesListValues;
        }
        private String getCountryFromCode(String code)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryCode.contentEquals(code))
                    return countryName;
            }
            return "unknown";
        }
        private String getCodeFromCountry(String country)
        {
            String[] isoCountryCodes= Locale.getISOCountries();
            for(int count=0; count<isoCountryCodes.length; count+=1)
            {
                String countryCode=isoCountryCodes[count];
                Locale locale=new Locale("",countryCode);
                String countryName=locale.getDisplayCountry();
                if (countryName.contentEquals(country))
                    return countryCode;
            }
            return "unknown";
        }
    }*/



    private String makeName(int id)
    {
        String letters=String.valueOf(id);
        char[] array=letters.toCharArray();
        String name="";
        for(int count=0; count<array.length; count++)
        {
            switch (array[count])
            {
                case '0':
                    name+="zero";
                    break;
                case '1':
                    name+="one";
                    break;
                case '2':
                    name+="two";
                    break;
                case '3':
                    name+="three";
                    break;
                case '4':
                    name+="four";
                    break;
                case '5':
                    name+="five";
                    break;
                case '6':
                    name+="six";
                    break;
                case '7':
                    name+="seven";
                    break;
                case '8':
                    name+="eight";
                    break;
                case '9':
                    name+="nine";
                    break;
                default :
                    name+="NON";
            }
        }
        return name;
    }




}
