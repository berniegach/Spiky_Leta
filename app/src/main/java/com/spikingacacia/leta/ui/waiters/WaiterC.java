package com.spikingacacia.leta.ui.waiters;

import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.WaitersD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class WaiterC
{
    public final List<WaiterItem> ITEMS = new ArrayList<WaiterItem>();
    public final Map<String, WaiterItem> ITEM_MAP = new HashMap<String, WaiterItem>();

    public WaiterC()
    {
        int pos=1;
        Iterator iterator= LoginA.waitersList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, WaitersD>set=(LinkedHashMap.Entry<Integer, WaitersD>) iterator.next();
            int id=set.getKey();
            WaitersD waiters=set.getValue();
            String email= waiters.getEmail();
            String names= waiters.getNames();
            double ratings = waiters.getRating();
            addItem(createDummyItem(pos,id,email,names,ratings));
            pos+=1;
        }
    }

    private void addItem(WaiterItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.pos, item);
    }

    public WaiterItem createDummyItem(int position, int id, String email, String names, double rating)
    {
        return new WaiterItem(String.valueOf(position),id,  email, names, rating);
    }

    public class WaiterItem
    {
        public final String pos;
        public int id;
        public String email;
        public String names;
        public double rating;

        public WaiterItem(String pos, int id, String email, String names, double rating)
        {
            this.pos = pos;
            this.id= id;
            this.email=email;
            this.names=names;
            this.rating = rating;
        }

        @Override
        public String toString()
        {
            return names;
        }
    }
}
