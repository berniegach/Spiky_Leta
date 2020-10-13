/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:06 PM
 */

package com.spikingacacia.leta.ui.board;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.Preferences;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdOverviewF extends Fragment
{
    private String url_get_comments= base_url+"get_ad_comments.php";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private static final String ARG_PARAM_ID = "id";
    private static final String ARG_PARAM_TITLE = "title";
    private static final String ARG_PARAM_CONTENT = "content";
    private static final String ARG_PARAM_IMAGE = "image";
    private static final String ARG_PARAM_VIEWS = "views";
    private static final String ARG_PARAM_LIKES = "likes";
    private static final String ARG_PARAM_COMMENTS = "comments";
    private static final String ARG_PARAM_DATE = "date";

    private String id;
    private String title;
    private Bitmap bitmap;
    private String content;
    private String views;
    private String likes;
    private String comments;
    private String date;
    private Preferences preferences;
    private LinearLayout l_comments;

    public AdOverviewF()
    {
        // Required empty public constructor
    }
    public static AdOverviewF newInstance(String id, String title, Bitmap bitmap, String content, String views, String likes, String comments, String date)
    {
        AdOverviewF fragment = new AdOverviewF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ID, id);
        args.putString(ARG_PARAM_TITLE, title);
        args.putParcelable(ARG_PARAM_IMAGE,bitmap);
        args.putString(ARG_PARAM_CONTENT,content);
        args.putString(ARG_PARAM_VIEWS,views);
        args.putString(ARG_PARAM_LIKES,likes);
        args.putString(ARG_PARAM_COMMENTS,comments);
        args.putString(ARG_PARAM_DATE,date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences=new Preferences(getContext());
        jsonParser=new JSONParser();
        if (getArguments() != null)
        {
            id = getArguments().getString(ARG_PARAM_ID);
            title = getArguments().getString(ARG_PARAM_TITLE);
            bitmap =  getArguments().getParcelable(ARG_PARAM_IMAGE);
            content = getArguments().getString(ARG_PARAM_CONTENT);
            views = getArguments().getString(ARG_PARAM_VIEWS);
            likes = getArguments().getString(ARG_PARAM_LIKES);
            comments = getArguments().getString(ARG_PARAM_COMMENTS);
            date = getArguments().getString(ARG_PARAM_DATE);
            //format the date
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            PrettyTime p = new PrettyTime();
            try
            {
                date = p.format(format.parse(date));
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_ad_overview, container, false);
        ((TextView)view.findViewById(R.id.title)).setText(title);
        ((ImageView)view.findViewById(R.id.image)).setImageBitmap(bitmap);
        ((TextView)view.findViewById(R.id.content)).setText(content);
        ((TextView)view.findViewById(R.id.views)).setText(String.valueOf(views)+" views");
        ((TextView)view.findViewById(R.id.likes)).setText(String.valueOf(likes)+" likes");
        ((TextView)view.findViewById(R.id.comments)).setText(String.valueOf(comments)+" comments");
        ((TextView)view.findViewById(R.id.date)).setText(date);
        l_comments=view.findViewById(R.id.l_comments);

        new GetCommentsTask(id).execute((Void)null);

        return view;
    }
    private class GetCommentsTask extends AsyncTask<Void, Void, Boolean>
    {
        final String id;
        LinkedHashMap<Integer,Comments> comments_list;

        public GetCommentsTask(String id)
        {
            this.id=id;
            comments_list= new LinkedHashMap<>();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("ad_id",id));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_comments,"POST",info);
            Log.d("comments",""+jsonObject.toString());
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
                        int comment_id=jsonObjectNotis.getInt("id");
                        int buyer_ad_id=jsonObjectNotis.getInt("ad_id");
                        int buyer_id=jsonObjectNotis.getInt("buyer_id");
                        String buyer_comment=jsonObjectNotis.getString("comment");
                        String buyer_names=jsonObjectNotis.getString("names");
                        String buyer_date=jsonObjectNotis.getString("date_added");

                        comments_list.put(comment_id,new Comments(comment_id,buyer_ad_id,buyer_id,buyer_comment,buyer_names,buyer_date));
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
                Iterator iterator= comments_list.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, Comments> set = (LinkedHashMap.Entry<Integer, Comments>) iterator.next();
                    Comments comments=set.getValue();
                    int comment_id=comments.comment_id;
                    int buyer_ad_id=comments.buyer_ad_id;
                    int buyer_id=comments.buyer_id;
                    String buyer_comment=comments.buyer_comment;
                    String buyer_names=comments.buyer_names;
                    String buyer_date=comments.buyer_date;
                    find_buyer_image(comment_id, buyer_ad_id, buyer_id, buyer_comment, buyer_names,  buyer_date);
                }

            }
            else
            {

            }
        }

        private void find_buyer_image(final int comment_id, final int buyer_ad_id, final int buyer_id, final String buyer_comment, final String buyer_names, final String buyer_date)
        {
            String url= base_url+"src/buyers/"+String.format("%s/pics/prof_pic",makeName(buyer_id))+".jpg";
            ImageRequest request=new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap response)
                        {
                            add_comment_layout(response,comment_id, buyer_ad_id, buyer_id, buyer_comment, buyer_names,  buyer_date);
                            Log.d("volley","succesful");
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError e)
                        {
                            add_comment_layout(null,comment_id, buyer_ad_id, buyer_id, buyer_comment, buyer_names,  buyer_date);
                            Log.e("voley",""+e.getMessage()+e.toString());
                        }
                    });
            RequestQueue request2 = Volley.newRequestQueue(getContext());
            request2.add(request);
        }

        private String makeName(int id)
        {
            String letters=String.valueOf(id);
            char[] array=letters.toCharArray();
            String name="";
            for(int count=0; count<array.length; count++)
            {
                switch (array[count])
                {
                    case '0':
                        name+="zero";
                        break;
                    case '1':
                        name+="one";
                        break;
                    case '2':
                        name+="two";
                        break;
                    case '3':
                        name+="three";
                        break;
                    case '4':
                        name+="four";
                        break;
                    case '5':
                        name+="five";
                        break;
                    case '6':
                        name+="six";
                        break;
                    case '7':
                        name+="seven";
                        break;
                    case '8':
                        name+="eight";
                        break;
                    case '9':
                        name+="nine";
                        break;
                    default :
                        name+="NON";
                }
            }
            return name;
        }
        private void add_comment_layout(Bitmap bitmap, int comment_id, int buyer_ad_id, int buyer_id, String buyer_comment, String buyer_names, String buyer_date)
        {
            ImageView imageView=new ImageView(getContext());
            TextView t_comment=new TextView(getContext());
            TextView t_names=new TextView(getContext());
            t_names.setPadding(10,0,10,0);
            TextView t_date=new TextView(getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(70,70));
            imageView.setImageBitmap(bitmap);
            t_comment.setText(buyer_comment);
            t_names.setText(buyer_names);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            PrettyTime p = new PrettyTime();
            try
            {
                t_date.setText(p.format(format.parse(buyer_date)));
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
            LinearLayout l_1=new LinearLayout(getContext());
            l_1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            l_1.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout l_2=new LinearLayout(getContext());
            l_2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            l_2.setOrientation(LinearLayout.VERTICAL);
            l_2.setGravity(Gravity.END);

            LinearLayout l_3=new LinearLayout(getContext());
            l_3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            l_3.setOrientation(LinearLayout.HORIZONTAL);
            //add the layoutts
            l_3.addView(t_names);
            l_3.addView(t_date);
            l_2.addView(t_comment);
            l_2.addView(l_3);
            l_1.addView(imageView);
            l_1.addView(l_2);
            l_comments.addView(l_1);

        }
    }
    private class Comments
    {
        int comment_id;
        int buyer_ad_id;
        int buyer_id;
        String buyer_comment;
        String buyer_names;
        String buyer_date;
        public Comments(int comment_id, int buyer_ad_id, int buyer_id, String buyer_comment, String buyer_names, String buyer_date)
        {
            this.comment_id = comment_id;
            this.buyer_ad_id = buyer_ad_id;
            this.buyer_id = buyer_id;
            this.buyer_comment = buyer_comment;
            this.buyer_names = buyer_names;
            this.buyer_date = buyer_date;
        }
    }
}
