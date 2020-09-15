package com.spikingacacia.leta.ui.main.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.Groups;
import com.spikingacacia.leta.ui.main.home.menuFragment.OnListFragmentInteractionListener;

import java.util.LinkedList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class MymenuGroupRecyclerViewAdapter extends RecyclerView.Adapter<MymenuGroupRecyclerViewAdapter.ViewHolder>
{
    private String image_url= base_url+"src/groups_pics/";
    private List<Groups> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private TextView t_last_checked = null;


    public MymenuGroupRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context)
    {
        mListener = listener;
        mValues = new LinkedList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_menu_groups, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if(mListener!=null)
                    mListener.onEditGroup(holder.mItem);
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
        public final TextView mTitleView;
        public Groups mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            //thumbNail =  view.findViewById(R.id.image);
            mTitleView = (TextView) view.findViewById(R.id.title);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
    public void listUpdated(List<Groups> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        notifyDataSetChanged();
    }
}