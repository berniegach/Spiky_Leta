package com.spikingacacia.leta.ui.main.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.DMenu;

import java.io.Serializable;
import java.util.List;

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
    private DMenu dMenu;
    private int menu_index;
    private List<DMenu> dMenuList;
    EventListener eventListener;
    public interface EventListener extends Serializable
    {
        void onLinkItem(final DMenu dMenu,final int menu_index,  List<DMenu> dMenuList);
        void onEditItemClicked(final DMenu dMenu);
    }
    public static ItemEditOptionsDialogFragment newInstance(DMenu dMenu, int menu_index, EventListener eventListener,  List<DMenu> dMenuList)
    {
        final ItemEditOptionsDialogFragment fragment = new ItemEditOptionsDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MENU_ITEM,dMenu);
        args.putInt(ARG_MENU_INDEX, menu_index);
        args.putSerializable(ARG_MENU_LIST, (Serializable) dMenuList);
        args.putSerializable(ARG_LISTENER, eventListener);
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
        TextView t_title = view.findViewById(R.id.title);
        LinearLayout l_edit = view.findViewById(R.id.edit);
        LinearLayout l_link = view.findViewById(R.id.link);
        t_title.setText(dMenu.getItem());
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
        return view;
    }



}