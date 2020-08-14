package com.spikingacacia.leta.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.SettingsActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.spikingacacia.leta.ui.main.dashboard.DashboardFragment;
import com.spikingacacia.leta.ui.main.home.AddItemActivity;
import com.spikingacacia.leta.ui.main.home.EditItemActivity;
import com.spikingacacia.leta.ui.main.home.menuFragment;
import com.spikingacacia.leta.ui.main.orders.OrdersOverviewFragment;
import com.spikingacacia.leta.ui.main.wallet.WalletActivity;
import com.spikingacacia.leta.ui.orders.OrdersActivity;
import com.spikingacacia.leta.ui.qr_code.QrCodeActivity;
import com.spikingacacia.leta.ui.waiters.WaitersActivity;


import java.util.LinkedHashMap;

import static com.spikingacacia.leta.ui.LoginActivity.mGoogleSignInClient;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

public class MainActivity extends AppCompatActivity implements
        menuFragment.OnListFragmentInteractionListener,
        OrdersOverviewFragment.OnFragmentInteractionListener, DashboardFragment.OnListFragmentInteractionListener
{
    private ProgressBar progressBar;
    private View mainFragment;
    private NavController navController;
    public static LinkedHashMap<Integer, Categories> categoriesLinkedHashMap;
    public static LinkedHashMap<Integer, DMenu> menuLinkedHashMap;
    private String TAG ="MainA";
    /*ActivityResultLauncher<Intent> mGetBarcode = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<Instrumentation.ActivityResult>() {
                @Override
                public void onActivityResult(Instrumentation.ActivityResult result)
                {
                    Intent intent = result.getData();
                    try
                    {
                        Barcode barcode = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        barcodeReceived(barcode);
                    }
                    catch (NullPointerException excpetion)
                    {
                        Log.e(TAG,"no barcode");
                        // TODO: remove this its only for testing
                        //onCorrectScan();
                    }

                }
            });*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_orders, R.id.navigation_dashboard, R.id.navigation_messages)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Menu nav_messages = navView.getMenu();
        if(serverAccount.getPersona()==2)
        {
            nav_messages.findItem(R.id.navigation_messages).setVisible(false);
            nav_messages.findItem(R.id.navigation_dashboard).setVisible(false);
        }

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
        MenuItem menu_waiters = menu.findItem(R.id.action_waiter);
        MenuItem menu_qr_codes = menu.findItem(R.id.action_qr_codes);
        MenuItem menu_wallet = menu.findItem(R.id.action_wallet);
        if(serverAccount.getPersona()==2)
        {
            menu_waiters.setVisible(false);
            menu_qr_codes.setVisible(false);
            menu_wallet.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_waiter)
        {
            Intent intent=new Intent(MainActivity.this, WaitersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else if(id == R.id.action_qr_codes)
        {
            Intent intent=new Intent(MainActivity.this, QrCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else if(id == R.id.action_wallet)
        {
            Intent intent=new Intent(MainActivity.this, WalletActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else if( id == R.id.action_sign_out)
        {
            mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Log.d(TAG,"gmail signed out");
                    finishAffinity();
                }
            });
        }
        else if (id == R.id.action_settings)
        {
            proceedToSettings();
            return true;
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


    /**
     * implementation of OrdersOverviewFragment.java*/
    @Override
    public void onChoiceClicked(int which)
    {
        Intent intent=new Intent(MainActivity.this, OrdersActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.putExtra("which",which);
        final int format= serverAccount.getOrderFormat();
        String title="Order";
        switch(which)
        {
            case 1:
                title = "Pending";
                break;
            case 2:
                title= "Payment";
                break;
            case 3:
                title= "In Progress";
                break;
            case 4:
                title= "Delivery";
                break;
            case 5:
                title="Finished";
        }
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }
    /*
     * *implementation of DashboardFragment.java
     * */
    @Override
    public void onCardviewClicked(int which)
    {
        // which can be 3, 4 and 5
        Intent intent=new Intent(MainActivity.this, HostActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.putExtra(HostActivity.ARG_WHICH_FRAGMENT,which);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    /*
     * *implementation of menuFragment.java
     * */
    @Override
    public void onAddNewItemClicked()
    {
        Intent intent=new Intent(MainActivity.this, AddItemActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    public void onEditItemClicked(DMenu dMenu)
    {
        Intent intent=new Intent(MainActivity.this, EditItemActivity.class);
        //prevent this activity from flickering as we call the next one
        intent.putExtra("item",dMenu);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}