/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/11/19 6:51 PM
 */
package com.spikingacacia.leta.ui.billing;

import com.android.billingclient.api.BillingClient.SkuType;

import java.util.Arrays;
import java.util.List;

/**
 * Static fields and methods useful for billing
 */
public final class  BillingConstants {
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    //public static final String SKU_PREMIUM = "premium";
    //public static final String SKU_GAS = "gas";

    // SKU for our subscription
    static final String SKU_MONTHLY = "leta_pay_monthly";
    static final String SKU_YEARLY = "leta_pay_yearly";


    //private static final String[] IN_APP_SKUS = {SKU_GAS, SKU_PREMIUM};
    private static final String[] SUBSCRIPTIONS_SKUS = {SKU_MONTHLY, SKU_YEARLY};

    private BillingConstants(){}

    /**
     * Returns the list of all SKUs for the billing type specified
     */
    /*public static final List<String> getSkuList(@SkuType String billingType) {
        return (billingType == SkuType.INAPP) ? Arrays.asList(IN_APP_SKUS)
                : Arrays.asList(SUBSCRIPTIONS_SKUS);
    }*/
    public static final List<String>getSkuList(@SkuType String billingType)
    {
        return Arrays.asList(SUBSCRIPTIONS_SKUS);
    }
}

