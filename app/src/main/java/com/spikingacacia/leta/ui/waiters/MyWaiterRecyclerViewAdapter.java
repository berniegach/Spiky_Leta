package com.spikingacacia.leta.ui.waiters;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.spikingacacia.leta.ui.database.Waiters;
import com.spikingacacia.leta.ui.waiters.WaiterFragment.OnListFragmentInteractionListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;


public class MyWaiterRecyclerViewAdapter extends RecyclerView.Adapter<MyWaiterRecyclerViewAdapter.ViewHolder>
{
    private final List<Waiters> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private List<Waiters>itemsCopy;

    public MyWaiterRecyclerViewAdapter(OnListFragmentInteractionListener listener, Context context)
    {
        mValues = new ArrayList<>();
        itemsCopy=new ArrayList<>();
        mListener = listener;
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_waiter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        String image_url= LoginActivity.base_url+"src/buyers_pics/";
        holder.mItem = mValues.get(position);

        holder.mNamesView.setText(mValues.get(position).getNames());
        holder.mEmailView.setText(mValues.get(position).getEmail());

        // thumbnail image
        String url=image_url+String.valueOf(mValues.get(position).getId())+'_'+String.valueOf(mValues.get(position).getImageType());
        Glide.with(mContext).load(url).into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mListener)
                {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                new AlertDialog.Builder(mContext)
                        .setItems(new String[]{"Delete"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new AlertDialog.Builder(mContext)
                                        .setMessage("Are you sure you want to remove the waiter?")
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {
                                                new DeleteWaiterTask(position, mValues.get(position).getId()).execute((Void) null);
                                            }
                                        }).create().show();
                            }
                        }).create().show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }
    public void filter(String text)
    {
        mValues.clear();
        if(text.isEmpty())
            mValues.addAll(itemsCopy);
        else
        {
            text=text.toLowerCase();
            for(Waiters item:itemsCopy)
            {
                if(item.getNames().toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNamesView;
        public final TextView mEmailView;
        public Waiters mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mImageView= view.findViewById(R.id.image);
            mNamesView = (TextView) view.findViewById(R.id.names);
            mEmailView = (TextView) view.findViewById(R.id.email);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNamesView.getText() + "'";
        }
    }
    public void listUpdated(List<Waiters> newitems)
    {
        mValues.clear();
        mValues.addAll(newitems);
        itemsCopy.addAll(newitems);
        notifyDataSetChanged();
    }
    public class DeleteWaiterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_delete_waiter = base_url + "delete_waiter.php";
        private int success=0;
        final private int waiter_id;
        final private int mPosition;
        private JSONParser jsonParser;


        DeleteWaiterTask(final int position, final int id)
        {
            mPosition=position;
            waiter_id =id;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING WAITER: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_id",Integer.toString(LoginActivity.serverAccount.getId())));
            info.add(new BasicNameValuePair("waiter_id",Integer.toString(waiter_id)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_waiter,"POST",info);
            try
            {
                String TAG_SUCCESS = "success";
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String TAG_MESSAGE = "message";
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("DELETING WAITER: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                mValues.remove(mPosition);
                notifyDataSetChanged();
            }
            else
            {

            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
}
