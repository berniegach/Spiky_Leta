package com.spikingacacia.leta.ui.board;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.board.AdsC.AdItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginA.base_url;
import static com.spikingacacia.leta.ui.LoginA.serverAccount;

public class advF extends Fragment
{

    private String url_add_advert= base_url+"add_advert.php";
    private String url_get_ads= base_url+"get_ads.php";
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private static int static_last_id=0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public advF()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static advF newInstance(int columnCount)
    {
        advF fragment = new advF();
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
        View view = inflater.inflate(R.layout.f_adv_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
            //linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            final advRVA my_advRVA=new advRVA(AdsC.ITEMS, mListener, getContext());
            my_advRVA.clearData();
            recyclerView.setAdapter(my_advRVA);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
                {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        if(static_last_id==1)
                            return;
                        new AdsTask(static_last_id-1, my_advRVA).execute((Void)null);
                    }
                }
            });
            new AdsTask(0, my_advRVA).execute((Void)null);
        }
        return view;
    }


    @Override
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
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.ads_menu, menu);
        final MenuItem add = menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                show_add_dialog();
                return true;
            }
        });
    }
    private void show_add_dialog()
    {
        final android.app.AlertDialog dialog;
        android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
        builderPass.setTitle("Create Content");
        //views
        final EditText e_title=new EditText(getContext());
        final EditText e_content=new EditText(getContext());
        e_title.setHint("title");
        e_content.setHint("content");
        LinearLayout layout=new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(e_title);
        layout.addView(e_content);
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

                        String title=e_title.getText().toString();
                        String content=e_content.getText().toString();
                        if(title.length()<3)
                        {
                            e_title.setError("title too short");
                        }
                        else if(content.length()<3)
                        {
                            e_content.setError("content too short");
                        }

                        else
                        {
                            if(mListener!=null)
                                mListener.onUploadPhoto(recyclerView,title,content);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onAdClicked(AdItem item);
        void onUploadPhoto(RecyclerView recyclerView, String title, String content);
    }
    private class AdsTask extends AsyncTask<Void, Void, Boolean>
    {
        final int last_id;
        final advRVA my_advra;

        @Override
        protected void onPreExecute()
        {
            Log.d("SCATEGORIES: ","starting....");
            super.onPreExecute();
        }
        public AdsTask(int last_id, advRVA advRVA)
        {
            this.last_id=last_id;
            my_advra=advRVA;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_id",Integer.toString(serverAccount.getId())));
            info.add(new BasicNameValuePair("last_id",Integer.toString(last_id)));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_ads,"POST",info);
            // Log.d("",""+jsonObject.toString());
            try
            {
                JSONArray array=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    array=jsonObject.getJSONArray("ads");
                    for(int count=0; count<array.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=array.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        if(count==0)
                            static_last_id=id;
                        int seller_id=jsonObjectNotis.getInt("seller_id");
                        String title=jsonObjectNotis.getString("title");
                        String content=jsonObjectNotis.getString("content");
                        int views=jsonObjectNotis.getInt("ad_views");
                        int likes=jsonObjectNotis.getInt("ad_likes");
                        int comments=jsonObjectNotis.getInt("ad_comments");
                        String date=jsonObjectNotis.getString("date_added");

                        find_image(id, title, content, views, likes, comments, date);

                    }
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

            if (successful)
            {

            }
            else
            {

            }
        }
        private void find_image(final int id, final String title, final String content, final int views, final int likes, final int comments, final String date)
        {
            String url= LoginA.base_url+"src/ads/"+String.format("%d",id)+".jpg";
            ImageRequest request=new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap response)
                        {

                            my_advra.add_ads(id,title,response,content,views,likes,comments,date);
                            //profilePic=response;
                            //imageView.setImageBitmap(response);
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
            RequestQueue request2 = Volley.newRequestQueue(getContext());
            request2.add(request);
        }
    }

}
