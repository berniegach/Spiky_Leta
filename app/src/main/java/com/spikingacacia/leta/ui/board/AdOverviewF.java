package com.spikingacacia.leta.ui.board;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spikingacacia.leta.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdOverviewF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdOverviewF extends Fragment
{
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
        if (getArguments() != null)
        {
            id = getArguments().getString(ARG_PARAM_ID);
            title = getArguments().getString(ARG_PARAM_TITLE);
            bitmap =  getArguments().getParcelable(ARG_PARAM_IMAGE);
            content = getArguments().getString(ARG_PARAM_CONTENT);
            views = getArguments().getString(ARG_PARAM_VIEWS);
            likes = getArguments().getString(ARG_PARAM_LIKES);
            comments = getArguments().getString(ARG_PARAM_COMMENTS);
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
        ((TextView)view.findViewById(R.id.views)).setText(String.valueOf(views));
        ((TextView)view.findViewById(R.id.likes)).setText(String.valueOf(likes));
        ((TextView)view.findViewById(R.id.comments)).setText(String.valueOf(comments));
        ((TextView)view.findViewById(R.id.date)).setText(date);

        return view;
    }
}
