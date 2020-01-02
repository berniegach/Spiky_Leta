package com.spikingacacia.leta.ui.inventory;

import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.SCategories;

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
public class SICategoryC
{
    /**
     * An array of items.
     */
    public final List<CategoryItem> ITEMS = new ArrayList<CategoryItem>();
    public final Map<String, CategoryItem> ITEM_MAP = new HashMap<String, CategoryItem>();

    public SICategoryC()
    {
        int pos=1;
        Iterator iterator= LoginA.sCategoriesList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SCategories>set=(LinkedHashMap.Entry<Integer, SCategories>) iterator.next();
            int id=set.getKey();
            SCategories sCategories=set.getValue();
            String category=sCategories.getCategory();
            String description=sCategories.getDescription();
            String date_added=sCategories.getDateadded();
            String date_changed=sCategories.getDatechanged();
            addItem(createItem(pos,id,category,description,date_added,date_changed));
            pos+=1;
        }
    }



    private  void addItem(CategoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    public  CategoryItem createItem(int position,int id, String category, String description, String dateadded, String datechanged) {
        return new CategoryItem(String.valueOf(position),  id, category, description, dateadded,  datechanged);
    }


    public  class CategoryItem {
        public final String position;
        public final int id;
        public final String category;
        public final String description;
        public final String dateadded;
        public final String datechanged;

        public CategoryItem(String position, int id, String category, String description, String dateadded, String datechanged) {
            this.position=position;
            this.id = id;
            this.category = category;
            this.description = description;
            this.dateadded = dateadded;
            this.datechanged = datechanged;
        }

        @Override
        public String toString() {
            return category;
        }
    }
}
