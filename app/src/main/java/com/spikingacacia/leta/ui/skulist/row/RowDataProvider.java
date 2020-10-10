/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/9/19 6:36 PM
 */
package com.spikingacacia.leta.ui.skulist.row;



/**
 * Provider for data that corresponds to a particular row
 */
public interface RowDataProvider {
    SkuRowData getData(int position);
}

