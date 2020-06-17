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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.Preferences;
import com.spikingacacia.leta.ui.database.DMenu;
import com.spikingacacia.leta.ui.database.SOrders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.spikingacacia.leta.ui.LoginA.sItemsList;
import static com.spikingacacia.leta.ui.LoginA.sOrdersList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SRSoldF extends Fragment implements OnChartValueSelectedListener
{
    private PieChart chart;
    private String TAG="SRSoldRateF";
    private List<String> unique_dates_list;
    private List<Integer> items_count_list;
    private double maxPrice=0.0;
    private Date start=null;
    private Date end=null;
    private Typeface font;
    private Preferences preferences;

    public static SRSoldF newInstance()
    {
        SRSoldF fragment = new SRSoldF();
        //Bundle bundle = new Bundle();
        //bundle.putInt(ARG_SECTION_NUMBER, index);
        //fragment.setArguments(bundle);
        return fragment;
    }
    public SRSoldF()
    {
        // Required empty public constructor
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
        View view= inflater.inflate(R.layout.f_srsold, container, false);

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
        setPieChart();
        setItemsPie();

        // redraw
        chart.invalidate();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.pie, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionToggleValues: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (chart.isDrawHoleEnabled())
                    chart.setDrawHoleEnabled(false);
                else
                    chart.setDrawHoleEnabled(true);
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
                item.setChecked(!item.isChecked());
                chart.invalidate();
                break;
        }
        return true;
    }

    private void formData()

    {
        items_count_list=new ArrayList<>();
        for(int c=0; c< sItemsList.size(); c++)
            items_count_list.add(0);
        int count=0;
        Iterator iterator= sItemsList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu> set = (LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            //int id = set.getKey();
            DMenu DMenu =set.getValue();
            int id= DMenu.getId();
            Iterator iterator_orders= sOrdersList.entrySet().iterator();
            while (iterator_orders.hasNext())
            {
                LinkedHashMap.Entry<Integer, SOrders>set_orders=(LinkedHashMap.Entry<Integer, SOrders>) iterator_orders.next();
                SOrders bOrders=set_orders.getValue();
                //int id=bOrders.getId();
                //int userId=bOrders.getUserId();
                int itemId=bOrders.getItemId();
                int orderNumber=bOrders.getOrderNumber();
                int orderStatus=bOrders.getOrderStatus();
                //String orderName=bOrders.getOrderName();
                //double price=bOrders.getPrice();
                //String username=bOrders.getUsername();
                //int tableNumber=bOrders.getTableNumber();
                //String dateAdded=bOrders.getDateAdded();
                String dateChanged=bOrders.getDateChanged();
                String[] date_pieces=dateChanged.split(":"); // the date is in the form "dd-MM-yyyy HH:mm"
                if(orderStatus!=5)
                    continue;
                if(id==itemId)
                    items_count_list.set(count,items_count_list.get(count)+1);
            }
            count+=1;
        }
        for(int c=0; c<items_count_list.size(); c++)
            Log.d("count:",":"+items_count_list.get(c));
    }
    private void setPieChart()
    {
        if(preferences.isDark_theme_enabled())
            chart.setBackgroundColor(Color.BLACK);
        else
            chart.setBackgroundColor(Color.WHITE);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        //chart.setCenterTextTypeface(tfLight);
       // chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        if(preferences.isDark_theme_enabled())
            chart.setEntryLabelColor(Color.WHITE);
        else
            chart.setEntryLabelColor(Color.BLACK);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

    }
    private void setItemsPie()
    {
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        List<PieEntry>entries=new ArrayList<>();
        if(sItemsList.size()==0)
            entries.add(new PieEntry(1,"Empty"));
        else
        {
            int index=0;
            Iterator iterator= sItemsList.entrySet().iterator();
            while (iterator.hasNext())
            {
                LinkedHashMap.Entry<Integer, DMenu> set = (LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
                //int id=set.getKey();
                DMenu DMenu =set.getValue();
                int id= DMenu.getId();
                String name= DMenu.getItem();
                name=name.replace("_"," ");
                if (items_count_list.get(index)==0)
                {
                    index+=1;
                    continue;
                }
                entries.add(new PieEntry(items_count_list.get(index),name));
                index+=1;
            }
        }


        PieDataSet dataSet = new PieDataSet(entries, "Items sold");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(0f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        //data.setValueTextColor(Color.BLUE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }






}
