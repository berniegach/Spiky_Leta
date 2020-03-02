package com.spikingacacia.leta.ui.inventory;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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
import com.spikingacacia.leta.ui.database.SCategories;
import com.spikingacacia.leta.ui.inventory.SICategoryF.OnListFragmentInteractionListener;
import com.spikingacacia.leta.ui.inventory.SICategoryC.CategoryItem;

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
 * {@link RecyclerView.Adapter} that can display a {@link CategoryItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SICategoryRecyclerViewAdapter extends RecyclerView.Adapter<SICategoryRecyclerViewAdapter.ViewHolder>
{
    private static final int PERMISSION_REQUEST_INTERNET=2;
    private String url_delete_category= base_url+"delete_seller_category.php";
    private String url_update_category= base_url+"update_seller_category.php";
    private static String url_upload_category_pic= LoginA.base_url+"upload_category_pic.php";
    private final List<CategoryItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private JSONParser jsonParser;
    private List<CategoryItem>itemsCopy;
    private final Context mContext;
    private static Context mContextStatic;
    private static ImageView imageView;
    private static int mCategoryIdForPhoto;
    Preferences preferences;

    public SICategoryRecyclerViewAdapter(List<CategoryItem> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        itemsCopy=new ArrayList<>();
        itemsCopy.addAll(items);
        mListener = listener;
        mContext=context;
        mContextStatic=context;
        jsonParser=new JSONParser();
        //preference
        preferences=new Preferences(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.f_sicategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position)
    {
        String cat=mValues.get(position).category;
        String des=mValues.get(position).description;
        cat=cat.replace("_"," ");
        des=des.replace("_"," ");
        holder.mItem = mValues.get(position);
        holder.mPositionView.setText(mValues.get(position).position);
        holder.mCategoryView.setText(cat);
        holder.mDescriptionView.setText(des);
        if(!preferences.isDark_theme_enabled())
        {
            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.secondary_background_light));
        }

        //get the category photo
        String url= LoginA.base_url+"src/sellers/"+String.format("%s/pics/c_%d", CommonHelper.makeName(LoginA.sellerAccount.getId()), mValues.get(position).id)+".jpg";
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
                                            .setMessage("Are you sure you want to edit this Category?")
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
                                                    String category=mValues.get(position).category;
                                                    category=category.replace("_"," ");
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
                                                    editText.setText(category);
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
                                                                        new RenameCategoryTask(position,mValues.get(position).id,name,description).execute((Void)null);
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
                                    imageView=holder.mImageView;
                                    mCategoryIdForPhoto=mValues.get(position).id;
                                    mListener.onCategoryPhotoEdit(mValues.get(position).id);
                                }
                                else
                                {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage("Are you sure you want to delete this category?\nAll the groups and items in this category will also be deleted.")
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
                                                    new DeleteCategoryTask(position, mValues.get(position).id).execute((Void) null);
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
            for(CategoryItem item:itemsCopy)
            {
                if(item.category.toLowerCase().contains(text))
                    mValues.add(item);
            }
        }
        notifyDataSetChanged();
    }
    public void notifyChange(int position,int id, String category, String description, String dateadded, String datechanged)
    {
        SICategoryC content=new SICategoryC();
        mValues.add(content.createItem(position, id,category, description, dateadded,datechanged));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPositionView;
        public final ImageView mImageView;
        public final TextView mCategoryView;
        public final TextView mDescriptionView;
        public CategoryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPositionView = (TextView) view.findViewById(R.id.position);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mCategoryView = (TextView) view.findViewById(R.id.category);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCategoryView.getText() + "'";
        }
    }
    public class RenameCategoryTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        final private int mId;
        private String mName;
        private String mDescription;
        final private int mPosition;
        private String dateChanged;

        RenameCategoryTask(final int position, final int id,String name,final String description)
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
            Log.d("EDITING CATEGORY: ","editing....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.sellerAccount.getId())));
            info.add(new BasicNameValuePair("category_id", Integer.toString(mId)));
            info.add(new BasicNameValuePair("name", mName));
            info.add(new BasicNameValuePair("description", mDescription));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_category,"POST",info);
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
            Log.d("EDIT CATEGORY: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Successful",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginA.sCategoriesList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SCategories> set = (LinkedHashMap.Entry<Integer, SCategories>) iterator.next();
                    int id = set.getKey();
                    SCategories sCategories = set.getValue();

                    if(id==mId)
                    {
                        sCategories.setDatechanged(dateChanged);
                        if (!sCategories.getCategory().contentEquals(mName))
                            sCategories.setCategory(mName);
                        if (!sCategories.getDescription().contentEquals(mDescription))
                            sCategories.setDescription(mDescription);
                        SICategoryC content = new SICategoryC();
                        mValues.set(mPosition, content.createItem(mPosition +1, id, mName, mDescription, sCategories.getDateadded(), dateChanged));
                        notifyDataSetChanged();
                        //iterator.remove();
                        LoginA.sCategoriesList.put(id, sCategories);
                        break;
                    }


                }
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
    public class DeleteCategoryTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        final private int mId;
        final private int mPosition;

        DeleteCategoryTask(final int position, final int id)
        {
            mPosition=position;
            mId=id;
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING CATEGORY: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.sellerAccount.getId())));
            info.add(new BasicNameValuePair("category_id",Integer.toString(mId)));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_category,"POST",info);
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
            Log.d("DELETING CATEGORY: ","finished....");
            if (successful)
            {
                Toast.makeText(mContext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                Iterator iterator= LoginA.sCategoriesList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SCategories>set=(LinkedHashMap.Entry<Integer, SCategories>) iterator.next();
                    int id=set.getKey();
                    if(id==mId )
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
}
