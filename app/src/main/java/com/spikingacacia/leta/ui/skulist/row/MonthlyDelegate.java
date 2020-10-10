/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/9/19 6:36 PM
 */
package com.spikingacacia.leta.ui.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.billing.BillingProvider;

import java.util.ArrayList;

/**
 * Handles Ui specific to "monthly gas" - subscription row
 */
public class MonthlyDelegate extends UiManagingDelegate {
    public static final String SKU_ID = "leta_pay_monthly";

    public MonthlyDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType String getType() {
        return SkuType.SUBS;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        if (mBillingProvider.isMonthlySubscribed()) {
            holder.button.setText(R.string.button_own);
        } else {
            int textId = mBillingProvider.isYearlySubscribed()
                    ? R.string.button_change : R.string.button_buy;
            holder.button.setText(textId);
        }
        holder.skuIcon.setImageResource(R.drawable.gold_icon);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
        if (mBillingProvider.isYearlySubscribed()) {
            // If we already subscribed to yearly gas, launch replace flow
            ArrayList<String> currentSubscriptionSku = new ArrayList<>();
            currentSubscriptionSku.add(YearlyDelegate.SKU_ID);
            mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                    currentSubscriptionSku, data.getSkuType());
        } else {
            mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                    data.getSkuType());
        }
    }
}

