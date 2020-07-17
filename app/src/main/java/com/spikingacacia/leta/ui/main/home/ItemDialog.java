package com.spikingacacia.leta.ui.main.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


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
    private LinearLayout layoutAddSizes;

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
        final Spinner spinner = view.findViewById(R.id.spinner);
        final EditText editItem = view.findViewById(R.id.item);
        final EditText editDescription = view.findViewById(R.id.description);
        final ImageButton backButton = view.findViewById(R.id.edit_back);
        final ImageButton add_sizes_Button = view.findViewById(R.id.add_sizes);
        layoutAddSizes = view.findViewById(R.id.layout_sizes);


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
                        formSizesPrices();
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
                spinner.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                editItem.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                layoutAddSizes.setVisibility(hasFocus? View.GONE : View.VISIBLE);
                backButton.setVisibility(hasFocus? View.VISIBLE : View.GONE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                spinner.setVisibility(View.VISIBLE);
                editItem.setVisibility(View.VISIBLE);
                layoutAddSizes.setVisibility(View.VISIBLE);
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
    private void formSizesPrices()
    {
        menuFragment.sizes="";
        menuFragment.prices="";
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
                menuFragment.sizes+=":";
                menuFragment.prices+=":";
            }
            menuFragment.sizes+=s_size;
            menuFragment.prices+=s_price;

        }
        addnewItem();
    }



}
