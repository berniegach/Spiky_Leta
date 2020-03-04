package com.spikingacacia.leta.ui.reports;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

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

import java.text.DateFormat;
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
import java.util.TimeZone;
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
    private List<Date> dates_list_hourly;
    private List<Double> prices_list_hourly;
    private List<Date> dates_list_daily;
    private List<Double> prices_list_daily;
    private List<Date> dates_list_monthly;
    private List<Double> prices_list_monthly;
    private List<Date> dates_list_yearly;
    private List<Double> prices_list_yearly;
    private Boolean[] dates_hourly_lists_done={false,false,false,false};
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
                graph_radio_checked_id=checkedId;
                final SimpleDateFormat f_mFormat=get_my_date_format(true);
                chart.getXAxis().setValueFormatter(new ValueFormatter()
                {
                    @Override
                    public String getFormattedValue(float value)
                    {
                        long millis = TimeUnit.HOURS.toMillis((long) value);
                        return f_mFormat.format(new Date(millis));
                    }
                });
                //formData();
                int which=1;
                if(checkedId==R.id.check_hourly)
                    which=1;
                else if(checkedId==R.id.check_daily)
                    which=2;
                else if(checkedId==R.id.check_monthly)
                    which=3;
                else if(checkedId==R.id.check_yearly)
                    which=4;
                if(!dates_hourly_lists_done[which-1])
                {
                    Toast.makeText(getContext(), "Please wai for data to load", Toast.LENGTH_SHORT).show();
                    return;
                }
                setData(which);
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
                                    setData(1);
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
                                    setData(1);
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
        new form_data_task(1).execute((Void)null);
        new form_data_task(2).execute((Void)null);
        new form_data_task(3).execute((Void)null);
        new form_data_task(4).execute((Void)null);
        setLineChart();
        //setData();

        // redraw
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
    private class form_data_task extends AsyncTask<Void, Void, Boolean>
    {
        int which=1;
        public form_data_task(int which)
        {
            this.which=which;
            switch (which)
            {
                case 1:
                    dates_list_hourly=new ArrayList<>();
                    prices_list_hourly=new ArrayList<>();
                    break;
                case 2:
                    dates_list_daily=new ArrayList<>();
                    prices_list_daily=new ArrayList<>();
                    break;
                case 3:
                    dates_list_monthly=new ArrayList<>();
                    prices_list_monthly=new ArrayList<>();
                    break;
                case 4:
                    dates_list_yearly=new ArrayList<>();
                    prices_list_yearly=new ArrayList<>();
            }
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //first get the earlist order date
            Date earliest_date=null;
            Iterator iterator= sOrdersList.entrySet().iterator();
            int count_date=0;
            while (iterator.hasNext())
            {
                LinkedHashMap.Entry<Integer, SOrders> set = (LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
                SOrders bOrders = set.getValue();
                int orderStatus = bOrders.getOrderStatus();
                String dateChanged = bOrders.getDateChanged();
                String[] date_pieces = dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
                if (orderStatus != 5)
                    continue;
                earliest_date = get_date_from_server_string(dateChanged);
                if(count_date==0)
                    break;
            }
            //daily
            Calendar cal_start=Calendar.getInstance(), cal_end=Calendar.getInstance(), cal_count=Calendar.getInstance();
            cal_start.setTime(earliest_date);
            for(cal_count=(Calendar)cal_start.clone(); cal_count.before(cal_end) || cal_count.equals(cal_end); cal_count=increment_calender(cal_count,which))
            {

                double total_price=0;
                iterator= sOrdersList.entrySet().iterator();
                while (iterator.hasNext())
                {
                    LinkedHashMap.Entry<Integer, SOrders> set = (LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
                    SOrders bOrders = set.getValue();
                    int orderStatus = bOrders.getOrderStatus();
                    String dateChanged = bOrders.getDateChanged();
                    double price=bOrders.getPrice();
                    String[] date_pieces = dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
                    if (orderStatus != 5)
                        continue;
                    Date date= get_date_from_server_string(dateChanged);
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(date);
                    if(calendar.equals(cal_count) || calendar.after(cal_count))
                    {
                        Calendar cal2=(Calendar) cal_count.clone();
                        cal2=increment_calender(cal2,which);
                        if(calendar.before(cal2))
                        {
                            total_price+=price;
                        }
                    }
                }
                if(total_price>0)
                {
                    switch (which)
                    {
                        case 1:
                            dates_list_hourly.add(cal_count.getTime());
                            prices_list_hourly.add(total_price);
                            break;
                        case 2:
                            dates_list_daily.add(cal_count.getTime());
                            prices_list_daily.add(total_price);
                            break;
                        case 3:
                            dates_list_monthly.add(cal_count.getTime());
                            prices_list_monthly.add(total_price);
                            break;
                        case 4:
                            dates_list_yearly.add(cal_count.getTime());
                            prices_list_yearly.add(total_price);
                    }
                }
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean successful)
        {
            dates_hourly_lists_done[which-1]=true;
            if(which==1)
            {
                setData(1);
                chart.invalidate();
            }
        }
    }
    private void setData(int which)
    {
        ArrayList<Entry> values = new ArrayList<>();
        Calendar c_day_before=Calendar.getInstance();
        Calendar c_day_after=Calendar.getInstance();

        switch (which)
        {
            case 1:
                for(int c=0; c<dates_list_hourly.size(); c++)
                {
                    if(c==0)
                    {
                        c_day_before.setTime(dates_list_hourly.get(0));
                        c_day_before=increment_calender(c_day_before,-1);
                        values.add(new Entry(c_day_before.getTimeInMillis()/(1000*60*60), 0));
                    }

                    values.add(new Entry(dates_list_hourly.get(c).getTime()/(1000*60*60), prices_list_hourly.get(c).floatValue()));
                    if(c==dates_list_hourly.size()-1)
                    {
                        c_day_after.setTime(dates_list_hourly.get(dates_list_hourly.size()-1));
                        c_day_after=increment_calender(c_day_after,-1);
                        values.add(new Entry(c_day_after.getTimeInMillis()/(1000*60*60), 0));
                    }
                }
                break;
            case 2:

                for(int c=0; c<dates_list_daily.size(); c++)
                {
                    if(c==0)
                    {
                        c_day_before.setTime(dates_list_daily.get(0));
                        c_day_before=increment_calender(c_day_before,-1);
                        values.add(new Entry(c_day_before.getTimeInMillis()/(1000*60*60), 0));
                    }
                    values.add(new Entry(dates_list_daily.get(c).getTime()/(1000*60*60), prices_list_daily.get(c).floatValue()));
                    if(c==dates_list_hourly.size()-1)
                    {
                        c_day_after.setTime(dates_list_daily.get(dates_list_daily.size()-1));
                        c_day_after=increment_calender(c_day_after,-1);
                        values.add(new Entry(c_day_after.getTimeInMillis()/(1000*60*60), 0));
                    }
                }
                break;
            case 3:
                for(int c=0; c<dates_list_monthly.size(); c++)
                {
                    if(c==0)
                    {
                        c_day_before.setTime(dates_list_monthly.get(0));
                        c_day_before=increment_calender(c_day_before,-1);
                        values.add(new Entry(c_day_before.getTimeInMillis()/(1000*60*60), 0));
                    }
                    values.add(new Entry(dates_list_monthly.get(c).getTime()/(1000*60*60), prices_list_monthly.get(c).floatValue()));
                    if(c==dates_list_hourly.size()-1)
                    {
                        c_day_after.setTime(dates_list_monthly.get(dates_list_monthly.size()-1));
                        c_day_after=increment_calender(c_day_after,-1);
                        values.add(new Entry(c_day_after.getTimeInMillis()/(1000*60*60), 0));
                    }
                }
                break;
            case 4:
                for(int c=0; c<dates_list_yearly.size(); c++)
                {
                    if(c==0)
                    {
                        c_day_before.setTime(dates_list_yearly.get(0));
                        c_day_before=increment_calender(c_day_before,-1);
                        values.add(new Entry(c_day_before.getTimeInMillis()/(1000*60*60), 0));
                    }
                    values.add(new Entry(dates_list_yearly.get(c).getTime()/(1000*60*60), prices_list_yearly.get(c).floatValue()));
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
    /*
    convert the string stored in the servers to Date
    @param str, date stored in string in format "dd-MM-yyyy HH:mm"
    @return Date
     */
    public Date get_date_from_server_string(String str)
    {
       Date date;
       SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        try
        {
            date = mFormat.parse(str);
            return date;
        } catch (ParseException e)
        {
            Log.e(TAG, "date parse error get_date "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    private Calendar increment_calender(Calendar calendar_to_add, int which)
    {
        switch (which)
        {
            case 1:
                calendar_to_add.add(Calendar.HOUR,1);
                break;
            case 2:
                calendar_to_add.add(Calendar.DAY_OF_MONTH,1);
                break;
            case 3:
                calendar_to_add.add(Calendar.MONTH,1);
                break;
            case 4:
                calendar_to_add.add(Calendar.YEAR,1);
                break;

        }
        return calendar_to_add;
    }
    /*
    using the four radiobuttons hourly, daily, monthly and yearly,
    get the date format to match the checked radio button
     */
    private SimpleDateFormat get_my_date_format(boolean for_graph)
    {
        SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy HH", Locale.ENGLISH);
        switch (graph_radio_checked_id)
        {
            case 1:
            case R.id.check_hourly:
                if(for_graph)
                    mFormat=new SimpleDateFormat("HH:mm",Locale.ENGLISH);
                else
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
        return mFormat;
    }

}
