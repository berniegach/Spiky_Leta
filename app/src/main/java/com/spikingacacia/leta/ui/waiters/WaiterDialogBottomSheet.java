/*
 * Created by Benard Gachanja on 10/19/20 7:15 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/19/20 7:15 PM
 */

package com.spikingacacia.leta.ui.waiters;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.spikingacacia.leta.R;

import java.io.Serializable;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     WaiterDialogBottomSheet.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class WaiterDialogBottomSheet extends BottomSheetDialogFragment
{

    // TODO: Customize parameter argument names
    private static final String ARG_LISTENER = "param1";

    public interface AddWaiterInterface extends Serializable
    {
        public void onWaiterAdd(String email);
    }
    private AddWaiterInterface addWaiterInterface;
    public static WaiterDialogBottomSheet newInstance( AddWaiterInterface addWaiterInterface)
    {
        final WaiterDialogBottomSheet fragment = new WaiterDialogBottomSheet();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_LISTENER, addWaiterInterface);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            addWaiterInterface = (AddWaiterInterface) getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_waiter_dialog_bottom_sheet_list_dialog, container, false);
        MaterialButton b_add = view.findViewById(R.id.button_add);
        EditText e_email = view.findViewById(R.id.edit_email);
        b_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = e_email.getText().toString();
                if(TextUtils.isEmpty(str))
                {
                    e_email.setError("Empty");
                }
                else if(!str.contains("@"))
                {
                    e_email.setError("Invalid");
                }
                else
                {
                    if(addWaiterInterface!=null)
                        addWaiterInterface.onWaiterAdd(str);
                    dismiss();
                }
            }
        });

        return view;
    }




}