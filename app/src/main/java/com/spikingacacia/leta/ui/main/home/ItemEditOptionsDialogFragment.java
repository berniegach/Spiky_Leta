/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.main.home;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.DMenu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ItemEditOptionsDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ItemEditOptionsDialogFragment extends BottomSheetDialogFragment
{

    private static final String ARG_MENU_ITEM = "arg1";
    private static final String ARG_MENU_INDEX = "arg2";
    private static final String ARG_MENU_LIST = "arg3";
    private static final String ARG_LISTENER = "arg4";
    private static final String ARG_LISTENER_UPDATE = "arg5";
    private DMenu dMenu;
    private int menu_index;
    private List<DMenu> dMenuList;
    private EventListener eventListener;
    public interface EventListener extends Serializable
    {
        void onLinkItem(final DMenu dMenu,final int menu_index,  List<DMenu> dMenuList);
        void onEditItemClicked(final DMenu dMenu);
    }
    private UpdateListener updateListener;
    public interface UpdateListener extends Serializable
    {
        void onItemAvailailabilityChanged(int menu_id, boolean changed);
    }
    public static ItemEditOptionsDialogFragment newInstance(DMenu dMenu, int menu_index, EventListener eventListener,  List<DMenu> dMenuList, UpdateListener updateListener)
    {
        final ItemEditOptionsDialogFragment fragment = new ItemEditOptionsDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MENU_ITEM,dMenu);
        args.putInt(ARG_MENU_INDEX, menu_index);
        args.putSerializable(ARG_MENU_LIST, (Serializable) dMenuList);
        args.putSerializable(ARG_LISTENER, eventListener);
        args.putSerializable(ARG_LISTENER_UPDATE, updateListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_item_edit_options_dialog_list_dialog, container, false);
        dMenu = (DMenu) getArguments().getSerializable(ARG_MENU_ITEM);
        menu_index = getArguments().getInt(ARG_MENU_INDEX);
        dMenuList = (List<DMenu>) getArguments().getSerializable(ARG_MENU_LIST);
        eventListener =(EventListener) getArguments().getSerializable(ARG_LISTENER);
        updateListener = (UpdateListener) getArguments().getSerializable(ARG_LISTENER_UPDATE);
        TextView t_title = view.findViewById(R.id.title);
        LinearLayout l_edit = view.findViewById(R.id.edit);
        LinearLayout l_link = view.findViewById(R.id.link);
        ToggleButton b_available = view.findViewById(R.id.available);
        t_title.setText(dMenu.getItem());
        b_available.setChecked(dMenu.isAvailable());
        l_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(eventListener!=null)
                    eventListener.onEditItemClicked(dMenu);
                dismiss();
            }
        });
        l_link.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(eventListener!=null)
                    eventListener.onLinkItem(dMenu, menu_index, dMenuList);
                dismiss();
            }
        });
        b_available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                new UpdateAvailabilityTask(dMenu,isChecked,menu_index).execute((Void)null);
            }
        });
        return view;
    }
    private class UpdateAvailabilityTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_item = base_url+"update_seller_item_availability.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private DMenu dMenu;
        private int menu_index;
        private String available;
        private int success;
        UpdateAvailabilityTask(DMenu dMenu, boolean checked, int menu_index)
        {
            this.dMenu = dMenu;
            available = checked? "1":"0";
            this.menu_index = menu_index;
            jsonParser = new JSONParser();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email", LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("item_id",String.valueOf(dMenu.getId())));
            info.add(new BasicNameValuePair("available",available));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_item,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {
                if(updateListener!=null)
                    updateListener.onItemAvailailabilityChanged(dMenu.getId(),available.contentEquals("1"));
                dismiss();
                //mValues.get(menu_index).setAvailable(available.contentEquals("1"));
            }
            else
            {
                Log.e("adding item", "error");
            }

        }
    }



}