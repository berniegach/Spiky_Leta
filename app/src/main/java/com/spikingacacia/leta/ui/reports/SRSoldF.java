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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.spikingacacia.leta.R;
import com.spikingacacia.leta.ui.database.SItems;
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
public class SRSoldF extends Fragment
{
    private PieChart chart;
    private String TAG="SRSoldRateF";
    private List<String> unique_dates_list;
    private List<Integer> items_count_list;
    private double maxPrice=0.0;
    private Date start=null;
    private Date end=null;
    private Typeface font;


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
    private void formData()

    {
        items_count_list=new ArrayList<>();
        for(int c=0; c< sItemsList.size(); c++)
            items_count_list.add(0);
        int count=0;
        Iterator iterator= sItemsList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SItems> set = (LinkedHashMap.Entry<Integer, SItems>) iterator.next();
            //int id = set.getKey();
            SItems sItems=set.getValue();
            int id=sItems.getId();
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
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5,10,5,5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(false);
        //chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        // chart.setHoleRadius(95f);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setEntryLabelColor(Color.WHITE);
        // chart.setEntryLabelTypeface(getResources().getFont(R.font.arima_madurai));

        Legend legend=chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(13);
        legend.setTextColor(Color.WHITE);
        legend.setTypeface(font);
        //entry label
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(font);
        chart.setEntryLabelTextSize(12);


        chart.invalidate();

    }
    private void setItemsPie()
    {
        List<PieEntry>entries=new ArrayList<>();
        //colors
        List<Integer>colors=new ArrayList<>();
        List<Integer>tempColors=new ArrayList<>();

        List<Integer>tempColorsRes=new ArrayList<>();
        if(sItemsList.size()==0)
        {
            entries.add(new PieEntry(1,"Empty"));
            colors=ColorTemplate.createColors(getResources(),new int[]{R.color.graph_1});
        }
        else
        {
            int index=0;
            Iterator iterator= sItemsList.entrySet().iterator();
            while (iterator.hasNext())
            {
                LinkedHashMap.Entry<Integer, SItems> set = (LinkedHashMap.Entry<Integer, SItems>) iterator.next();
                //int id=set.getKey();
                SItems sItems=set.getValue();
                int id=sItems.getId();
                String name=sItems.getItem();
                name=name.replace("_"," ");
                if (items_count_list.get(index)==0)
                {
                    index+=1;
                    continue;
                }
                entries.add(new PieEntry(items_count_list.get(index),name));
                tempColors.add(getColor(index%16));
                index+=1;
            }
            int[]tempTempColors=new int[tempColors.size()];
            for(int count=0; count<tempColors.size(); count+=1 )
                tempTempColors[count]=tempColors.get(count);
            colors=ColorTemplate.createColors(getResources(),tempTempColors);
        }

        PieDataSet set=new PieDataSet(entries,"Items Sold");
        set.setSliceSpace(0f);
        //colors
        set.setColors(colors);
        PieData data=new PieData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(font);
        data.setValueFormatter(new PercentFormatter());

        chart.setData(data);
        chart.highlightValues(null);
        chart.invalidate();
    }
    private int getColor(final int index)
    {
        switch(index)
        {
            case 0:
                return R.color.graph_1;
            case 1:
                return R.color.graph_2;
            case 2:
                return R.color.graph_3;
            case 3:
                return R.color.graph_4;
            case 4:
                return R.color.graph_5;
            case 5:
                return R.color.graph_6;
            case 6:
                return R.color.graph_7;
            case 7:
                return R.color.graph_8;
            case 8:
                return R.color.graph_9;
            case 9:
                return R.color.graph_10;
            case 10:
                return R.color.graph_11;
            case 11:
                return R.color.graph_12;
            case 12:
                return R.color.graph_13;
            case 13:
                return R.color.graph_14;
            case 14:
                return R.color.graph_2;
            case 15:
                return R.color.graph_3;
            default:
                return R.color.graph_4;
        }

    }






}
