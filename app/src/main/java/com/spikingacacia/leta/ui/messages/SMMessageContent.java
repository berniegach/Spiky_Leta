package com.spikingacacia.leta.ui.messages;

import com.spikingacacia.leta.ui.LoginA;
import com.spikingacacia.leta.ui.database.SMessages;

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
public class SMMessageContent
{

    /**
     * An array of items.
     */
    public final List<MessageItem> ITEMS = new ArrayList<MessageItem>();
    public final Map<String, MessageItem> ITEM_MAP = new HashMap<String, MessageItem>();

    public SMMessageContent()
    {
        int pos=1;
        Iterator iterator= LoginA.sMessagesList.entrySet().iterator();
        while(iterator.hasNext())
        {
            LinkedHashMap.Entry<String, SMessages>set=(LinkedHashMap.Entry<String, SMessages>) iterator.next();
            String name=set.getKey();
            SMessages sMessages=set.getValue();
            int id=sMessages.getId();
            int classes=sMessages.getClasses();
            String message=sMessages.getMessage();
            String dateAdded=sMessages.getDateAdded();
            addItem(createItem(pos,id,classes,message,dateAdded));
            pos+=1;
        }
    }

    private  void addItem(MessageItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    private  MessageItem createItem(int position,int id,int classes, String message,String dateAdded)
    {
        return new MessageItem(String.valueOf(position),id,classes,message,dateAdded);
    }

    public  class MessageItem
    {
        public final String position;
        public final int id;
        public final int classes;
        public final String message;
        public final String dateAdded;

        public MessageItem(String position, int id, int classes, String message, String dateAdded)
        {
            this.position = position;
            this.id = id;
            this.classes = classes;
            this.message = message;
            this.dateAdded = dateAdded;
        }

        @Override
        public String toString()
        {
            return message;
        }
    }
}
