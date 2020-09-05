package com.spikingacacia.leta.ui.tasty;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.emoji.widget.EmojiTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.spikingacacia.leta.ui.JSONParser;
import com.spikingacacia.leta.ui.LoginActivity;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.TastyBoard;
import com.spikingacacia.leta.ui.util.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;

public class TastyBoardOverviewFragment extends Fragment
{
    private static final String ARG_TASTY_BOARD = "param1";
    private static  TastyBoard tastyBoard;
    DemoCollectionPagerAdapter demoCollectionPagerAdapter;
    ViewPager viewPager;
    private static Context context;
    private String TAG = "tasty_board_overview_f";

    public TastyBoardOverviewFragment()
    {
        // Required empty public constructor
    }

    public static TastyBoardOverviewFragment newInstance(TastyBoard tastyBoard)
    {
        TastyBoardOverviewFragment fragment = new TastyBoardOverviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASTY_BOARD, tastyBoard);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            tastyBoard =(TastyBoard) getArguments().getSerializable(ARG_TASTY_BOARD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tasty_board_overview, container, false);
        context = getContext();
        ConstraintLayout l_less = view.findViewById(R.id.layout_less);
        ImageView imageView = view.findViewById(R.id.image);
        TextView t_title = view.findViewById(R.id.title);
        TextView t_restaurant = (TextView) view.findViewById(R.id.restaurant);
        TextView t_location = (TextView) view.findViewById(R.id.location);
        TextView t_discount = (TextView) view.findViewById(R.id.discount);
        ImageView imageSeller = view.findViewById(R.id.image_seller);

        demoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        ImageButton b_expand_less = view.findViewById(R.id.expand_less);
        ImageButton b_expand_more = view.findViewById(R.id.expand_more);
        Utils.collapse(viewPager);
        b_expand_more.setVisibility(View.GONE);

        b_expand_less.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setVisibility(View.GONE);
                b_expand_more.setVisibility(View.VISIBLE);
                Utils.collapse(l_less);
                Utils.expand(viewPager);
            }
        });
        b_expand_more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setVisibility(View.GONE);
                b_expand_less.setVisibility(View.VISIBLE);
                Utils.collapse(viewPager);
                Utils.expand(l_less);
            }
        });

        t_title.setText(tastyBoard.getTitle());
        t_restaurant.setText(LoginActivity.getServerAccount().getUsername());
        String s_distance = "Here";
        Double distance = -1.0;//tastyBoard.getDistance();
        t_location.setText(s_distance);

        String image_url= base_url+"src/tasty_board_pics/";
        // image
        String url=image_url+String.valueOf(tastyBoard.getId())+'_'+String.valueOf(tastyBoard.getImageType());
        Glide.with(getContext()).load(url).into(imageView);
        String url_seller= LoginActivity.base_url+"src/sellers_pics/"+ LoginActivity.getServerAccount().getId()+'_'+LoginActivity.getServerAccount().getImageType();
        Glide.with(getContext()).load(url_seller).into(imageSeller);

        //sizes and prices
        String[] sizes_and_prices = tastyBoard.getSizeAndPrice().split(":");
        String[] new_prices = tastyBoard.getDiscountPrice().split(":");
        String[] sizes = new String[sizes_and_prices.length];
        double[] old_prices = new double[sizes_and_prices.length];
        int[] discounts = new int[sizes_and_prices.length];
        String s_discount="";
        String currency="";
        String[] location_pieces = LoginActivity.getServerAccount().getLocation().split(":");
        if(location_pieces.length==4)
            currency = getCurrencyCode(location_pieces[3])+" ";
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
            t_discount.setVisibility(View.INVISIBLE);


        return view;
    }

    //to retrieve currency code
    private String getCurrencyCode(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
    }
    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter
    {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            if(i==0)
                fragment = new DescriptionFragment();
            else
                fragment = new CommentsFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if(position==0)
                return "Description";
            else
                return "Reviews";
        }
    }

    public static class DescriptionFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_description, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            ((TextView) view.findViewById(R.id.description)).setText(tastyBoard.getDescription());
        }
    }
    public static class CommentsFragment extends Fragment
    {
        private LinearLayout layout;
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_reviews_list, container, false);
            layout = view.findViewById(R.id.base);

            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            new GetCommentsTask().execute((Void)null);
        }
        private class GetCommentsTask extends AsyncTask<Void, Void, Boolean>
        {
            private String url_get_comments= base_url+"get_tasty_board_comments.php";
            private JSONParser jsonParser;
            private String TAG_SUCCESS="success";
            private String TAG_MESSAGE="message";
            private List<Comments> list = new ArrayList<>();
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
                    for(int c=0; c<list.size(); c++)
                    {
                        addNewLayout(list.get(c));
                    }


                }
                else
                {

                }
            }
            private void addNewLayout(Comments comments)
            {
                final View view = getLayoutInflater().inflate(R.layout.fragment_reviews, null);
                ImageView imageView = view.findViewById(R.id.image);
                TextView t_names = view.findViewById(R.id.names);
                TextView t_time = view.findViewById(R.id.time);
                EmojiTextView t_comment = view.findViewById(R.id.comment);

                t_names.setText(comments.names);
                try
                {
                    byte[] data = Base64.decode(comments.comment, Base64.DEFAULT);
                    String newStringWithEmojis = new String(data, "UTF-8");
                    t_comment.setText(newStringWithEmojis);

                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                    t_comment.setText(comments.comment);
                }

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                PrettyTime p = new PrettyTime();
                try
                {
                    t_time.setText(p.format(format.parse(comments.date)));
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
                String url_seller= LoginActivity.base_url+"src/buyers_pics/"+ comments.buyer_id+'_'+comments.buyer_image_type;
                Glide.with(context).load(url_seller).into(imageView);
                layout.addView(view);

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

    }
}