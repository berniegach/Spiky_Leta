/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui;


import android.util.Log;

import com.android.billingclient.api.Purchase;
import com.spikingacacia.leta.ui.billing.BillingManager;
import com.spikingacacia.leta.ui.skulist.row.MonthlyDelegate;
import com.spikingacacia.leta.ui.skulist.row.YearlyDelegate;


import java.util.List;

/**
 * Handles control logic of the LoginActivity
 */
public class MainViewController {
    private static final String TAG = "MainViewController";

    // Graphics for the gas gauge
    /*private static int[] TANK_RES_IDS = { R.drawable.gas0, R.drawable.gas1, R.drawable.gas2,
            R.drawable.gas3, R.drawable.gas4 };*/

    // How many units (1/4 tank is our unit) fill in the tank.
    private static final int TANK_MAX = 4;

    private final UpdateListener mUpdateListener;
    private LoginActivity mActivity;

    // Tracks if we currently own subscriptions SKUs
    private boolean mMonthly;
    private boolean mYearly;

    // Tracks if we currently own a premium car
    //private boolean mIsPremium;

    // Current amount of gas in tank, in units
    //private int mTank;

    public MainViewController(LoginActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
        //loadData();
    }

    /*public void useGas() {
        mTank--;
        saveData();
        Log.d(TAG, "Tank is now: " + mTank);
    }*/

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

   /* public boolean isTankEmpty() {
        return mTank <= 0;
    }*/

   /* public boolean isTankFull() {
        return mTank >= TANK_MAX;
    }*/

    /*public boolean isPremiumPurchased() {
        return mIsPremium;
    }*/

    public boolean isMonthlySubscribed() {
        Log.d(TAG,"subscribed monthly " +mMonthly);
        return mMonthly;
    }

    public boolean isYearlySubscribed() {
        Log.d(TAG,"yearly " +mYearly);
        return mYearly;
    }

    /*public @DrawableRes int getTankResId() {
        int index = (mTank >= TANK_RES_IDS.length) ? (TANK_RES_IDS.length - 1) : mTank;
        return TANK_RES_IDS[index];
    }*/

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            mActivity.onBillingManagerSetupFinished();
        }

       /* @Override
        public void onConsumeFinished(String token, @BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
            // check if token corresponding to the expected sku was consumed.
            // If you have more than one sku, you probably need to validate that the token matches
            // the SKU you expect.
            // It could be done by maintaining a map (updating it every time you call consumeAsync)
            // of all tokens into SKUs which were scheduled to be consumed and then looking through
            // it here to check which SKU corresponds to a consumed token.
            if (result == BillingResponse.OK) {
                // Successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                saveData();
                mActivity.alert(R.string.alert_fill_gas, mTank);
            } else {
                mActivity.alert(R.string.alert_error_consuming, result);
            }

            mActivity.showRefreshedUi();
            Log.d(TAG, "End consumption flow.");
        }*/

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            mMonthly = false;
            mYearly = false;

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    /*case PremiumDelegate.SKU_ID:
                        Log.d(TAG, "You are Premium! Congratulations!!!");
                        mIsPremium = true;
                        break;*/
                    /*case GasDelegate.SKU_ID:
                        Log.d(TAG, "We have gas. Consuming it.");
                        // We should consume the purchase and fill up the tank once it was consumed
                        mActivity.getBillingManager().consumeAsync(purchase.getPurchaseToken());
                        break;*/
                    case MonthlyDelegate.SKU_ID:
                        mMonthly = true;
                        break;
                    case YearlyDelegate.SKU_ID:
                        mYearly = true;
                        break;
                }
            }

            mActivity.showRefreshedUi();
        }
    }

    /**
     * Save current tank level to disc
     *
     * Note: In a real application, we recommend you save data in a secure way to
     * prevent tampering.
     * For simplicity in this sample, we simply store the data using a
     * SharedPreferences.
     */
    /*private void saveData() {
        SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();
        spe.putInt("tank", mTank);
        spe.apply();
        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
    }*/

    /*private void loadData() {
        SharedPreferences sp = mActivity.getPreferences(MODE_PRIVATE);
        mTank = sp.getInt("tank", 2);
        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
    }*/
}