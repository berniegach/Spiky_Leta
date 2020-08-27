package com.spikingacacia.leta.ui.main.wallet;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static com.spikingacacia.leta.ui.LoginActivity.base_url;
import static com.spikingacacia.leta.ui.main.wallet.WalletHomeFragment.transactionsList;


public class WalletGraphFragment extends Fragment implements
        OnChartValueSelectedListener
{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private LineChart chart;
    private String TAG = "performance_graph_f";
    private LinkedHashMap<Long,Double> milliseconds_data;

    public WalletGraphFragment()
    {
        // Required empty public constructor
    }

    public static WalletGraphFragment newInstance(String param1, String param2)
    {
        WalletGraphFragment fragment = new WalletGraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_graph, container, false);
        chart = view.findViewById(R.id.chart1);

        milliseconds_data = new LinkedHashMap<>();

        setChart();
        setData();
        return view;
    }
    private void setChart()
    {
        chart.setOnChartValueSelectedListener(this);

        // enable description text
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText("");

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setGranularity(1f); // one hour
        /*xl.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value)
            {
                String days[] =getResources().getStringArray(R.array.days);
                int day = (int) value % days.length;
                if(day<0)
                    return "";
                    else
                        return days[day];
            }
        });*/
        xl.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return "";//mFormat.format(new Date(millis));
            }
        });
        //xl.setDrawGridLines(true);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }
    private void setData()
    {
        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null)
            {
                set = createSet("Balance");
                data.addDataSet(set);
            }
            String date_added="";
            for(int c=0; c<transactionsList.size(); c++)
            {
                date_added = transactionsList.get(c).getDate_added();
                Double balance = Double.valueOf(transactionsList.get(c).getBalance());
                data.addEntry(new Entry(get_milliseconds_from_server_string(date_added),balance.floatValue()),0);
                data.notifyDataChanged();

            }
            //if the granularity is 1 it means we add in terms of hours
            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);


            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            //chart.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(get_milliseconds_from_server_string(date_added));

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }



    }

    private LineDataSet createSet(String label) {

        LineDataSet set = new LineDataSet(null, label );
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        //set.setCircleColor(Color.rgb(200, 150, 117));
        set.setCircleColor(Color.parseColor("#FF5C5C"));
        set.setDrawCircleHole(false);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }




    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }



    public long get_milliseconds_from_server_string(String str)
    {
        Date date;
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        try
        {
            date = mFormat.parse(str);
            return date.getTime();
        } catch (ParseException e)
        {
            Log.e(TAG, "date parse error get_date "+e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }




}