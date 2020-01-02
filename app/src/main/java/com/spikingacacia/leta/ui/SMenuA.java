package com.spikingacacia.leta.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.inventory.SIInventoryA;
import com.spikingacacia.leta.ui.messages.SMMessageListActivity;
import com.spikingacacia.leta.ui.orders.SOOrdersA;
import com.spikingacacia.leta.ui.reports.SRReportsA;

import static com.spikingacacia.leta.ui.LoginA.loginProgress;
import static com.spikingacacia.leta.ui.LoginA.sFinalProgress;

public class SMenuA extends AppCompatActivity
implements SMenuF.OnFragmentInteractionListener{
    private boolean runRate=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_smenu);

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
        if(LoginA.sellerAccount.getUsername().length()<2 || LoginA.sellerAccount.getUsername().contentEquals("null"))
        {
            ((TextView)findViewById(R.id.who)).setText("Please go to settings and set the business name...");
        }
        else
            ((TextView)findViewById(R.id.who)).setText(LoginA.sellerAccount.getUsername());
        if(LoginA.currentSubscription.length()>10)
            ((TextView)findViewById(R.id.trial)).setText(LoginA.currentSubscription);
        else
            ((TextView)findViewById(R.id.trial)).setVisibility(View.GONE);
        checkFields();
        if(runRate)
        {
            AppRater.app_launched(this);
            runRate=false;
        }

    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        new AlertDialog.Builder(SMenuA.this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to exit?")
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
                        finishAffinity();
                        //Intent intent=new Intent(Intent.ACTION_MAIN);
                        // intent.addCategory(Intent.CATEGORY_HOME);
                        // startActivity(intent);
                    }
                }).create().show();
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
            if(LoginA.sCategoriesList.size()==0)
                message="Please create the major categories eg food, drinks etc.\nGo to "+
                        "\\u279e Inventory  \\u279e Options(upper right corner) \\u279e add";
            else if(LoginA.sGroupsList.size()==0)
                message="Please create the groups eg pizza, burgers etc.\nGo to "+
                        "\\u279e Inventory  \\u279e  category \\u279e Options(upper right corner) \\u279e add";
            else if(LoginA.sItemsList.size()==0)
                message="Please create the items eg pepperoni, cheese burger etc.\nGo to "+
                        " \\u279e Inventory  \\u279e  category \\u279e  group \\u279e Options(upper right corner) \\u279e add";
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

}
