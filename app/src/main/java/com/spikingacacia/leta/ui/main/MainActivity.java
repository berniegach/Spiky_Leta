package com.spikingacacia.leta.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.SMenuA;
import com.spikingacacia.leta.ui.SettingsActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.spikingacacia.leta.ui.main.home.ItemDialog;
import com.spikingacacia.leta.ui.main.home.ItemDialogEdit;
import com.spikingacacia.leta.ui.main.home.menuFragment;
import com.spikingacacia.leta.ui.main.orders.SOOverviewF;
import com.spikingacacia.leta.ui.orders.SOOrderF;
import com.spikingacacia.leta.ui.util.GetFilePathFromDevice;


import java.util.LinkedHashMap;

import static com.spikingacacia.leta.ui.LoginA.mGoogleSignInClient;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;

public class MainActivity extends AppCompatActivity implements
        menuFragment.OnListFragmentInteractionListener, ItemDialog.NoticeDialogListener, ItemDialogEdit.NoticeDialogListener,
        SOOverviewF.OnFragmentInteractionListener
{
    private ProgressBar progressBar;
    private View mainFragment;
    private NavController navController;
    public static LinkedHashMap<Integer, Categories> categoriesLinkedHashMap;
    public static LinkedHashMap<Integer, DMenu> menuLinkedHashMap;
    private String TAG ="MainA";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orders, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        progressBar = findViewById(R.id.progress);
        mainFragment = findViewById(R.id.nav_host_fragment);

        categoriesLinkedHashMap = new LinkedHashMap<>();
        menuLinkedHashMap = new LinkedHashMap<>();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            proceedToSettings();
            return true;
        }
        else if( id == R.id.action_sign_out)
        {
            mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Log.d(TAG,"gmail signed out");
                    finish();
                }
            });
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(MainActivity.this)
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
                    }
                }).create().show();
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainFragment.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    void proceedToSettings()
    {
        Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onItemAdded()
    {
        navController.navigate(R.id.navigation_home);
    }
/*
* Implementation of ItemDialogEdit.java
 */
    @Override
    public void onItemUpdated()
    {

    }
    /**
     * implementation of SOOverviewF.java*/
    @Override
    public void onChoiceClicked(int which)
    {
       /* mWhichOrder=which;
        final int format= serverAccount.getOrderFormat();
        switch(which)
        {
            case 1:
                fragmentWhich="Pending";
                break;
            case 2:
                fragmentWhich= format==1?"In Progress":"Payment";
                break;
            case 3:
                fragmentWhich= format==1?"Delivery":"In Progress";
                break;
            case 4:
                fragmentWhich= format==1?"Payment":"Delivery";
                break;
            case 5:
                fragmentWhich="Finished";
        }
        setTitle(fragmentWhich);
        Fragment fragment= SOOrderF.newInstance(1,which);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,fragmentWhich);
        transaction.addToBackStack(fragmentWhich);
        transaction.commit();*/
    }
}