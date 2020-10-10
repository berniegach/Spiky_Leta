/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/9/19 6:36 PM
 */
package com.spikingacacia.leta.ui.billing;



/**
 * An interface that provides an access to BillingLibrary methods
 */
public interface BillingProvider {
    BillingManager getBillingManager();
   // boolean isPremiumPurchased();
    boolean isMonthlySubscribed();
    //boolean isTankFull();
    boolean isYearlySubscribed();
}

