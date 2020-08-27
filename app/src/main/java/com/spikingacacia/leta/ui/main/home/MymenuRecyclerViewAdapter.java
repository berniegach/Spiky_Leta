package com.spikingacacia.leta.ui.main.home;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.http.DelegatingSSLSession;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.DMenu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.main.home.menuFragment.*;


public class MymenuRecyclerViewAdapter extends RecyclerView.Adapter<MymenuRecyclerViewAdapter.ViewHolder>
{
    private List<DMenu> mValues;
    private List<DMenu>itemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private FragmentManager fragmentManager;
    private static int lastImageFaded = -1;
    private String TAG = "my_menu_rva";

    public MymenuRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context, FragmentManager fragmentManager)
    {
        mListener = listener;
        mValues = new LinkedList<>();
        itemsCopy = new LinkedList<>();
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        String image_url= base_url+"src/items_pics/";
        holder.mItem = mValues.get(position);
        holder.mItemView.setText(mValues.get(position).getItem());
        holder.mDescriptionView.setText(mValues.get(position).getDescription());
        String[] sizes = mValues.get(position).getSizes().split(":");
        String[] prices = mValues.get(position).getPrices().split(":");
        String sizePrice="";
        for(int c=0; c<sizes.length; c++)
        {
            String location = LoginActivity.getServerAccount().getLocation();
            String[] location_pieces = location.split(",");
            if(location_pieces.length==4)
                sizePrice+=" "+sizes[c]+" @ "+getCurrencyCode(location_pieces[3])+" "+prices[c];
            else
                sizePrice+=" "+sizes[c]+" @ "+prices[c];
        }
        holder.mPriceView.setText(sizePrice);

        // image
        String url=image_url+String.valueOf(mValues.get(position).getId())+'_'+String.valueOf(mValues.get(position).getImageType());
        Glide.with(context).load(url).into(holder.image);
        if(LoginActivity.getServerAccount().getPersona()==2)
        {
            holder.mEditButton.setVisibility(View.GONE);
            holder.mLinkButton.setVisibility(View.INVISIBLE);
        }
        holder.mEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                menuFragment.editItem(holder.mItem);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holder.image.getImageAlpha()==20)
                {
                    holder.image.setImageAlpha(255);
                    //holder.mItemView.setAlpha((float)0.4);
                    holder.mDescriptionView.setAlpha((float)0.0);
                }
                else
                {
                    holder.image.setImageAlpha(20);
                    //holder.mItemView.setAlpha((float)1.0);
                    holder.mDescriptionView.setAlpha((float)1.0);
                }
            }
        });
        holder.mLinkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateLinkedFood(holder.mItem, position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView image;
        public final TextView mItemView;
        public final TextView mDescriptionView;
        public final TextView mPriceView;
        public final ImageButton mEditButton;
        public final ImageButton mLinkButton;
        public DMenu mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            image = view.findViewById(R.id.image);
            mItemView = (TextView) view.findViewById(R.id.item);
            mDescriptionView = view.findViewById(R.id.description);
            mPriceView = (TextView) view.findViewById(R.id.price);
            mEditButton = view.findViewById(R.id.edit);
            mLinkButton = view.findViewById(R.id.link);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mItemView.getText() + "'";
        }
    }
    //to retrieve currency code
    private String getCurrencyCode(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
    }

    //to retrieve currency symbol
    private String getCurrencySymbol(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getSymbol();
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(DMenu item:itemsCopy)
            {
                if(item.getItem().toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void filterCategory(int category_id)
    {
        mValues.clear();
        if(category_id == 0)
            mValues.addAll(itemsCopy);
        else
        {
            for(DMenu item:itemsCopy)
            {
                if(item.getCategoryId() == category_id)
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void listUpdated(List<DMenu> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
    private void updateLinkedFood(final DMenu dMenu,final int menu_index)
    {
        String linked_foods = dMenu.getLinkedItems();
        String[] links = linked_foods.split(":");
        String[] items = new String[mValues.size()];
        final String[] ids = new String[mValues.size()];
        final boolean[] items_checked = new boolean[mValues.size()];

        if(links.length==1 && links[0].contentEquals("null"))
            links[0]="-1";
        else if(links.length==1 && links[0].contentEquals(""))
            links[0]="-1";
        for(int c=0; c<items.length; c++)
        {
            boolean item_updated = false;
            items[c] = mValues.get(c).getItem();
            ids[c] = String.valueOf(mValues.get(c).getId());
            //set the linked item to true
            for( int d=0; d<links.length; d++)
            {
                int id = Integer.valueOf(links[d]);
                for(int e=0; e<mValues.size(); e++)
                {
                    if( id==mValues.get(c).getId())
                    {
                        items_checked[c]=true;
                    }
                }
            }
        }
        new AlertDialog.Builder(context)
                .setTitle("Accompaniments")
                .setMultiChoiceItems(items, items_checked, new DialogInterface.OnMultiChoiceClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        items_checked[which] = isChecked;
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String links="";
                        int count = 0;
                        for(int c=0; c<items_checked.length; c++)
                        {
                            if(!items_checked[c])
                                continue;
                            if(count != 0)
                            {
                                links+=":";
                            }
                            links+=ids[c];
                            count+=1;
                        }
                        new UpdateItemTask(dMenu,links, menu_index).execute((Void)null);
                    }
                }).create().show();

    }
    private class UpdateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_item = base_url+"update_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private DMenu dMenu;
        private String linked_items;
        private int menu_index;
        private int success;
        UpdateItemTask(DMenu dMenu, String linked_items, int menu_index)
        {
            Toast.makeText(context,"Please wait...",Toast.LENGTH_SHORT).show();
            this.dMenu = dMenu;
            this.linked_items = linked_items;
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
                Toast.makeText(context,"Successfully updated",Toast.LENGTH_SHORT).show();
                mValues.get(menu_index).setLinkedItems(linked_items);
                //listener.onItemUpdated();

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
                Toast.makeText(context,"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}