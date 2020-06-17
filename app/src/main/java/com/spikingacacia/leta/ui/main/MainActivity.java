package com.spikingacacia.leta.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.SMenuA;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.spikingacacia.leta.ui.main.home.ItemDialog;
import com.spikingacacia.leta.ui.main.home.menuFragment;
import com.spikingacacia.leta.ui.util.GetFilePathFromDevice;


import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity implements
        menuFragment.OnListFragmentInteractionListener, ItemDialog.NoticeDialogListener
{
    private ProgressBar progressBar;
    private View mainFragment;
    public static LinkedHashMap<Integer, Categories> categoriesLinkedHashMap;
    public static LinkedHashMap<Integer, DMenu> menuLinkedHashMap;
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        progressBar = findViewById(R.id.progress);
        mainFragment = findViewById(R.id.nav_host_fragment);

        categoriesLinkedHashMap = new LinkedHashMap<>();
        menuLinkedHashMap = new LinkedHashMap<>();
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

    @Override
    public void onItemAdded()
    {

    }
}