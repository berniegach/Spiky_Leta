package com.spikingacacia.leta.ui.main.home;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.inventory.SIItemRecyclerViewAdapter;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.GetFilePathFromDevice;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.sItemsList;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;


public class ItemDialog extends DialogFragment
{
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onItemAdded();
    }
    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;
    private ImageButton imageButton;
    //variables
    String category_title;
    String item;
    String description;

    public ItemDialog()
    {

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

        final View view = inflater.inflate(R.layout.item_dialog, null);
        imageButton = view.findViewById(R.id.image);
        Spinner spinner = view.findViewById(R.id.spinner);
        final EditText editItem = view.findViewById(R.id.item);
        final EditText editDescription = view.findViewById(R.id.description);

        //categories
        final List<String> categories= getCategories();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),   android.R.layout.simple_spinner_item, categories);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);



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

        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        item = editItem.getText().toString();
                        description = editDescription.getText().toString();
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
                        menuFragment.newItem = item;
                        menuFragment.newDescription = description;
                        menuFragment.newCategoryId = getCategoryId(category_title);
                        addnewItem();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
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
    private void addnewItem()
    {
        Intent intent=new Intent();
        //show only images
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getParentFragment().startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),1);
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



}
