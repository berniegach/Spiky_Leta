/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 8/20/20 1:26 PM
 */

package com.spikingacacia.leta.ui.charts;

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
import com.spikingacacia.leta.ui.LoginActivity;

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

import static com.spikingacacia.leta.ui.LoginActivity.base_url;


public class TrafficGraphFragment extends Fragment implements
        OnChartValueSelectedListener
{
    private LineChart chart;
    private String TAG = "traffic_graph_f";
    private LinkedHashMap<Long,Double> milliseconds_data;
    private Thread thread;

    public TrafficGraphFragment()
    {
        // Required empty public constructor
    }

    public static TrafficGraphFragment newInstance()
    {
        TrafficGraphFragment fragment = new TrafficGraphFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_traffic_graph, container, false);

        chart = view.findViewById(R.id.chart1);

        // Selection of the spinner
        final Spinner spinner_years = (Spinner) view.findViewById(R.id.spinner_years);
        final Spinner spinner_months = (Spinner) view.findViewById(R.id.spinner_months);
        final Spinner spinner_days = (Spinner) view.findViewById(R.id.spinner_days);
        spinner_days.setSelection(0,false);
        spinner_months.setSelection(0,false);
        spinner_years.setSelection(0,false);
        spinner_months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                formData("Orders",0,spinner_years.getSelectedItemPosition()==0?0:Integer.parseInt((String)spinner_years.getSelectedItem())
                        ,position,
                        spinner_days.getSelectedItemPosition(),milliseconds_data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinner_years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                formData("Orders",0,position==0?0:Integer.parseInt((String)spinner_years.getSelectedItem()),
                        spinner_months.getSelectedItemPosition(), spinner_days.getSelectedItemPosition(),milliseconds_data);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinner_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                formData("Orders",0,spinner_years.getSelectedItemPosition()==0?0:Integer.parseInt((String)spinner_years.getSelectedItem()),
                        spinner_months.getSelectedItemPosition(), position,milliseconds_data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        milliseconds_data = new LinkedHashMap<>();

        setChart();
        new Data4Task().execute((Void)null);
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
        xl.setValueFormatter(new ValueFormatter() {
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
        });
        //xl.setDrawGridLines(true);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }
    private void addEntry(long time, Double sum) {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null)
            {
                set = createSet("Traffic");
                data.addDataSet(set);
            }
            //if the granularity is 1 it means we add in terms of hours
            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.addEntry(new Entry(time,  sum.floatValue()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            //chart.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(time);

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
    class OneShotTask implements Runnable
    {
        String label;
        int datasetIndex;
        Long time;
        Double sum;
        OneShotTask(String label, int datasetIndex,  Long time, Double sum)
        {
            this.time = time;
            this.sum = sum;
            this.label = label;
            this.datasetIndex = datasetIndex;
        }
        public void run()
        {
            addEntry(time,sum);
        }
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
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
    public static int getDayNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    public static int getMonthNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }
    public static int getYearNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    public static int getHourNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    private void formData(final String label, final int datasetIndex, final int year, final int month, final int  day, final LinkedHashMap<Long,Double> data)
    {
        //0 for any parameter means all
        //the graph has the following filter combinations
        /*  YEAR  |   MONTH   |   DAY     ||  GRAPH FILTER
        ----------------------------------------------------
             all   |   all     |   all     || days
        ----------------------------------------------------
            2020   |   all     |   all     || days->2020
        ----------------------------------------------------
            2020   |   jan     |   all     || days->2020->jan
        ----------------------------------------------------
             all   |   jan     |   all     || days->jan
        ----------------------------------------------------
            2020   |   jan     |   mon     || hours->2020->jan->mon
        ----------------------------------------------------
             all   |   jan     |   mon     || hours->jan->mon
        ----------------------------------------------------
             all   |   all     |   mon     || hours->mon
        ----------------------------------------------------
            2020   |   all     |   mon     || hours->2020->mon
        ----------------------------------------------------
            all   |   all     |   hide     || months
        ----------------------------------------------------
            all   |   jan     |   hide     || months->jan
        ----------------------------------------------------
            2020   |   jan     |   hide     || months->jan->2020
        ----------------------------------------------------
            all   |   hide     |   hide     || years
        ----------------------------------------------------
         */
        if (thread != null)
            thread.interrupt();
        chart.clearValues();
        if(day==0)
        {
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
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
            });
            thread = new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    Double[] daysSumArray = {0.0,0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0 };
                    SortedSet<Long> keys = new TreeSet<>(data.keySet());
                    for (Long key : keys)
                    {
                        Double value = data.get(key);
                        Date date = new Date(key);
                        int day_to_show = getDayNumber(date); //days start at 1 which is SUN
                        int year_to_show = getYearNumber(date);
                        int month_to_show = getMonthNumber(date)+1; //months start ta 0 which is JAN


                                     /*  YEAR  |   MONTH   |   DAY     ||  GRAPH FILTER
                                    ----------------------------------------------------
                                         all   |   all     |   all     || days
                                    ----------------------------------------------------
                                        2020   |   all     |   all     || days->2020
                                    ----------------------------------------------------
                                        2020   |   jan     |   all     || days->2020->jan
                                    ----------------------------------------------------
                                         all   |   jan     |   all     || days->jan*/
                        if(month==0 && year==0)
                            daysSumArray[day_to_show-1] += value;
                        else if(month==0 && year>0)
                        {
                            //days->2020
                            if(year_to_show==year)
                                daysSumArray[day_to_show-1] += value;
                        }
                        else if(month>0 && year>0)
                        {
                            //days->2020->jan
                            if(year_to_show==year && month_to_show == month)
                                daysSumArray[day_to_show-1] += value;
                        }
                        else if(month>0 && year==0)
                        {
                            //days->jan
                            if(month_to_show == month)
                                daysSumArray[day_to_show-1] += value;
                        }


                    }
                    for(int c=0; c<=6; c++)
                        getActivity().runOnUiThread(new OneShotTask(label, datasetIndex,(long)c+1,daysSumArray[c]));

                }
            });

            thread.start();
        }
        else if( day >0 && day <8)
        {
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value)
                {
                    String hours[] =getResources().getStringArray(R.array.hours_24);
                    int hour = (int) value % hours.length;
                    if(hour<0)
                        return "";
                    else
                        return hours[hour];
                }
            });
            thread = new Thread(new Runnable()
            {

                @Override
                public void run()
                {

                    Double[] hoursSumArray = {0.0,0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0,0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
                    SortedSet<Long> keys = new TreeSet<>(data.keySet());
                    for (Long key : keys)
                    {
                        Double value = data.get(key);
                        Date date = new Date(key);
                        int day_to_show = getDayNumber(date); //days start at 1 which is SUN
                        int year_to_show = getYearNumber(date);
                        int month_to_show = getMonthNumber(date)+1; //months start ta 0 which is JAN
                        int hour_of_day = getHourNumber(date);


                                     /*  YEAR  |   MONTH   |   DAY     ||  GRAPH FILTER
                                    ----------------------------------------------------
                                        2020   |   jan     |   mon     || hours->2020->jan->mon
                                    ----------------------------------------------------
                                         all   |   jan     |   mon     || hours->jan->mon
                                    ----------------------------------------------------
                                         all   |   all     |   mon     || hours->mon
                                    ----------------------------------------------------
                                        2020   |   all     |   mon     || hours->2020->mon
                                    ----------------------------------------------------
                                     */
                        if(month>0 && year>0 && day_to_show == day)
                        {
                            if(year_to_show==year && month_to_show == month)
                                hoursSumArray[hour_of_day]+=value;
                        }
                        else if(month>0 && year==0 && day_to_show == day)
                        {
                            if(month_to_show == month)
                                hoursSumArray[hour_of_day]+=value;
                        }

                        else if(month==0 && year==0 && day_to_show == day)
                            hoursSumArray[hour_of_day]+=value;
                        else if(month==0 && year>0 && day_to_show == day)
                        {
                            //days->2020
                            if(year_to_show==year)
                                hoursSumArray[hour_of_day]+=value;
                        }

                    }
                    for(int c=0; c<=23; c++)
                        getActivity().runOnUiThread(new OneShotTask(label, datasetIndex,(long)c,hoursSumArray[c]));


                }
            });

            thread.start();


        }
        else if( day == 8 && month !=13)
        {
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value)
                {
                    String months[] =getResources().getStringArray(R.array.months);
                    int month = (int) value % months.length;
                    if(month<0)
                        return "";
                    else
                        return months[month];
                }
            });
            thread = new Thread(new Runnable() {

                @Override
                public void run()
                {
                    Double monthsSumArray[] = {0.0,0.0 ,0.0 ,0.0 ,0.0 ,0.0 ,0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
                    SortedSet<Long> keys = new TreeSet<>(data.keySet());
                    for (Long key : keys)
                    {
                        Double value = data.get(key);
                        Date date = new Date(key);
                        int day_to_show = getDayNumber(date); //days start at 1 which is SUN
                        int year_to_show = getYearNumber(date);
                        int month_to_show = getMonthNumber(date)+1; //months start ta 0 which is JAN

                          /*  YEAR  |   MONTH   |   DAY     ||  GRAPH FILTER
                    ----------------------------------------------------
                        all   |   all     |   hide     || months
                    ----------------------------------------------------
                        all   |   jan     |   hide     || months->jan
                    ----------------------------------------------------
                        2020   |   jan     |   hide     || months->jan->2020
                    ----------------------------------------------------
                     */
                        if(month==0 && year==0)
                            monthsSumArray[month_to_show-1] += value;
                        else if(month>0 && year==0 && month==month_to_show)
                            monthsSumArray[month_to_show-1] += value;
                        else if(month>0 && year>0 && month==month_to_show)
                            monthsSumArray[month_to_show-1] += value;


                    }
                    for(int c=0; c<=11; c++)
                        getActivity().runOnUiThread(new OneShotTask(label, datasetIndex,(long)c+1,monthsSumArray[c]));

                }
            });
            thread.start();
        }
        else if(day==8 && month==13)
        {
             /*  YEAR  |   MONTH   |   DAY     ||  GRAPH FILTER
        ----------------------------------------------------
            all   |   hide     |   hide     || years
        ----------------------------------------------------
         */
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value)
                {
                    return String.valueOf((int)value);
                }
            });
            thread = new Thread(new Runnable() {

                @Override
                public void run()
                {
                    //REMEBER TO UPDATE STRING ARRAY FOR YEARS IN arrays.xml
                    Double yearsSumArray[] = {0.0,0.0  };
                    LinkedHashMap<Integer,Double > data_years = new LinkedHashMap<>();
                    SortedSet<Long> keys = new TreeSet<>(data.keySet());
                    for (Long key : keys)
                    {
                        Double value = data_years.get(key);
                        Date date = new Date(key);
                        int year = getYearNumber(date);
                        if(data_years.get(year) == null)
                            data_years.put(year,value);
                        else
                            data_years.put(year,data_years.get(year)+value);

                    }
                    Iterator iterator = data_years.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        LinkedHashMap.Entry<Integer, Double>set = (LinkedHashMap.Entry<Integer, Double>) iterator.next();
                        getActivity().runOnUiThread(new OneShotTask(label, datasetIndex,(long)set.getKey(),set.getValue()));
                    }

                }
            });
            thread.start();
        }

        /*else if(monthly)
        {

        }
        else if(yearly)
        {

        }*/
    }
    private class Data4Task extends AsyncTask<Void, String, Boolean>
    {
        private String url_get_data=base_url+"get_data_4.php";
        private String TAG_SUCCESS="success";
        private String TAG_MESSAGE="message";
        private JSONParser jsonParser;

        public Data4Task()
        {
            jsonParser = new JSONParser();
            milliseconds_data.clear();
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //getting columns list
            List<NameValuePair> info=new ArrayList<NameValuePair>(); //info for staff count
            info.add(new BasicNameValuePair("seller_email", LoginActivity.getServerAccount().getEmail()));
            // making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_get_data,"POST",info);
            //Log.d("data 1",""+jsonObject.toString());
            try
            {
                JSONArray dataArrayList=null;
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    dataArrayList = jsonObject.getJSONArray("data");
                    for (int count = 0; count < dataArrayList.length(); count += 1)
                    {
                        JSONObject jsonObjectdata = dataArrayList.getJSONObject(count);
                        String user_email = jsonObjectdata.getString("user_email");
                        String date_added = jsonObjectdata.getString("date_added");
                        //if the specific millisecond data contains info just add the sum onto it
                        if(milliseconds_data.get(get_milliseconds_from_server_string(date_added))==null)
                            milliseconds_data.put(get_milliseconds_from_server_string(date_added),1.0);
                        else
                            milliseconds_data.put(get_milliseconds_from_server_string(date_added), milliseconds_data.get(get_milliseconds_from_server_string(date_added)) +1);

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
                formData("Traffic",0,0, 0, 0,milliseconds_data);

            }
            else
            {

            }
        }
    }
}