/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/9/19 6:36 PM
 */
package com.spikingacacia.leta.ui.skulist;

import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;


import com.spikingacacia.leta.ui.skulist.row.RowDataProvider;
import com.spikingacacia.leta.ui.skulist.row.RowViewHolder;
import com.spikingacacia.leta.ui.skulist.row.SkuRowData;
import com.spikingacacia.leta.ui.skulist.row.UiManager;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Adapter for a RecyclerView that shows SKU details for the app.
 * <p>
 *     Note: It's done fragment-specific logic independent and delegates control back to the
 *     specified handler (implemented inside AcquireFragment in this example)
 * </p>
 */
public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowDataProvider {
    /**
     * Types for adapter rows
     */
    @Retention(SOURCE)
    @IntDef({TYPE_HEADER, TYPE_NORMAL})
    public @interface RowTypeDef {}
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private UiManager mUiManager;
    private List<SkuRowData> mListData;

    void setUiManager(UiManager uiManager) {
        mUiManager = uiManager;
    }

    void updateData(List<SkuRowData> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    @Override
    public @RowTypeDef int getItemViewType(int position) {
        return mListData == null ? TYPE_HEADER : mListData.get(position).getRowType();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, @RowTypeDef int viewType) {
        return mUiManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        mUiManager.onBindViewHolder(getData(position), holder);
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public SkuRowData getData(int position) {
        return mListData == null ? null : mListData.get(position);
    }
}

