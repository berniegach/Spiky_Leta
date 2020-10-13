/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */
package com.spikingacacia.leta.ui.skulist.row;



/**
 * Provider for data that corresponds to a particular row
 */
public interface RowDataProvider {
    SkuRowData getData(int position);
}

