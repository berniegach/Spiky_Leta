/*
 * Created by Benard Gachanja on 10/13/20 5:26 PM
 * Copyright (c) 2020 . Spiking Acacia. All rights reserved.
 * Last modified 10/6/20 10:06 AM
 */

package com.spikingacacia.leta.ui.tasty;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.ui.database.TastyBoard;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.util.Utils.getCurrencyCode;


/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     TastyBoardOverviewBottomSheet.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class TastyBoardOverviewBottomSheet extends BottomSheetDialogFragment
{

    private static final String ARG_TASTY_BOARD = "param1";
    private static final String ARG_LISTENER = "param2";
    private static TastyBoard tastyBoard;
    private RecyclerView recyclerView;
    private String TAG = "tasty_board_bottom_sheet";

    public static TastyBoardOverviewBottomSheet newInstance(TastyBoard tastyBoard)
    {
        final TastyBoardOverviewBottomSheet fragment = new TastyBoardOverviewBottomSheet();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_TASTY_BOARD, tastyBoard);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_tasty_board_overview_bottom_sheet, container, false);
        if (getArguments() != null)
        {
            tastyBoard =(TastyBoard) getArguments().getSerializable(ARG_TASTY_BOARD);
        }
        TextView t_title = view.findViewById(R.id.title);
        TextView t_restaurant = (TextView) view.findViewById(R.id.restaurant);
        TextView t_location = (TextView) view.findViewById(R.id.location);
        TextView t_discount = (TextView) view.findViewById(R.id.discount);
        TextView t_description = view.findViewById(R.id.description);
        recyclerView = view.findViewById(R.id.list);


        t_title.setText(tastyBoard.getTitle());
        t_restaurant.setText(LoginActivity.getServerAccount().getUsername());
        t_description.setText(tastyBoard.getDescription());
        t_location.setText("Here");


        //sizes and prices
        String[] sizes_and_prices = tastyBoard.getSizeAndPrice().split(":");
        String[] new_prices = tastyBoard.getDiscountPrice().split(":");
        String[] sizes = new String[sizes_and_prices.length];
        double[] old_prices = new double[sizes_and_prices.length];
        int[] discounts = new int[sizes_and_prices.length];
        String s_discount="";
        String currency="";
        String location = LoginActivity.getServerAccount().getLocation();
        String[] location_pieces = location.split(",");
        if(location_pieces.length==4)
            currency = getCurrencyCode(location_pieces[3]);
        for(int c=0; c<sizes_and_prices.length; c++)
        {
            sizes[c] =sizes_and_prices[c].split(",")[0];
            old_prices[c] =  Double.parseDouble(sizes_and_prices[c].split(",")[1]);
            discounts[c] =(int) ( (old_prices[c] - Double.parseDouble(new_prices[c]))/old_prices[c] *100);
            if(c!=0)
                s_discount+="\n";
            s_discount+=sizes[c]+" @ "+currency+new_prices[c];
        }
        int biggest_discount = 0;
        for(int c=0; c<sizes.length; c++)
        {
            if(discounts[c]>biggest_discount)
                biggest_discount = discounts[c];
        }
        if(biggest_discount>0)
            t_discount.setText(s_discount);
        else
            t_discount.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter());
        new UpdateTastyBoardTask(1).execute((Void)null);
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    {

        final ImageView imageView;
        final TextView t_names;
        final TextView t_time;
        final TextView t_comment;

        ViewHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.fragment_tasty_board_overview_bottom_sheet_item, parent, false));
            imageView = itemView.findViewById(R.id.image);
            t_names = itemView.findViewById(R.id.names);
            t_time = itemView.findViewById(R.id.time);
            t_comment = itemView.findViewById(R.id.comment);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder>
    {

        private List<Comments> list = new ArrayList<>();

        ItemAdapter()
        {
            new GetCommentsTask().execute((Void)null);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            ViewHolder viewHolder =  new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
            return  viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            //holder.text.setText(String.valueOf(position));
            Comments comments = list.get(position);
            holder.t_names.setText(comments.names);
            try
            {
                byte[] data = Base64.decode(comments.comment, Base64.DEFAULT);
                String newStringWithEmojis = new String(data, "UTF-8");
                holder.t_comment.setText(newStringWithEmojis);

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                holder.t_comment.setText(comments.comment);
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            PrettyTime p = new PrettyTime();
            try
            {
                holder.t_time.setText(p.format(format.parse(comments.date)));
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            String url_seller= base_url+"src/buyers_pics/"+ comments.buyer_id+'_'+comments.buyer_image_type;
            Glide.with(getContext()).load(url_seller).into(holder.imageView);
        }

        @Override
        public int getItemCount()
        {
            return list.size();
        }
        private class GetCommentsTask extends AsyncTask<Void, Void, Boolean>
        {
            private String url_get_comments= base_url+"get_tasty_board_comments.php";
            private JSONParser jsonParser;
            private String TAG_SUCCESS="success";
            private String TAG_MESSAGE="message";
            LinkedHashMap<Integer, Comments> comments_list;

            public GetCommentsTask()
            {
                jsonParser = new JSONParser();
                comments_list= new LinkedHashMap<>();
            }
            @Override
            protected Boolean doInBackground(Void... params)
            {
                //getting columns list
                List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
                info.add(new BasicNameValuePair("tasty_board_id",String.valueOf(tastyBoard.getId())));
                // making HTTP request
                JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_comments,"POST",info);
                //Log.d("comments",""+jsonObject.toString());
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
                            int buyer_id = jsonObjectNotis.getInt("buyer_id");
                            String buyer_email=jsonObjectNotis.getString("buyer_email");
                            String comment=jsonObjectNotis.getString("comment");
                            String names=jsonObjectNotis.getString("names");
                            String date=jsonObjectNotis.getString("date_added");
                            String buyer_image_type=jsonObjectNotis.getString("buyer_image_type");

                            list.add(new Comments(id,buyer_id,buyer_email, comment,names,date, buyer_image_type));
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
                    notifyDataSetChanged();

                }
                else
                {

                }
            }

        }

    }
    private class Comments
    {
        public int id;
        public int buyer_id;
        public String buyer_email;
        public String comment;
        public String names;
        public String date;
        public String buyer_image_type;

        public Comments(int id, int buyer_id, String buyer_email, String comment, String names, String date, String buyer_image_type)
        {
            this.id = id;
            this.buyer_id = buyer_id;
            this.buyer_email = buyer_email;
            this.comment = comment;
            this.names = names;
            this.date = date;
            this.buyer_image_type = buyer_image_type;
        }

    }
    private class UpdateTastyBoardTask extends AsyncTask<Void, Void, Boolean>
    {
        private String url_update= base_url+"update_tasty_board_views_and_likes.php";
        private JSONParser jsonParser;
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        final int which;

        @Override
        protected void onPreExecute()
        {
            jsonParser = new JSONParser();
            super.onPreExecute();
        }
        public UpdateTastyBoardTask(int which)
        {
            //which is 1 for view and 2 for likes
            this.which =which;
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("tasty_board_id",String.valueOf(tastyBoard.getId())));
            info.add(new BasicNameValuePair("buyer_email",LoginActivity.getServerAccount().getEmail()));
            info.add(new BasicNameValuePair("which",Integer.toString(which)));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update,"POST",info);
            try
            {
                JSONArray array=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                    return true;
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
                Log.d(TAG,"updated views");
            }
            else
            {

            }
        }

    }

}