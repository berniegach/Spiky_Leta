package com.spikingacacia.leta.ui.main.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.Categories;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.main.MainActivity;
import com.spikingacacia.leta.ui.util.VolleyMultipartRequest;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.LoginActivity.serverAccount;

/**
 * A fragment representing a list of Items.
 */
public class menuFragment extends Fragment
{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewMenu;
    private MymenuRecyclerViewAdapter mymenuRecyclerViewAdapter;
    private MymenuCategoryRecyclerViewAdapter mymenuCategoryRecyclerViewAdapter;
    private OnListFragmentInteractionListener mListener;
    public static int itemIdToEdit;
    public static int newCategoryId;
    public static String newItem;
    public static String newDescription;
    public static String sizes;
    public static String prices;
    private String TAG = "menuF";

    public menuFragment()
    {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static menuFragment newInstance()
    {
        menuFragment fragment = new menuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        RecyclerView recyclerViewCategories = view.findViewById(R.id.list_categories);
        mymenuCategoryRecyclerViewAdapter = new MymenuCategoryRecyclerViewAdapter(mListener);
        recyclerViewCategories.setAdapter(mymenuCategoryRecyclerViewAdapter);

        recyclerViewMenu = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (getHorizontalItemCount()<=1)
        {
            recyclerViewMenu.setLayoutManager(new LinearLayoutManager(context));
        } else
        {
            recyclerViewMenu.setLayoutManager(new GridLayoutManager(context, getHorizontalItemCount()));
        }
        mymenuRecyclerViewAdapter = new MymenuRecyclerViewAdapter(mListener, getContext(), getChildFragmentManager());
        recyclerViewMenu.setAdapter(mymenuRecyclerViewAdapter);
        recyclerViewMenu.addItemDecoration(new SpacesItemDecoration(16));
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
    public void onResume()
    {
        super.onResume();
        new CategoriesTask().execute((Void)null);
        new MenuTask().execute((Void)null);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_fragment_menu, menu);

        final MenuItem add=menu.findItem(R.id.action_add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                onAddNewItem();
                /*if(mListener!=null)
                    mListener.onAddNewItem();*/
                return false;
            }
        });
        if(serverAccount.getPersona() ==2)
            add.setVisible(false);

        // Associate searchable configuration with the SearchView
        SearchView searchView =  (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                MymenuRecyclerViewAdapter adapter=( MymenuRecyclerViewAdapter) recyclerViewMenu.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                MymenuRecyclerViewAdapter adapter=( MymenuRecyclerViewAdapter) recyclerViewMenu.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });

    }


    public interface OnListFragmentInteractionListener
    {
        //void onEditMenu(int which, DMenu dMenu);
        //void onMenuItemInteraction(DMenu item);
        //void onCategoryItemInteraction(Categories item);
    }
    private int getHorizontalItemCount()
    {
        int screenSize = getContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 3;

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 2;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 1;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
            default:
                return 1;
        }
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            int pos = parent.getChildLayoutPosition(view);
            int items = getHorizontalItemCount();

            // Add top margin only for the first item to avoid double space between items
            if (pos < items) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
    public void onAddNewItem()
    {
        DialogFragment dialog = new ItemDialog();
        dialog.show(getChildFragmentManager(), "ItemDialogFragment");
    }
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            final Uri uri = data.getData();
            try
            {

                final String path = getPath(uri);
                Log.d("path",""+path);

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                //upload
                if (path == null)
                {
                    Log.e("upload cert","its null");
                }
                else
                {
                    //Uploading code
                    try
                    {
                        new CreateItemTask(newItem,newDescription,newCategoryId,".jpg", bitmap).execute((Void)null);
                    }
                    catch (Exception e)
                    {
                        Log.e("uploading",""+e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("bitmap", "" + e.getMessage());
            }
        }
        else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            final Uri uri = data.getData();
            try
            {

                final String path = getPath(uri);
                //Log.d("path 2",""+path);

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                if(bitmap == null)
                {
                    Log.e(TAG,"failed to compress image");
                    Toast.makeText(getContext(),"Image upload failed",Toast.LENGTH_SHORT).show();
                    return;
                }
                ItemDialogEdit.imageView.setImageBitmap(bitmap);
                //imageButton.setImageBitmap(bitmap);
                //upload
                if (path == null)
                {
                    Log.e("upload cert","its null");
                }
                else
                {
                    //Uploading code
                    try
                    {
                        uploadBitmap(bitmap, itemIdToEdit);
                    }
                    catch (Exception e)
                    {
                        Log.e("uploading",""+e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("bitmap", "" + e.getMessage());
            }
        }
    }
    private String getPath(Uri uri)
    {
        if(uri==null)
            return null;
        String res=null;

        if (DocumentsContract.isDocumentUri(getContext(), uri))
        {
            //emulator
            String[] path = uri.getPath().split(":");
            res = path[1];
            Log.i("debinf ProdAct", "Real file path on Emulator: "+res);
        }
        else {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }
    private void uploadBitmap(final Bitmap bitmap, final int insert_id)
    {
        String url_upload_profile_pic= LoginActivity.base_url+"upload_inventory_pic.php";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url_upload_profile_pic,
                new Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse response)
                    {
                        //weve uploaded the image therefore its okay to proceed with adding the new item in the server
                        int statusCode = response.statusCode;
                        Toast.makeText(getContext(),"Successful",Toast.LENGTH_SHORT).show();
                        new MenuTask().execute((Void)null);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("png", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String , String >params = new HashMap<>();
                params.put("name", "name"); //Adding text parameter to the request
                params.put("id",String.valueOf(insert_id));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(final Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int quality = 100;
        while(true)
        {
            byteArrayOutputStream.reset();
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream))
            {

                //Log.e(TAG,"bytes length "+byteArrayOutputStream.toByteArray().length);
                if(byteArrayOutputStream.toByteArray().length<=2000000)
                    return byteArrayOutputStream.toByteArray();
            }
            else
                return null;
            quality-=10;
        }

    }
    public class CreateItemTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_add_item = base_url+"create_seller_item.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private String item;
        private String description;
        private Integer category_id;
        private String image_type;
        private Bitmap bitmap;
        private  int insert_id;
        private int success;
        CreateItemTask(final String item, final String description, final Integer category_id, String image_type, Bitmap bitmap)
        {
            Toast.makeText(getContext(),"Please wait...",Toast.LENGTH_SHORT).show();
            this.item = item;
            this.description = description;
            this.category_id = category_id;
            this.image_type = image_type;
            this.bitmap = bitmap;
            jsonParser = new JSONParser();
            Log.d("CRATEITEM"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("seller_email",serverAccount.getEmail()));
            info.add(new BasicNameValuePair("item",item));
            info.add(new BasicNameValuePair("description",description));
            info.add(new BasicNameValuePair("category_id",Integer.toString(category_id)));
            info.add(new BasicNameValuePair("sizes",sizes));
            info.add(new BasicNameValuePair("prices",prices));
            info.add(new BasicNameValuePair("image_type",image_type));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_add_item,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    insert_id = jsonObject.getInt("id");
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
                uploadBitmap(bitmap, insert_id);

            }
            else if(success==-2)
            {
                Log.e("adding item", "error");
                Toast.makeText(getContext(),"Item already defined",Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class CategoriesTask extends AsyncTask<Void, Void, Boolean>
    {
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private  JSONParser jsonParser;
        private List<Categories> list;
        @Override
        protected void onPreExecute()
        {
            Log.d("SCATEGORIES: ","starting....");
            jsonParser = new JSONParser();
            list = new ArrayList<>();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email",serverAccount.getEmail()));
            // making HTTP request
            String url_get_s_categories = base_url + "get_categories.php";
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_categories,"POST",info);
            Log.d("sCategories",""+jsonObject.toString());
            try
            {
                JSONArray categoriesArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    categoriesArrayList=jsonObject.getJSONArray("categories");
                    for(int count=0; count<categoriesArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=categoriesArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        String title=jsonObjectNotis.getString("title");
                        String description=jsonObjectNotis.getString("description");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        Categories categories =new Categories(id,title,description,image_type,date_added,date_changed);
                        list.add(categories);
                        MainActivity.categoriesLinkedHashMap.put(id,categories);
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
        protected void onPostExecute(final Boolean successful) {

            if (successful)
            {
                mymenuCategoryRecyclerViewAdapter.listUpdated(list);
            }
            else
            {

            }
        }
    }
    private class MenuTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_get_s_items = base_url + "get_seller_items.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;
        private List<DMenu>list;
        @Override
        protected void onPreExecute()
        {
            Log.d("SITEMS: ","starting....");
            jsonParser = new JSONParser();
            list = new ArrayList<>();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("email",serverAccount.getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_s_items,"POST",info);
            Log.d("sItems",""+jsonObject.toString());
            try
            {
                JSONArray itemsArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    itemsArrayList=jsonObject.getJSONArray("items");
                    for(int count=0; count<itemsArrayList.length(); count+=1)
                    {
                        JSONObject jsonObjectNotis=itemsArrayList.getJSONObject(count);
                        int id=jsonObjectNotis.getInt("id");
                        int category_id=jsonObjectNotis.getInt("category_id");
                        int group_id;
                        try
                        {
                            group_id=jsonObjectNotis.getInt("group_id");
                        }
                        catch (Exception e)
                        {
                            group_id = -1;
                        }
                        String linked_items = jsonObjectNotis.getString("linked_items");
                        String item=jsonObjectNotis.getString("item");
                        String description=jsonObjectNotis.getString("description");
                        String sizes = jsonObjectNotis.getString("sizes");
                        String prices = jsonObjectNotis.getString("prices");
                        String image_type=jsonObjectNotis.getString("image_type");
                        String date_added=jsonObjectNotis.getString("date_added");
                        String date_changed=jsonObjectNotis.getString("date_changed");

                        DMenu dMenu =new DMenu(id,category_id,group_id,linked_items, item,description,sizes, prices,image_type,date_added,date_changed);
                        list.add(dMenu);
                        MainActivity.menuLinkedHashMap.put(id,dMenu);

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
        protected void onPostExecute(final Boolean successful) {


            if (successful)
            {
                mymenuRecyclerViewAdapter.listUpdated(list);
            }
            else
            {

            }
        }
    }

}