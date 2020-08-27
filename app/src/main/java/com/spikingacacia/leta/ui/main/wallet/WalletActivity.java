package com.spikingacacia.leta.ui.main.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.spikingacacia.leta.R;


public class WalletActivity extends AppCompatActivity implements
        WalletHomeFragment.OnListFragmentInteractionListener,
        WithdrawFragment.OnListFragmentInteractionListener
{
    private String TAG = "wallet_a";
    private ProgressBar progressBar;
    private View mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle("Wallet");

        progressBar = findViewById(R.id.progress);
        mainFragment = findViewById(R.id.base);

        Fragment fragment= WalletHomeFragment.newInstance("","");
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.base,fragment,"cash");
        transaction.commit();

    }
/*
*   implementation of WalletHomeFragment.java
 */
    @Override
    public void onWithdrawClicked(Double total)
    {
        Fragment fragment= WithdrawFragment.newInstance(total);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base,fragment,"cash");
        transaction.addToBackStack(null);
        transaction.commit();
    }
    void showProgress(boolean show)
    {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
        mainFragment.setVisibility( show? View.INVISIBLE :View.VISIBLE);
    }
    /*
     *   implementation of WithdrawFragment.java
     */

    @Override
    public void onWithdrawProcess(boolean show_progressbar)
    {
        showProgress(show_progressbar);
    }

    @Override
    public void withdrawFinished()
    {
        onBackPressed();
    }
}