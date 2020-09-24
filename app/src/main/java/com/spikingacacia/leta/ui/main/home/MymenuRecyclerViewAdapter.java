package com.spikingacacia.leta.ui.main.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.spikingacacia.leta.R;
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

    OptionsListener optionsListener;


    public interface OptionsListener
    {
        void onOptionsMenuSelected(final DMenu dMenu, int menu_position, List<DMenu> dMenuList);
    }


    public MymenuRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context, FragmentManager fragmentManager, OptionsListener optionsListener)
    {
        mListener = listener;
        mValues = new LinkedList<>();
        itemsCopy = new LinkedList<>();
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.optionsListener = optionsListener;
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
        String[] sizePrice;
        String location = LoginActivity.getServerAccount().getLocation();
        String[] location_pieces = location.split(",");
        if(sizes.length == 1)
        {
            if(location_pieces.length==4)
                sizePrice = new String[]{getCurrencyCode(location_pieces[3])+" "+prices[0]};
            else
                sizePrice = new String[]{prices[0]};
        }
        else
        {
            sizePrice = new String[sizes.length];
            for(int c=0; c<sizes.length; c++)
            {

                if(location_pieces.length==4)
                    sizePrice[c] = sizes[c]+" @ "+getCurrencyCode(location_pieces[3])+" "+prices[c];
                else
                    sizePrice[c] = sizes[c]+" @ "+prices[c];
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, sizePrice);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.mPriceView.setAdapter(adapter);
        //holder.mToggleButton.setChecked(mValues.get(position).isAvailable());

        // image
        String url=image_url+String.valueOf(mValues.get(position).getId())+'_'+String.valueOf(mValues.get(position).getImageType());
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                    {
                        //holder.image.getLayoutParams().width = 20;
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                    {
                        return false;
                    }
                })
                .into(holder.image);
        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(optionsListener!=null)
                    optionsListener.onOptionsMenuSelected(holder.mItem, position, mValues);
                return false;
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
        public final Spinner mPriceView;
        public DMenu mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            image = view.findViewById(R.id.image);
            mItemView = (TextView) view.findViewById(R.id.item);
            mDescriptionView = view.findViewById(R.id.description);
            mPriceView =  view.findViewById(R.id.price);
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
    public void filterCategory(List<Integer> category_ids)
    {
        mValues.clear();
        for(int cat_id:category_ids)
        {
            for(DMenu item:itemsCopy)
            {
                if(item.getCategoryId() == cat_id)
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void listUpdated(List<DMenu> newitems)
    {
        mValues.clear();
        itemsCopy.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }


}