package com.spikingacacia.leta.ui.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SIGroupC
{
    /**
     * An array of items.
     */
    public final List<GroupItem> ITEMS = new ArrayList<GroupItem>();
    public final Map<String, GroupItem> ITEM_MAP = new HashMap<String, GroupItem>();

    public SIGroupC(int categoryId)
    {
        int pos=1;
        /*Iterator iterator= LoginActivity.sGroupsList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SGroups>set=(LinkedHashMap.Entry<Integer, SGroups>) iterator.next();
            int id=set.getKey();
            SGroups sGroups=set.getValue();
            int category=sGroups.getCategory();
            String group=sGroups.getGroup();
            String description=sGroups.getDescription();
            String date_added=sGroups.getDateadded();
            String date_changed=sGroups.getDatechanged();
            if(categoryId==category)
            {
                addItem(createItem(pos,id,category,group,description,date_added,date_changed));
                pos+=1;
            }
        }*/
    }



    private  void addItem(GroupItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    public GroupItem createItem(int position, int id, int category, String group, String description, String dateadded, String datechanged) {
        return new GroupItem(String.valueOf(position),  id, category, group, description, dateadded,  datechanged);
    }


    public  class GroupItem
    {
        public final String position;
        public final int id;
        public final int category;
        public final String group;
        public final String description;
        public final String dateadded;
        public final String datechanged;

        public GroupItem(String position, int id, int category, String group, String description, String dateadded, String datechanged) {
            this.position=position;
            this.id = id;
            this.category = category;
            this.group=group;
            this.description = description;
            this.dateadded = dateadded;
            this.datechanged = datechanged;
        }

        @Override
        public String toString() {
            return group;
        }
    }
}
