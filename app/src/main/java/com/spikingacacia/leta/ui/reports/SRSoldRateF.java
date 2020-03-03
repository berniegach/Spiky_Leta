package com.spikingacacia.leta.ui.reports;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.SOrders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
    private Preferences preferences;
    int graph_radio_checked_id=1;
    TextView t_start;
    TextView t_end;
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
        setHasOptionsMenu(true);
        if (getArguments() != null)
        {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        preferences = new Preferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_srsold_rate, container, false);
        if(!preferences.isDark_theme_enabled())
        {
            view.findViewById(R.id.chart_back).setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
        }
        chart=view.findViewById(R.id.chart);

        //font
        font= ResourcesCompat.getFont(getContext(),R.font.arima_madurai);
        final RadioGroup g_time=view.findViewById(R.id.radio_graph_time);
        g_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ENGLISH);
                switch (checkedId)
                {
                    case R.id.check_hourly:
                        mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        break;
                    case R.id.check_daily:
                        mFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);
                        break;
                    case R.id.check_monthly:
                        mFormat = new SimpleDateFormat("MM-yy", Locale.ENGLISH);
                        break;
                    case R.id.check_yearly:
                        mFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
                        break;

                }
                final SimpleDateFormat f_mFormat=mFormat;
                chart.getXAxis().setValueFormatter(new ValueFormatter()
                {
                    @Override
                    public String getFormattedValue(float value)
                    {
                        long millis = TimeUnit.HOURS.toMillis((long) value);
                        return f_mFormat.format(new Date(millis));
                    }
                });
                graph_radio_checked_id=checkedId;
                formData();
                setData();
                chart.invalidate();


            }
        });
        t_start=view.findViewById(R.id.start);
        t_end=view.findViewById(R.id.end);
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
                                    setData();
                                    chart.invalidate();
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
                                    setData();
                                    chart.invalidate();
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
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        t_end.setText("End "+sdf.format(cal.getTime()));
        try
        {
            end=sdf.parse(sdf.format(cal.getTime()));
        } catch (ParseException e)
        {
            Log.e(TAG,"could not parse date "+e.getMessage());
        }
        formData();
        setLineChart();
        setData();

        // redraw
        chart.invalidate();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.line, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IDataSet set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = chart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }

        }
        return true;
    }

    private void setLineChart()
    {
        chart.setViewPortOffsets(0, 0, 0, 0);
        if(preferences.isDark_theme_enabled())
            chart.setBackgroundColor(Color.BLACK);
        else
            chart.setBackgroundColor(Color.WHITE);
        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setLabelCount(5, false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setDrawGridLines(true);
        x.setValueFormatter(new ValueFormatter()
        {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value)
            {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis y = chart.getAxisLeft();
        //y.setTypeface(tfLight);
        y.setLabelCount(6, false);

        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(true);

        if(preferences.isDark_theme_enabled())
        {
            y.setAxisLineColor(Color.WHITE);
            x.setTextColor(Color.WHITE);
            x.setAxisLineColor(Color.WHITE);
        }
        else
        {
            y.setAxisLineColor(Color.BLACK);
            x.setTextColor(Color.BLACK);
            x.setAxisLineColor(Color.BLACK);
        }

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        // don't forget to refresh the drawing
        chart.invalidate();
    }
    private long timeInHours(String dt)
    {
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
        switch (graph_radio_checked_id)
        {
            case 1:
            case R.id.check_hourly:
                mFormat= new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
                break;
            case R.id.check_daily:
                mFormat= new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                break;
            case R.id.check_monthly:
                mFormat= new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
                break;
            case R.id.check_yearly:
                mFormat= new SimpleDateFormat("yyyy", Locale.ENGLISH);
                break;
        }
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
        int count_date=0;
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
            SOrders bOrders=set.getValue();
            int orderStatus=bOrders.getOrderStatus();
            String dateChanged=bOrders.getDateChanged();
            String[] date_pieces=dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
            if(orderStatus!=5)
                continue;
            SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String formatted_date="";
            try
            {
                //start=mFormat.parse(date_pieces[0].split(" ")[0]);
                switch (graph_radio_checked_id)
                {
                    case 1:
                    case R.id.check_hourly:
                        mFormat= new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
                        break;
                    case R.id.check_daily:
                        mFormat= new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        break;
                    case R.id.check_monthly:
                        mFormat= new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
                        break;
                    case R.id.check_yearly:
                        mFormat= new SimpleDateFormat("yyyy", Locale.ENGLISH);
                        break;
                }
                if(count_date==0)
                {
                    start=mFormat.parse(date_pieces[0]);
                    t_start.setText("Start " + mFormat.format(start));
                    count_date+=1;
                }
                formatted_date=mFormat.format( mFormat.parse(date_pieces[0]));
            }
            catch (ParseException e)
            {
                Log.e(TAG," could not parse date "+e.getMessage());
            }
            order_dates.add(formatted_date);
        }
        Set<String> unique_dates=new HashSet<>(order_dates);
        unique_dates_list=new ArrayList<>(unique_dates);
        Collections.sort(unique_dates_list);
        prices_list=new ArrayList<>();
        //initialize the prices to 0
        for(int c=0; c<unique_dates_list.size(); c++)
            prices_list.add(0.0);
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
                String date_to_compare;
                switch (graph_radio_checked_id)
                {
                    case 1:
                    case R.id.check_hourly:
                        date_to_compare=date_pieces[0];
                        break;
                    case R.id.check_daily:
                        date_to_compare=date_pieces[0].split(" ")[0];
                        break;
                    case R.id.check_monthly:
                    {
                        String[] dates=date_pieces[0].split(" ")[0].split("-");
                        date_to_compare=dates[1]+"-"+dates[2];
                        break;
                    }
                    case R.id.check_yearly:
                    {
                        String[] dates=date_pieces[0].split(" ")[0].split("-");
                        date_to_compare=dates[2];
                        break;
                    }
                    default:
                        date_to_compare=date_pieces[0];
                }
                if(unique_dates_list.get(c).contentEquals(date_to_compare))
                    prices_list.set(c,prices_list.get(c)+price);
                if(prices_list.get(c)>maxPrice)
                    maxPrice=prices_list.get(c);
            }
        }
        for(int c=0; c<unique_dates_list.size(); c++)
        {
            Log.d(TAG,unique_dates_list.get(c));
            Log.d(TAG,prices_list.get(c).toString());
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
            switch (graph_radio_checked_id)
            {
                case 1:
                case R.id.check_hourly:
                    mFormat=new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
                    break;
                case R.id.check_daily:
                    mFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    break;
                case R.id.check_monthly:
                    mFormat=new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
                    break;
                case R.id.check_yearly:
                    mFormat=new SimpleDateFormat("yyyy", Locale.ENGLISH);
                    break;

            }

            if( (start!=null) && (end!=null) )
            {
                try
                {

                    Date date= mFormat.parse(unique_dates_list.get(c));

                    if((date.after(start) || date.equals(start)) && (date.before(end) || date.equals(end)) )
                    {
                        values.add(new Entry(time, price));
                    }

                }
                catch (ParseException e)
                {
                    Log.e(TAG,"error setting time 2 "+e.getMessage());
                }
            }
            else
            {
                values.add(new Entry(time, price));
            }
        }

        LineDataSet set1 = new LineDataSet(values, "Hourly sales");
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Hourly sales");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            //set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            //set1.setColor(Color.BLUE);
            set1.setFillColor(Color.rgb(104, 241, 175));
            set1.setFillAlpha(200);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            //data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

}
