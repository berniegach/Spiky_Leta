package com.spikingacacia.leta.ui.reports;


import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.SOrders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.spikingacacia.leta.ui.LoginA.sOrdersList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SRSoldRateF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SRSoldRateF extends Fragment
{
    private LineChart chart;
    private String TAG="SRSoldRateF";
    private List<String> unique_dates_list;
    private List<Double> prices_list;
    private double maxPrice=0.0;
    private Date start=null;
    private Date end=null;
    private Typeface font;
    public SRSoldRateF()
    {
        // Required empty public constructor
    }

    public static SRSoldRateF newInstance()
    {
        SRSoldRateF fragment = new SRSoldRateF();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_srsold_rate, container, false);
        chart=view.findViewById(R.id.chart);
        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        final TextView t_start=view.findViewById(R.id.start);
        final TextView t_end=view.findViewById(R.id.end);
        t_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DatePicker datePicker=new DatePicker(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle("Start Date")
                        .setView(datePicker)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                int day=datePicker.getDayOfMonth();
                                int month=datePicker.getMonth()+1;
                                int year=datePicker.getYear();
                                SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                try
                                {
                                    start=mFormat.parse(String.format("%d-%d-%d",day,month,year));
                                    t_start.setText(String.format("%s %d-%d-%d","Start",day,month,year));
                                    //setData();
                                    //chart.invalidate();
                                }
                                catch (ParseException e)
                                {
                                    Log.e(TAG,"error setting start time "+e.getMessage());
                                }


                            }
                        })
                        .create().show();

            }
        });
        t_end.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final DatePicker datePicker=new DatePicker(getContext());
                new AlertDialog.Builder(getContext())
                        .setTitle("End Date")
                        .setView(datePicker)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                int day=datePicker.getDayOfMonth();
                                int month=datePicker.getMonth()+1;
                                int year=datePicker.getYear();
                                SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                try
                                {
                                    end=mFormat.parse(String.format("%d-%d-%d",day,month,year));
                                    t_end.setText(String.format("%s %d-%d-%d","End",day,month,year));
                                    //setData();
                                    //chart.invalidate();
                                }
                                catch (ParseException e)
                                {
                                    Log.e(TAG,"error setting end time "+e.getMessage());
                                }
                            }
                        })
                        .create().show();
            }
        });
        formData();
        setLineChart();
        setData();

        // redraw
        chart.invalidate();
        return view;
    }
    private void setLineChart()
    {
        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(true);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.DKGRAY);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        //seekBarX.setProgress(100);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setTextColor(Color.WHITE);
        l.setTextSize(11f);
        l.setTypeface(font);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTypeface(font);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        //xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter()
        {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value, AxisBase axisBase)
            {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(font);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum((float)maxPrice*2);
        leftAxis.setYOffset(-9f);
        //leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

    }
    private long timeInHours(String dt)
    {
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
        try
        {
            Date date=mFormat.parse(dt);
            long milliseconds=date.getTime();
            return TimeUnit.MILLISECONDS.toHours(milliseconds);
        }
        catch (ParseException e)
        {
            Log.e(TAG,"error setting time "+e.getMessage());
            return 0;
        }

    }
    private void formData()

    {
        List<String> order_dates=new ArrayList<>();
        Iterator iterator= sOrdersList.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
            SOrders bOrders=set.getValue();
            int orderStatus=bOrders.getOrderStatus();
            String dateChanged=bOrders.getDateChanged();
            String[] date_pieces=dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
            if(orderStatus!=5)
                continue;
            Log.d(TAG,"date:"+date_pieces[0]);
            order_dates.add(date_pieces[0]);
        }
        Set<String> unique_dates=new HashSet<>(order_dates);
        unique_dates_list=new ArrayList<>(unique_dates);
        Collections.sort(unique_dates_list);
        prices_list=new ArrayList<>();
        //initialize the prices to 0
        for(int c=0; c<unique_dates_list.size(); c++)
            prices_list.add(0.0);
        Log.d(TAG,"dates:"+unique_dates_list.size());
        Log.d(TAG,"prices:"+prices_list.size());
        //get the hourly price total
        for(int c=0; c<unique_dates_list.size(); c++)
        {
            Iterator iterator_prices= sOrdersList.entrySet().iterator();
            while (iterator_prices.hasNext())
            {
                LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator_prices.next();
                SOrders bOrders=set.getValue();
                int orderStatus=bOrders.getOrderStatus();
                double price=bOrders.getPrice();
                String dateChanged=bOrders.getDateChanged();
                String[] date_pieces=dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
                if(orderStatus!=5)
                    continue;
                if(unique_dates_list.get(c).contentEquals(date_pieces[0]))
                    prices_list.set(c,prices_list.get(c)+price);
                if(prices_list.get(c)>maxPrice)
                    maxPrice=prices_list.get(c);
            }
            Log.d(TAG,"price:"+prices_list.get(c));
        }
    }
    private void setData()
    {
        ArrayList<Entry> values = new ArrayList<>();
        for(int c=0; c<unique_dates_list.size(); c++)
        {
            float time=(float)timeInHours(unique_dates_list.get(c));
            float price=prices_list.get(c).floatValue();
            //check if the dates are within range
            SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
            if( (start!=null) && (end!=null) )
            {
                try
                {
                    Date date=mFormat.parse(unique_dates_list.get(c));
                    if(date.after(start) && date.before(end))
                        values.add(new Entry(time,price));

                }
                catch (ParseException e)
                {
                    Log.e(TAG,"error setting time 2 "+e.getMessage());
                }
            }
            else
                values.add(new Entry(time,price));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Hourly sales");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        chart.setData(data);
    }

}
