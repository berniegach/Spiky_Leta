/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/9/19 6:36 PM
 */
package com.spikingacacia.leta.ui.skulist.row;

import android.widget.Toast;

import com.android.billingclient.api.BillingClient.SkuType;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.billing.BillingProvider;

/**
 * Implementations of this abstract class are responsible to render UI and handle user actions for
 * skulist rows to render RecyclerView with AcquireFragment's specific UI
 */
public abstract class UiManagingDelegate {

    protected final BillingProvider mBillingProvider;

    public abstract @SkuType String getType();

    public UiManagingDelegate(BillingProvider billingProvider) {
        mBillingProvider = billingProvider;
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        holder.description.setText(data.getDescription());
        holder.price.setText(data.getPrice());
        holder.button.setEnabled(true);
    }

    public void onButtonClicked(SkuRowData data) {
        mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                data.getSkuType());
    }

    protected void showAlreadyPurchasedToast() {
        Toast.makeText(mBillingProvider.getBillingManager().getContext(),
                R.string.alert_already_purchased, Toast.LENGTH_SHORT).show();
    }
}
