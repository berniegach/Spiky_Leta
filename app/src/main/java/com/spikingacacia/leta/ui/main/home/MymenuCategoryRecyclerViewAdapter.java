package com.spikingacacia.leta.ui.main.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.main.home.menuFragment.OnListFragmentInteractionListener;

import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class MymenuCategoryRecyclerViewAdapter extends RecyclerView.Adapter<MymenuCategoryRecyclerViewAdapter.ViewHolder>
{
    private String image_url= base_url+"src/categories_pics/";
    private List<Categories> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private Chip t_last_checked = null;


    public MymenuCategoryRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context)
    {
        mListener = listener;
        mValues = new LinkedList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_menu_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mTitleView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(mListener!=null)
                    mListener.onEditCategory(holder.mItem);
                return false;
            }
        });
        holder.mTitleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    if(t_last_checked != null)
                        t_last_checked.setChecked(false);
                    menuFragment.mymenuRecyclerViewAdapter.filterCategory(mValues.get(position).getId());
                    t_last_checked = holder.mTitleView;
                }
                else
                {

                }

            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(mListener!=null)
                    mListener.onEditCategory(holder.mItem);
                return false;
            }
        });
       /* holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mTitleView.getCurrentTextColor() == context.getResources().getColor(R.color.colorAccent))
                {
                    menuFragment.mymenuRecyclerViewAdapter.filterCategory(0);
                    holder.mTitleView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
                else
                {
                    //if we had clicked on any category before remove the color
                    if(t_last_checked!=null)
                        t_last_checked.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                    menuFragment.mymenuRecyclerViewAdapter.filterCategory(mValues.get(position).getId());
                    holder.mTitleView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    t_last_checked = holder.mTitleView;
                }
            }
        });
        // thumbnail image
        String url=image_url+String.valueOf(mValues.get(position).getId())+'_'+String.valueOf(mValues.get(position).getImageType());
        Glide.with(context).load(url).into(holder.thumbNail);*/
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        //public final ImageView thumbNail;
        public final Chip mTitleView;
        public Categories mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            //thumbNail =  view.findViewById(R.id.image);
            mTitleView = view.findViewById(R.id.title);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
    public void listUpdated(List<Categories> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        notifyDataSetChanged();
    }
}