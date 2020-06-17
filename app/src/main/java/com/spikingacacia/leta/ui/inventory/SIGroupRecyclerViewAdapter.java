package com.spikingacacia.leta.ui.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.CommonHelper;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.SGroups;
import com.spikingacacia.leta.ui.inventory.SIGroupC.GroupItem;
import com.spikingacacia.leta.ui.inventory.SIGroupF.OnListFragmentInteractionListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GroupItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SIGroupRecyclerViewAdapter extends RecyclerView.Adapter<SIGroupRecyclerViewAdapter.ViewHolder>
{
    private String url_delete_group = base_url+"delete_seller_group.php";
    private String url_update_group = base_url+"update_seller_group.php";
    private final List<GroupItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private List<GroupItem>itemsCopy;
    private final Context mContext;
    private final int mCategoryId;
    Preferences preferences;

    public SIGroupRecyclerViewAdapter(List<GroupItem> items, OnListFragmentInteractionListener listener, Context context,int categoryId) {
        mValues = items;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mListener = listener;
        mContext=context;
        mCategoryId=categoryId;
        jsonParser=new JSONParser();
        //preference
        preferences=new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_sigroup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position)
    {
        String group=mValues.get(position).group;
        String des=mValues.get(position).description;
        group=group.replace("_"," ");
        des=des.replace("_"," ");
        holder.mItem = mValues.get(position);
        holder.mPositionView.setText(mValues.get(position).position);
        holder.mGroupView.setText(group);
        holder.mDescriptionView.setText(des);
        //get the category photo
        String url= LoginA.base_url+"src/sellers/"+String.format("%s/pics/g_%d", CommonHelper.makeName(LoginA.serverAccount.getId()), mValues.get(position).id)+".jpg";
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClicked(holder.mItem);
                }
            }
        });
        if(serverAccount.getPersona()==0)
            holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                new AlertDialog.Builder(mContext)
                        .setItems(new String[]{"Edit","image","Delete"}, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i==0)
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to edit this Group?")
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
                                                    String group=mValues.get(position).group;
                                                    group=group.replace("_"," ");
                                                    String description=mValues.get(position).description;
                                                    description=description.replace("_"," ");
                                                    final android.app.AlertDialog dialog;
                                                    android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(mContext);
                                                    builderPass.setTitle("Edit");
                                                    //name container
                                                    TextInputLayout textInputLayout=new TextInputLayout(mContext);
                                                    textInputLayout.setPadding(10,0,10,0);
                                                    textInputLayout.setGravity(Gravity.CENTER);
                                                    //name edittext
                                                    final EditText editText=new EditText(mContext);
                                                    editText.setPadding(20,10,20,10);
                                                    editText.setTextSize(14);
                                                    textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    editText.setHint("Name");
                                                    editText.setError(null);
                                                    editText.setText(group);
                                                    editText.setSingleLine(true);
                                                    //description container
                                                    TextInputLayout textInputLayout_d=new TextInputLayout(mContext);
                                                    textInputLayout_d.setPadding(10,0,10,0);
                                                    textInputLayout_d.setGravity(Gravity.CENTER);
                                                    //description edittext
                                                    final EditText editText_d=new EditText(mContext);
                                                    editText_d.setPadding(20,10,20,10);
                                                    editText_d.setTextSize(14);
                                                    textInputLayout_d.addView(editText_d,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    editText_d.setHint("Description");
                                                    editText_d.setError(null);
                                                    editText_d.setText(description);
                                                    //parent layout
                                                    LinearLayout layout=new LinearLayout(mContext);
                                                    layout.setOrientation(LinearLayout.VERTICAL);
                                                    layout.addView(textInputLayout);
                                                    layout.addView(textInputLayout_d);
                                                    builderPass.setView(layout);
                                                    builderPass.setPositiveButton("Edit", null);
                                                    builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i)
                                                        {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    dialog=builderPass.create();
                                                    dialog.setOnShowListener(new DialogInterface.OnShowListener()
                                                    {
                                                        @Override
                                                        public void onShow(DialogInterface dialogInterface)
                                                        {
                                                            Button button=((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                                                            button.setOnClickListener(new View.OnClickListener()
                                                            {
                                                                @Override
                                                                public void onClick(View view)
                                                                {
                                                                    String name=editText.getText().toString();
                                                                    String description=editText_d.getText().toString();
                                                                    if(name.length()<3)
                                                                    {
                                                                        editText.setError("Name too short");
                                                                    }
                                                                    else if(description.length()<3)
                                                                    {
                                                                        editText_d.setError("Description too short");
                                                                    }

                                                                    else
                                                                    {
                                                                        new RenameGroupTask(position,mValues.get(position).id,name,description).execute((Void)null);
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                    dialog.show();
                                                }
                                            }).create().show();
                                }
                                else if(i==1)
                                {
                                    mListener.onGroupPhotoEdit(mValues.get(position).id);
                                }
                                else
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to delete this group?\nAll the items in this group will also be deleted.")
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
                                                    new DeleteGroupTask(position, mValues.get(position).id).execute((Void) null);
                                                }
                                            }).create().show();
                                }
                            }
                        }).create().show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
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
            for(GroupItem item:itemsCopy)
            {
                if(item.group.toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void notifyChange(int position,int id, int category, String group, String description, String dateadded, String datechanged)
    {
        SIGroupC content=new SIGroupC(category);
        mValues.add(content.createItem(position, id,category, group, description, dateadded,datechanged));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPositionView;
        public final ImageView mImageView;
        public final TextView mGroupView;
        public final TextView mDescriptionView;
        public GroupItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPositionView = (TextView) view.findViewById(R.id.position);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mGroupView = (TextView) view.findViewById(R.id.group);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mGroupView.getText() + "'";
        }
    }
    public class RenameGroupTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        final private int mId;
        private String mName;
        private String mDescription;
        final private int mPosition;
        private String dateChanged;

        RenameGroupTask(final int position, final int id, String name, final String description)
        {
            mId=id;
            mPosition=position;
            mName =name;
            //replace the position name spaces with _
            mName = mName.replace(" ","_");
            mDescription =description;
            mDescription = mDescription.toLowerCase().replace(" ","_");
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("EDITING GROUP: ","editing....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.serverAccount.getId())));
            info.add(new BasicNameValuePair("category_id", Integer.toString(mCategoryId)));
            info.add(new BasicNameValuePair("group_id", Integer.toString(mId)));
            info.add(new BasicNameValuePair("name", mName));
            info.add(new BasicNameValuePair("description", mDescription));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_group,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    dateChanged=jsonObject.getString("date_changed");
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
            Log.d("EDIT GROUP: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Successful",Toast.LENGTH_SHORT).show();
                /*Iterator iterator= LoginA.sGroupsList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SGroups> set = (LinkedHashMap.Entry<Integer, SGroups>) iterator.next();
                    int id = set.getKey();
                    SGroups sGroups=set.getValue();
                    if(id==mId)
                    {
                        sGroups.setDatechanged(dateChanged);
                        if (!sGroups.getGroup().contentEquals(mName))
                            sGroups.setGroup(mName);
                        if (!sGroups.getDescription().contentEquals(mDescription))
                            sGroups.setDescription(mDescription);
                        SIGroupC content=new SIGroupC(mCategoryId);
                        mValues.set(mPosition, content.createItem(mPosition +1, id, mCategoryId,mName, mDescription, sGroups.getDateadded(), dateChanged));
                        notifyDataSetChanged();
                        //iterator.remove();
                        LoginA.sGroupsList.put(id, sGroups);
                        break;
                    }


                }*/
            }
            else
            {
                Toast.makeText(mContext,"Error please try again",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
    public class DeleteGroupTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        final private int mId;
        final private int mPosition;

        DeleteGroupTask(final int position, final int id)
        {
            mPosition=position;
            mId=id;
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING GROUP: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.serverAccount.getId())));
            info.add(new BasicNameValuePair("group_id",Integer.toString(mId)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_group,"POST",info);
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
            Log.d("DELETING GROUP: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                /*Iterator iterator= LoginA.sGroupsList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SGroups>set=(LinkedHashMap.Entry<Integer, SGroups>) iterator.next();
                    int id=set.getKey();
                    if(id==mId )
                    {
                        iterator.remove();
                        mValues.remove(mPosition);
                        notifyDataSetChanged();
                        break;
                    }
                }*/
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
