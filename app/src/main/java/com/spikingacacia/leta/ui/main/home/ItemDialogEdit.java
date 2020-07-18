package com.spikingacacia.leta.ui.main.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.AppController;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.main.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;


public class ItemDialogEdit extends DialogFragment
{
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onItemUpdated();
    }
    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;
    //variables
    private String category_title;
    private DMenu dMenu;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static NetworkImageView imageView;
    private LinearLayout layoutAddSizes;
    private String sizes;
    private String prices;

    public ItemDialogEdit(DMenu dMenu)
    {
       this.dMenu = dMenu;
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //variables

        final View view = inflater.inflate(R.layout.item_dialog_edit, null);

        final ImageButton deleteButton = view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DeleteItemTask(dMenu.getId(), dMenu.getImageType()).execute((Void)null);
                dismiss();
            }
        });
        //image
        String image_url= base_url+"src/items_pics/";
        String url=image_url+String.valueOf(dMenu.getId())+'_'+dMenu.getImageType();
        imageView = view.findViewById(R.id.image);
        imageView.setImageUrl(url, imageLoader);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                menuFragment.itemIdToEdit = dMenu.getId();
                editImage();
            }
        });

        final Spinner spinner = view.findViewById(R.id.spinner);
        final EditText editItem = view.findViewById(R.id.item);
        final EditText editDescription = view.findViewById(R.id.description);
        final EditText editPrice = view.findViewById(R.id.price);
        final ImageButton backButton = view.findViewById(R.id.edit_back);
        final ImageButton add_sizes_Button = view.findViewById(R.id.add_sizes);
        layoutAddSizes = view.findViewById(R.id.layout_sizes);
        //item
        editItem.setText(dMenu.getItem());
        //description
        editDescription.setText(dMenu.getDescription());
        //sizes and prices
        addNewSizeLayoutAndFillData();
        //categories
        final List<String> categories= getCategories();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),   android.R.layout.simple_spinner_item, categories);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(getCategoryIndexInSpinner(dMenu.getCategoryId()));



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                category_title = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        //add sizes
        add_sizes_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewSizeLayout();
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String item = editItem.getText().toString();
                        String description = editDescription.getText().toString();
                        if(TextUtils.isEmpty(item))
                        {
                            editItem.setError("Item name empty");
                            return;
                        }
                        if(TextUtils.isEmpty(description))
                        {
                            editDescription.setError("Description empty");
                            return;
                        }
                        formSizesPrices(item, description);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        editDescription.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                imageView.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                spinner.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                editItem.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                editPrice.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                backButton.setVisibility(hasFocus? View.VISIBLE : View.GONE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                imageView.setVisibility( View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                editItem.setVisibility(View.VISIBLE);
                editPrice.setVisibility(View.VISIBLE);
                backButton.setVisibility( View.GONE);
                editDescription.clearFocus();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this.getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }
    private List<String> getCategories()
    {
        List<String> list = new ArrayList<>();
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            list.add(categories.getTitle());
        }
        return  list;
    }
    private void editImage()
    {
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getParentFragment().startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),2);
    }
    private int getCategoryId(String item)
    {
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            String title = categories.getTitle();
            if(item.contentEquals(title))
                return id;
        }
        return -1;
    }
    private int getCategoryIndexInSpinner(int category_id)
    {
        int index = 0;
        Iterator iterator = MainActivity.categoriesLinkedHashMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, Categories>set=(LinkedHashMap.Entry<Integer, Categories>) iterator.next();
            int id=set.getKey();
            Categories categories =set.getValue();
            int cat_id = categories.getId();
            if(cat_id == category_id)
                return index;
            index+=1;
        }
        return -1;
    }
    private void addNewSizeLayoutAndFillData()
    {
        String[] sizes = dMenu.getSizes().split(":");
        String[] prices = dMenu.getPrices().split(":");

        final EditText t_size_first = layoutAddSizes.getChildAt(0).findViewById(R.id.edit_size);
        final EditText t_price_first = layoutAddSizes.getChildAt(0).findViewById(R.id.edit_price);

        for(int c=0; c<sizes.length; c++)
        {
            if( c==0 )
            {
                t_size_first.setText(sizes[0]);
                t_price_first.setText(prices[0]);
            }
            else
            {
                final View view = getLayoutInflater().inflate(R.layout.item_dialog_sizes_prices, null);
                final EditText t_size = view.findViewById(R.id.edit_size);
                final EditText t_price = view.findViewById(R.id.edit_price);
                final ImageButton deleteButton = view.findViewById(R.id.delete);

                t_size.setText(sizes[c]);
                t_price.setText(prices[c]);
                deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        layoutAddSizes.removeView(view);
                    }
                });
                layoutAddSizes.addView(view);
            }

        }


    }
    private void addNewSizeLayout()
    {
        final View view = getLayoutInflater().inflate(R.layout.item_dialog_sizes_prices, null);
        final ImageButton deleteButton = view.findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                layoutAddSizes.removeView(view);
            }
        });
        layoutAddSizes.addView(view);
    }
    private void formSizesPrices(String item, String description)
    {
        sizes="";
        prices="";
        int count = layoutAddSizes.getChildCount();
        for(int c = 0; c<count; c++)
        {
            View view = layoutAddSizes.getChildAt(c);
            TextView t_size = view.findViewById(R.id.edit_size);
            TextView t_price = view.findViewById(R.id.edit_price);
            String s_size = t_size.getText().toString();
            String s_price = t_price.getText().toString();
            if(TextUtils.isEmpty(s_size))
            {
                t_size.setError("No size");
                return;
            }
            if(TextUtils.isEmpty(s_price))
            {
                t_price.setError("No price");
                return;
            }
            if(c != 0)
            {
                sizes+=":";
                prices+=":";
            }
            sizes+=s_size;
            prices+=s_price;

        }
        new UpdateItemTask(dMenu.getId(),item,description,getCategoryId(category_title), dMenu.getLinkedItems(),".jpg").execute((Void)null);
    }

    private class UpdateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update_item = base_url+"update_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int item_id;
        private String item;
        private String description;
        private Integer category_id;
        private String linked_items;
        //private String selling_price;
        private String image_type;
        private int success;
        UpdateItemTask(int item_id, final String item, final String description, final Integer category_id, String linked_items, String image_type)
        {
            Toast.makeText(getContext(),"Please wait...",Toast.LENGTH_SHORT).show();
            this.item_id = item_id;
            this.item = item;
            this.description = description;
            this.category_id = category_id;
            this.linked_items = linked_items;
            //this.selling_price = selling_price;
            this.image_type = image_type;
            jsonParser = new JSONParser();
            Log.d("CRATEITEM"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            info.add(new BasicNameValuePair("item_id",String.valueOf(item_id)));
            info.add(new BasicNameValuePair("category_id",Integer.toString(category_id)));
            info.add(new BasicNameValuePair("group_id",Integer.toString(-1)));
            info.add(new BasicNameValuePair("linked_items",linked_items));
            info.add(new BasicNameValuePair("item",item));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("sizes",sizes));
            info.add(new BasicNameValuePair("selling_price",prices));
            info.add(new BasicNameValuePair("image_type",image_type));
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
                //Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
               //listener.onItemUpdated();

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
                Toast.makeText(getContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class DeleteItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_delete_item = base_url+"delete_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private int success=0;
        final private int mId;
        private String image_type;

        DeleteItemTask(final int id, String image_type)
        {
            mId=id;
            this.image_type = image_type;
            jsonParser = new JSONParser();
        }
        @Override
        protected void onPreExecute()
        {
            Log.d("DELETING ITEM: ","deleting....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //logIn=handler.LogInStaff(mEmail,mPassword);
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("item_id",Integer.toString(mId)));
            info.add(new BasicNameValuePair("image_type",image_type));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_delete_item,"POST",info);
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
            Log.d("DELETING ITEM: ","finished....");
            if (successful)
            {
                Toast.makeText(getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
               listener.onItemUpdated();
            }
            else
            {

            }
        }

    }



}
