package com.spikingacacia.leta.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.spikingacacia.leta.R;


/**
 * A fragment representing a single Message detail screen.
 * This fragment is either contained in a {@link SMMessageListActivity}
 * in two-pane mode (on tablets) or a {@link SMMessageDetailActivity}
 * on handsets.
 */
public class SMMessageDetailFragment extends Fragment
{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private SMMessageContent.MessageItem mItem;
    private String[]content;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SMMessageDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        content=getArguments().getStringArray("items");

        /*if (getArguments().containsKey(ARG_ITEM_ID))
        {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = BMMessageContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.smmessage_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (content != null)
        {
            ((TextView) rootView.findViewById(R.id.message_detail)).setText(content[3]);
            ((TextView) rootView.findViewById(R.id.date)).setText(content[4]);
        }

        return rootView;
    }
}
