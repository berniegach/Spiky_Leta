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
    private double price;
    private String username;
    private String waiter_names;
    private int tableNumber;
    private String dateAdded;
    private String dateChanged;

    public Orders(int id,int userId, String userEmail, int itemId, int orderNumber, int orderStatus, String orderName, double price, String username, String waiter_names, int tableNumber, String dateAdded, String dateChanged)
    {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.itemId = itemId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.price = price;
        this.username=username;
        this.waiter_names=waiter_names;
        this.tableNumber=tableNumber;
        this.dateAdded = dateAdded;
        this.dateChanged = dateChanged;
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



}
