package com.spikingacacia.leta.ui.waiters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.spikingacacia.leta.ui.database.WaitersD;
import com.spikingacacia.leta.ui.waiters.WaiterC.WaiterItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.waitersList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WaiterF extends Fragment
{

    private String url_add_waiter= base_url+"add_waiter.php";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private  RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WaiterF()
    {
    }

    @SuppressWarnings("unused")
    public static WaiterF newInstance(int columnCount)
    {
        WaiterF fragment = new WaiterF();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.f_waiter_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1)
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else
            {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL));
            WaiterC content=new WaiterC();
            recyclerView.setAdapter(new WaiterRVA(content.ITEMS, mListener, context));
        }
        return view;
    }


   /* @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }*/

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
    public interface OnListFragmentInteractionListener
    {
        void onListFragmentInteraction(WaiterItem item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.waiters_menu, menu);
        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                final android.app.AlertDialog dialog;
                android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
                builderPass.setTitle("Leta email?");
                TextInputLayout textInputLayout=new TextInputLayout(getContext());
                textInputLayout.setPadding(10,0,10,1);
                textInputLayout.setGravity(Gravity.CENTER);
                final EditText editText=new EditText(getContext());
                editText.setPadding(20,10,20,10);
                editText.setTextSize(14);
                textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                editText.setHint("Waiters email used in leta");
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
                                    new CreateWaiterTask(name).execute((Void)null);
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
                WaiterRVA adapter=(WaiterRVA) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                WaiterRVA adapter=(WaiterRVA) recyclerView.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });
    }
    public class CreateWaiterTask extends AsyncTask<Void, Void, Boolean>
    {
        private String email;
        private int success;
        private int id=-1;
        private String waiter_name="";
        CreateWaiterTask(final String name)
        {
            Toast.makeText(getContext(),"Adding the waiter started. Please wait...",Toast.LENGTH_SHORT).show();
            this.email =name;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_id",Integer.toString(LoginA.sellerAccount.getId())));
            info.add(new BasicNameValuePair("waiter_username", email));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_waiter,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    id=jsonObject.getInt("id");
                    waiter_name=jsonObject.getString("waiter_name");
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

                Log.d("adding new waiter", "done...");
                Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                WaiterRVA  adapter=(WaiterRVA) recyclerView.getAdapter();
                WaitersD waiter=new WaitersD(id, email,waiter_name,0);
                waitersList.put(id,waiter);
                adapter.notifyChange(waitersList.size(),id, email,waiter_name,0);

            }
            else
            {
                Log.e("adding waiter", "error");
                Toast.makeText(getContext(),"Error adding the waiter",Toast.LENGTH_SHORT).show();
            }

        }
    }


}
