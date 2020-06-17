package com.spikingacacia.leta.ui.inventory;

import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.DMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SIItemC
{
    /**
     * An array of items.
     */
    public final List<InventoryItem> ITEMS = new ArrayList<InventoryItem>();
    public final Map<String, InventoryItem> ITEM_MAP = new HashMap<String, InventoryItem>();

    public SIItemC(int categoryId, int groupId)
    {
        int pos=1;
        Iterator iterator= LoginA.sItemsList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, DMenu>set=(LinkedHashMap.Entry<Integer, DMenu>) iterator.next();
            int id=set.getKey();
            DMenu DMenu =set.getValue();
            /*int category= DMenu.getCategory();
            int group= DMenu.getGroup();
            String item= DMenu.getItem();
            String description= DMenu.getDescription();
            double selling_price= DMenu.getSellingPrice();
            int available= DMenu.getAvailable();
            String date_added= DMenu.getDateadded();*/
            String date_changed= DMenu.getDatechanged();
           /* if(groupId==group && category==categoryId)
            {
                addItem(createItem(pos,id,category,group,item,description,selling_price,available,date_added,date_changed));
                pos+=1;
            }*/
        }
    }



    private  void addItem(InventoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    public InventoryItem createItem(int position, int id, int category, int group, String item, String description, double sellingPrice, int available, String dateadded, String datechanged) {
        return new InventoryItem(String.valueOf(position),  id, category, group, item, description, sellingPrice, available, dateadded,  datechanged);
    }


    public  class InventoryItem
    {
        public final String position;
        public final int id;
        public final int category;
        public final int group;
        public final String item;
        public final String description;
        public final double sellingPrice;
        public final int available;
        public final String dateadded;
        public final String datechanged;

        public InventoryItem(String position, int id, int category, int group, String item, String description, double sellingPrice, int available, String dateadded, String datechanged) {
            this.position=position;
            this.id = id;
            this.category = category;
            this.group=group;
            this.item=item;
            this.description = description;
            this.sellingPrice=sellingPrice;
            this.available=available;
            this.dateadded = dateadded;
            this.datechanged = datechanged;
        }

        @Override
        public String toString() {
            return item;
        }
    }
}
