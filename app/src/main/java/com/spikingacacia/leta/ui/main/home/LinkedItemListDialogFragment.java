package com.spikingacacia.leta.ui.main.home;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.util.Utils.getCurrencyCode;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LinkedItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class LinkedItemListDialogFragment extends BottomSheetDialogFragment
{

    private static final String ARG_MENU_ITEM = "arg1";
    private static final String ARG_MENU_INDEX = "arg2";
    private static final String ARG_MENU_LIST = "arg3";
    private static final String ARG_LISTENER = "arg4";
    private DMenu dMenu;
    private int menu_index;
    private List<DMenu> dMenuList;
    RecyclerView recyclerView;
    private String[] ids;
    private String[] items;
    private boolean[] items_checked;
    private boolean[] items_checked_free;
    private UpdateListener updateListener;
    public interface UpdateListener extends Serializable
    {
        void onLinkedItemUpdateDone(int menu_id, String linked_items, String linked_items_prices);
    }
    public static LinkedItemListDialogFragment newInstance(DMenu dMenu, int menu_index, List<DMenu> dMenuList, UpdateListener updateListener)
    {
        final LinkedItemListDialogFragment fragment = new LinkedItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MENU_ITEM,dMenu);
        args.putInt(ARG_MENU_INDEX, menu_index);
        args.putSerializable(ARG_MENU_LIST, (Serializable) dMenuList);
        args.putSerializable(ARG_LISTENER,updateListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        dMenu = (DMenu) getArguments().getSerializable(ARG_MENU_ITEM);
        menu_index = getArguments().getInt(ARG_MENU_INDEX);
        dMenuList = (List<DMenu>) getArguments().getSerializable(ARG_MENU_LIST);
        updateListener = (UpdateListener) getArguments().getSerializable(ARG_LISTENER);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LinkedItemAdapter());
        TextView t_title = view.findViewById(R.id.title);
        t_title.setText(dMenu.getItem());
        Button b_update = view.findViewById(R.id.b_update);
        b_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String links="";
                String links_prices = "";
                int count = 0;
                for(int c=0; c<items_checked.length; c++)
                {
                    if(!items_checked[c])
                        continue;
                    if(count != 0)
                    {
                        links+=":";
                        links_prices+=":";
                    }
                    links+=ids[c];
                    links_prices+= items_checked_free[c]?"1":"0";
                    count+=1;
                }
                new UpdateItemTask(dMenu,links, links_prices, menu_index).execute((Void)null);
            }
        });
    }


    private class ViewHolder extends RecyclerView.ViewHolder
    {

        final CheckBox checkBox;
        final TextView price;
        final CheckBox checkBoxFree;

        ViewHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog_item, parent, false));
            checkBox = itemView.findViewById(R.id.checkbox);
            price = itemView.findViewById(R.id.price);
            checkBoxFree = itemView.findViewById(R.id.check_free);
        }
    }

    private class LinkedItemAdapter extends RecyclerView.Adapter<ViewHolder>
    {


        LinkedItemAdapter()
        {
            String linked_foods = dMenu.getLinkedItems();
            String linked_foods_price = dMenu.getLinkedItemsPrice();
            String[] links = linked_foods.split(":");
            String[] links_price = linked_foods_price.split(":");
            items = new String[dMenuList.size()];

            ids = new String[dMenuList.size()];
            items_checked = new boolean[dMenuList.size()];
            items_checked_free = new boolean[dMenuList.size()];

            if(links.length==1 && links[0].contentEquals("null"))
                links[0]="-1";
            else if(links.length==1 && links[0].contentEquals(""))
                links[0]="-1";
            if(links.length != links_price.length)
            {
                links_price = new String[links.length];
                for(int c=0; c<links.length; c++)
                    links_price[c] = "0";
            }
            for(int c=0; c<items.length; c++)
            {
                boolean item_updated = false;
                items[c] = dMenuList.get(c).getItem();
                ids[c] = String.valueOf(dMenuList.get(c).getId());
                //set the linked item to true
                for( int d=0; d<links.length; d++)
                {
                    int id = Integer.valueOf(links[d]);
                    for(int e=0; e<dMenuList.size(); e++)
                    {
                        if( id==dMenuList.get(c).getId())
                        {
                            items_checked[c]=true;
                            items_checked_free[c] = links_price[d].contentEquals("1");
                        }
                    }
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            holder.checkBox.setText(items[position]);
            holder.checkBox.setChecked(items_checked[position]);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    items_checked[position] = isChecked;
                    holder.checkBoxFree.setEnabled(isChecked);
                }
            });
           holder.checkBoxFree.setEnabled(items_checked[position]);
           holder.checkBoxFree.setChecked(items_checked_free[position]);

            holder.checkBoxFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    items_checked_free[position] = isChecked;
                }
            });
            //set prices
            String[] sizes = dMenuList.get(position).getSizes().split(":");
            String[] prices = dMenuList.get(position).getPrices().split(":");
            String sizePrice="";
            String location = LoginActivity.getServerAccount().getLocation();
            String[] location_pieces = location.split(",");
            if(sizes.length == 1)
            {
                if(location_pieces.length==4)
                    sizePrice = getCurrencyCode(location_pieces[3])+" "+prices[0];
                else
                    sizePrice = prices[0];
            }
            else
            {
                for(int c=0; c<sizes.length; c++)
                {
                    if(location_pieces.length==4)
                        sizePrice+=" "+sizes[c]+" @ "+getCurrencyCode(location_pieces[3])+" "+prices[c];
                    else
                        sizePrice+=" "+sizes[c]+" @ "+prices[c];
                }
            }
            holder.price.setText(sizePrice);
            if(Integer.parseInt(ids[position]) == dMenu.getId())
                holder.checkBox.setEnabled(false);
        }

        @Override
        public int getItemCount()
        {
            return items.length;
        }

    }
    private class UpdateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_item = base_url+"update_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private DMenu dMenu;
        private String linked_items;
        private String linked_items_prices;
        private int menu_index;
        private int success;
        UpdateItemTask(DMenu dMenu, String linked_items, String linked_items_prices, int menu_index)
        {
            this.dMenu = dMenu;
            this.linked_items = linked_items;
            this.linked_items_prices = linked_items_prices;
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
            info.add(new BasicNameValuePair("category_id",Integer.toString(dMenu.getCategoryId())));
            info.add(new BasicNameValuePair("group_id",Integer.toString(-1)));
            info.add(new BasicNameValuePair("linked_items",linked_items));
            info.add(new BasicNameValuePair("linked_items_price",linked_items_prices));
            info.add(new BasicNameValuePair("item",dMenu.getItem()));
            info.add(new BasicNameValuePair("description",dMenu.getDescription()));
            info.add(new BasicNameValuePair("sizes",dMenu.getSizes()));
            info.add(new BasicNameValuePair("selling_price",dMenu.getPrices()));
            info.add(new BasicNameValuePair("image_type",dMenu.getImageType()));
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

                Log.d("adding new item", "done...");
                if(updateListener!=null)
                {
                    updateListener.onLinkedItemUpdateDone(dMenu.getId(),linked_items, linked_items_prices);
                    dismiss();
                }
                //listener.onItemUpdated();

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
            }

        }
    }

}