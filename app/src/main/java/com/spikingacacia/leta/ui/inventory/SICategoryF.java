package com.spikingacacia.leta.ui.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.SCategories;
import com.spikingacacia.leta.ui.inventory.SICategoryC.CategoryItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SICategoryF extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String url_add_category= base_url+"create_seller_category.php";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private  RecyclerView recyclerView;

    public SICategoryF() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SICategoryF newInstance(int columnCount) {
        SICategoryF fragment = new SICategoryF();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeLight);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        //View view= localInflater.inflate(R.layout.f_sicategory_list, container, false);
        View view = inflater.inflate(R.layout.f_sicategory_list, container, false);
        //getContext().getTheme().applyStyle(R.style.AppThemeLight, true); //blue ripple color



        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL));
            SICategoryC content=new SICategoryC();
            recyclerView.setAdapter(new SICategoryRecyclerViewAdapter(content.ITEMS, mListener,getContext()));
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.sicategory_menu, menu);
        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                final android.app.AlertDialog dialog;
                android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
                builderPass.setTitle("Name?");
                TextInputLayout textInputLayout=new TextInputLayout(getContext());
                textInputLayout.setPadding(10,0,10,0);
                textInputLayout.setGravity(Gravity.CENTER);
                final EditText editText=new EditText(getContext());
                editText.setPadding(20,10,20,10);
                editText.setTextSize(14);
                textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                editText.setHint("New Category");
                editText.setError(null);
                LinearLayout layout=new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(textInputLayout);
                builderPass.setView(layout);
                builderPass.setPositiveButton("Add", null);
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
                                if(name.length()<3)
                                {
                                    editText.setError("Name too short");
                                }

                                else
                                {
                                    new CreateCategoryTask(name).execute((Void)null);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
                return true;
            }
        });

        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                SICategoryRecyclerViewAdapter adapter=(SICategoryRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                SICategoryRecyclerViewAdapter adapter=(SICategoryRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemClicked(CategoryItem item);
        void onCategoryPhotoEdit(int id);
    }
    public class CreateCategoryTask extends AsyncTask<Void, Void, Boolean>
    {
        private String name;
        private int success;
        private int id=-1;
        private String dateAdded="null";
        CreateCategoryTask(final String name)
        {
            Toast.makeText(getContext(),"Adding category started. Please wait...",Toast.LENGTH_SHORT).show();
            String name_temp=name.toLowerCase();
            name_temp=name_temp.replace(" ","_");
            this.name=name_temp;
            Log.d("CRATECATEGORY"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.sellerAccount.getId())));
            info.add(new BasicNameValuePair("name",name));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_category,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    id=jsonObject.getInt("id");
                    dateAdded=jsonObject.getString("dateadded");
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

                Log.d("adding new category", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                SICategoryRecyclerViewAdapter adapter=(SICategoryRecyclerViewAdapter) recyclerView.getAdapter();
                SCategories sCategories=new SCategories(id,name,"null", dateAdded,"null");
                LoginA.sCategoriesList.put(id,sCategories);
                adapter.notifyChange(LoginA.sCategoriesList.size(),id,name,"null",dateAdded,"null");

            }
            else if(success==-2)
            {
                Log.e("adding category", "error");
                Toast.makeText(getContext(),"Category already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
