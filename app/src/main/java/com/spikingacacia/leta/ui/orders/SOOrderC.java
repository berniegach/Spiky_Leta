package com.spikingacacia.leta.ui.orders;

import com.spikingacacia.leta.ui.database.SOrders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.spikingacacia.leta.ui.LoginA.sOrdersList;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SOOrderC
{
    public final List<OrderItem> ITEMS = new ArrayList<OrderItem>();
    public final Map<String, OrderItem> ITEM_MAP = new HashMap<String, OrderItem>();

    public SOOrderC(int whichOrder)
    {
        List<String> order_numbers=new ArrayList<>();
        Iterator iterator= sOrdersList.entrySet().iterator();
        while (iterator.hasNext())
        {
            LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator.next();
            SOrders bOrders=set.getValue();
            //int id=bOrders.getId();
            //int userId=bOrders.getUserId();
            //int itemId=bOrders.getItemId();
            int orderNumber=bOrders.getOrderNumber();
            int orderStatus=bOrders.getOrderStatus();
            //String orderName=bOrders.getOrderName();
            //double price=bOrders.getPrice();
            //String username=bOrders.getUsername();
            //int tableNumber=bOrders.getTableNumber();
            String dateAdded=bOrders.getDateAdded();
            //String dateChanged=bOrders.getDateChanged();
            String[] date_pieces=dateAdded.split(" ");
            if(orderStatus!=whichOrder)
                continue;
            String unique_name=date_pieces[0]+":"+orderNumber+":"+orderStatus;
            order_numbers.add(unique_name);
        }

        int position=0;
        Set<String> unique=new HashSet<>(order_numbers);
        List<String> order_counts=new ArrayList<>(unique);
        Iterator<String> iterator_2=order_counts.iterator();
        while(iterator_2.hasNext())
        {
            String unique_name=iterator_2.next();
            //items
            int id=0;
            int userId=0;
            int itemId=0;
            int orderNumber=0;
            int orderStatus=0;
            String orderName="";
            double price=0.0;
            String username="";
            int tableNumber=0;
            String dateAdded="";
            String dateChanged="";


            Iterator iterator_3= sOrdersList.entrySet().iterator();
            while (iterator_3.hasNext())
            {
                LinkedHashMap.Entry<Integer, SOrders>set=(LinkedHashMap.Entry<Integer, SOrders>) iterator_3.next();
                SOrders bOrders=set.getValue();
                orderStatus=bOrders.getOrderStatus();
                if(orderStatus!=whichOrder)
                    continue;
                orderNumber=bOrders.getOrderNumber();
                dateAdded=bOrders.getDateAdded();
                String[] date_pieces=dateAdded.split(" ");
                String unique_name_2=date_pieces[0]+":"+orderNumber+":"+orderStatus;
                if(unique_name_2.contentEquals(unique_name))
                {
                    id=bOrders.getId();
                    userId=bOrders.getUserId();
                    itemId=bOrders.getItemId();

                    orderName=bOrders.getOrderName();
                    price=bOrders.getPrice();
                    username=bOrders.getUsername();
                    tableNumber=bOrders.getTableNumber();
                    dateChanged=bOrders.getDateChanged();
                   break;
                }

            }
            addItem(CreateItem(position+1,id,userId,itemId,orderNumber,orderStatus,orderName,price,username,tableNumber,dateAdded,dateChanged));
            position+=1;

        }
    }

    private  void addItem(OrderItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    public  OrderItem CreateItem(int position, int id, int userId, int itemId, int orderNumber, int orderStatus, String orderName, double price, String username, int tableNumber, String dateAdded, String dateChanged)
    {
        return new OrderItem(String.valueOf(position), id, userId, itemId, orderNumber, orderStatus, orderName, price, username, tableNumber, dateAdded, dateChanged);
    }

    public class OrderItem
    {
        public String position;
        public int id;
        public int userId;
        public int itemId;
        public int orderNumber;
        public int orderStatus;
        public String orderName;
        public double price;
        public String username;
        public String waiter_names;
        public int tableNumber;
        public String dateAdded;
        public String dateChanged;

        public OrderItem(String position, int id, int userId, int itemId, int orderNumber, int orderStatus, String orderName, double price, String username, int tableNumber, String dateAdded, String dateChanged)
        {
            this.position = position;
            this.id = id;
            this.userId = userId;
            this.itemId = itemId;
            this.orderNumber = orderNumber;
            this.orderStatus = orderStatus;
            this.orderName = orderName;
            this.price = price;
            this.username=username;
            this.tableNumber=tableNumber;
            this.dateAdded = dateAdded;
            this.dateChanged = dateChanged;
        }

        @Override
        public String toString()
        {
            return username;
        }
    }
}
