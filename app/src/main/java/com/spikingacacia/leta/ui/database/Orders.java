package com.spikingacacia.leta.ui.database;

public class Orders
{
    private int id;
    private int userId;
    private String userEmail;
    private int itemId;
    private int orderNumber;
    private int orderStatus;
    private String orderName;
    private String size;
    private double price;
    private String username;
    private String waiter_names;
    private int tableNumber;
    private int preOrder;
    private String collectTime;
    private int orderType;
    private String deliveryMobile;
    private String deliveryInstructions;
    private String deliveryLocation;
    private String dateAdded;
    private String dateChanged;
    private String dateAddedLocal;


    public Orders(int id,int userId, String userEmail, int itemId, int orderNumber, int orderStatus, String orderName, String size, double price, String username, String waiter_names, int tableNumber,
                  int preOrder, String collectTime, int order_type, String deliveryMobile, String deliveryInstructions, String deliveryLocation,
                  String dateAdded, String dateChanged, String dateAddedLocal)
    {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.itemId = itemId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.size = size;
        this.price = price;
        this.username=username;
        this.waiter_names=waiter_names;
        this.tableNumber=tableNumber;
        this.preOrder = preOrder;
        this.collectTime = collectTime;
        this.orderType = order_type;
        this.deliveryMobile = deliveryMobile;
        this.deliveryInstructions = deliveryInstructions;
        this.deliveryLocation = deliveryLocation;
        this.dateAdded = dateAdded;
        this.dateChanged = dateChanged;
        this.dateAddedLocal = dateAddedLocal;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public int getItemId()
    {
        return itemId;
    }

    public void setItemId(int itemId)
    {
        this.itemId = itemId;
    }

    public int getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public int getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderName()
    {
        return orderName;
    }

    public void setOrderName(String orderName)
    {
        this.orderName = orderName;
    }
    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getWaiter_names()
    {
        return waiter_names;
    }

    public void setWaiter_names(String waiter_names)
    {
        this.waiter_names = waiter_names;
    }

    public int getTableNumber()
    {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber)
    {
        this.tableNumber = tableNumber;
    }
    public int getPreOrder()
    {
        return preOrder;
    }

    public void setPreOrder(int preOrder)
    {
        this.preOrder = preOrder;
    }
    public String getCollectTime()
    {
        return collectTime;
    }

    public void setCollectTime(String collectTime)
    {
        this.collectTime = collectTime;
    }

    public int getOrderType()
    {
        return orderType;
    }

    public void setOrderType(int orderType)
    {
        this.orderType = orderType;
    }

    public String getDeliveryMobile()
    {
        return deliveryMobile;
    }

    public void setDeliveryMobile(String deliveryMobile)
    {
        this.deliveryMobile = deliveryMobile;
    }

    public String getDeliveryInstructions()
    {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions)
    {
        this.deliveryInstructions = deliveryInstructions;
    }

    public String getDeliveryLocation()
    {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation)
    {
        this.deliveryLocation = deliveryLocation;
    }

    public String getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded)
    {
        this.dateAdded = dateAdded;
    }

    public String getDateChanged()
    {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged)
    {
        this.dateChanged = dateChanged;
    }
    public String getDateAddedLocal()
    {
        return dateAddedLocal;
    }

    public void setDateAddedLocal(String dateAddedLocal)
    {
        this.dateAddedLocal = dateAddedLocal;
    }


}
