/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
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

