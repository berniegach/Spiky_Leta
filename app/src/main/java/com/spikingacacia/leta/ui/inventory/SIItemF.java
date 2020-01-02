package com.spikingacacia.leta.ui.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.SItems;
import com.spikingacacia.leta.ui.inventory.SIGroupC.GroupItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.sItemsList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SIItemF extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_GROUP_ID = "group_id";
    // TODO: Customize parameters
    private String url_add_item = base_url+"create_seller_item.php";
    private int mCategoryId;
    private int mGroupId;
    private OnListFragmentInteractionListener mListener;
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private  RecyclerView recyclerView;

    public SIItemF() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SIItemF newInstance(int categoryId, int groupId) {
        SIItemF fragment = new SIItemF();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID,categoryId);
        args.putInt(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCategoryId=getArguments().getInt(ARG_CATEGORY_ID);
            mGroupId = getArguments().getInt(ARG_GROUP_ID);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_siitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL));
            SIItemC content=new SIItemC(mCategoryId,mGroupId);
            recyclerView.setAdapter(new SIItemRecyclerViewAdapter(content.ITEMS, mListener,getContext(), mCategoryId,mGroupId));
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.siitem_menu, menu);
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
                editText.setHint("New Item");
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
                                    new CreateItemTask(name).execute((Void)null);
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
                SIItemRecyclerViewAdapter adapter=(SIItemRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                SIItemRecyclerViewAdapter adapter=(SIItemRecyclerViewAdapter) recyclerView.getAdapter();
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
        //void onItemClicked(GroupItem item);
        void onItemPhotoEdit(int id);
    }
    public class CreateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String name;
        private int success;
        private int id=-1;
        private String dateAdded="null";
        CreateItemTask(final String name)
        {
            Toast.makeText(getContext(),"Adding item started. Please wait...",Toast.LENGTH_SHORT).show();
            String name_temp=name.toLowerCase();
            name_temp=name_temp.replace(" ","_");
            this.name=name_temp;
            Log.d("CRATEITEM"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",Integer.toString(LoginA.sellerAccount.getId())));
            info.add(new BasicNameValuePair("name",name));
            info.add(new BasicNameValuePair("category_id",Integer.toString(mCategoryId)));
            info.add(new BasicNameValuePair("group_id",Integer.toString(mGroupId)));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_item,"POST",info);
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

                Log.d("adding new item", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                SIItemRecyclerViewAdapter adapter=(SIItemRecyclerViewAdapter) recyclerView.getAdapter();
                SItems sItems=new SItems(id, mCategoryId,mGroupId,name,"null",0.0,1,dateAdded,"null");
                sItemsList.put(id,sItems);
                adapter.notifyChange(sItemsList.size(),id, mCategoryId,mGroupId,name,"null",0.0,1,dateAdded,"null");

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
                Toast.makeText(getContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
