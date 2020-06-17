package com.spikingacacia.leta.ui.waiters;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.WaitersD;
import com.spikingacacia.leta.ui.waiters.WaiterF.OnListFragmentInteractionListener;
import com.spikingacacia.leta.ui.waiters.WaiterC.WaiterItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WaiterItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class WaiterRVA extends RecyclerView.Adapter<WaiterRVA.ViewHolder>
{
    private String url_delete_waiter= base_url+"delete_waiter.php";
    private final List<WaiterItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private List<WaiterItem>itemsCopy;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    Preferences preferences;

    public WaiterRVA(List<WaiterItem> items, OnListFragmentInteractionListener listener, Context context)
    {
        mValues = items;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mListener = listener;
        mContext=context;
        //preference
        preferences=new Preferences(context);
        jsonParser=new JSONParser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_waiter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);

        holder.mPosView.setText(mValues.get(position).pos);
        holder.mNamesView.setText(mValues.get(position).names);
        holder.mEmailView.setText(mValues.get(position).email);
        String url= LoginA.base_url+"src/buyers/"+String.format("%s/pics/prof_pic",makeName(mValues.get(position).id))+".jpg";
        //get the waiter photo
        ImageRequest request=new ImageRequest(
                url,
                new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        holder.mImageView.setImageBitmap(response);
                        Log.d("volley","succesful");
                    }
                }, 0, 0, null,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("voley",""+e.getMessage()+e.toString());
                    }
                });
        RequestQueue request2 = Volley.newRequestQueue(mContext);
        request2.add(request);

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
                                                new DeleteWaiterTask(position, mValues.get(position).id).execute((Void) null);
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
            for(WaiterItem item:itemsCopy)
            {
                if(item.names.toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void notifyChange(int position,int id, String email, String names, double ratings)
    {
        WaiterC  content=new WaiterC();
        mValues.add(content.createDummyItem(position,id,email,names,ratings));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView mPosView;
        public final ImageView mImageView;
        public final TextView mNamesView;
        public final TextView mEmailView;
        public WaiterItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mPosView = (TextView) view.findViewById(R.id.item_number);
            mImageView=(ImageView) view.findViewById(R.id.image);
            mNamesView = (TextView) view.findViewById(R.id.names);
            mEmailView = (TextView) view.findViewById(R.id.email);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNamesView.getText() + "'";
        }
    }
    public class DeleteWaiterTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        final private int waiter_id;
        final private int mPosition;

        DeleteWaiterTask(final int position, final int id)
        {
            mPosition=position;
            waiter_id =id;
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
            info.add(new BasicNameValuePair("seller_id",Integer.toString(LoginA.serverAccount.getId())));
            info.add(new BasicNameValuePair("waiter_id",Integer.toString(waiter_id)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_waiter,"POST",info);
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
        protected void onPostExecute(final Boolean successful) {
            Log.d("DELETING WAITER: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginA.waitersList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, WaitersD>set=(LinkedHashMap.Entry<Integer, WaitersD>) iterator.next();
                    int id=set.getKey();
                    if(id== waiter_id)
                    {
                        iterator.remove();
                        mValues.remove(mPosition);
                        notifyDataSetChanged();
                        break;
                    }
                }
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
    private String makeName(int id)
    {
        String letters=String.valueOf(id);
        char[] array=letters.toCharArray();
        String name="";
        for(int count=0; count<array.length; count++)
        {
            switch (array[count])
            {
                case '0':
                    name+="zero";
                    break;
                case '1':
                    name+="one";
                    break;
                case '2':
                    name+="two";
                    break;
                case '3':
                    name+="three";
                    break;
                case '4':
                    name+="four";
                    break;
                case '5':
                    name+="five";
                    break;
                case '6':
                    name+="six";
                    break;
                case '7':
                    name+="seven";
                    break;
                case '8':
                    name+="eight";
                    break;
                case '9':
                    name+="nine";
                    break;
                default :
                    name+="NON";
            }
        }
        return name;
    }
}
