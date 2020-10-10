/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 8/12/20 4:43 PM
 */

package com.spikingacacia.leta.ui.database;

public class Transactions
{
    private int id;
    private String order_number;
    private String order_date_added;
    private String balance;
    private String log_type;
    private String log;
    private String mobile_number;
    private String date_added;

    public Transactions(int id, String order_number, String order_date_added, String balance, String log_type, String log, String mobile_number, String date_added)
    {
        this.id = id;
        this.order_number = order_number;
        this.order_date_added = order_date_added;
        this.balance = balance;
        this.log_type = log_type;
        this.log = log;
        this.mobile_number = mobile_number;
        this.date_added = date_added;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getOrder_number()
    {
        return order_number;
    }

    public void setOrder_number(String order_number)
    {
        this.order_number = order_number;
    }

    public String getOrder_date_added()
    {
        return order_date_added;
    }

    public void setOrder_date_added(String order_date_added)
    {
        this.order_date_added = order_date_added;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public String getLog_type()
    {
        return log_type;
    }

    public void setLog_type(String log_type)
    {
        this.log_type = log_type;
    }

    public String getLog()
    {
        return log;
    }

    public void setLog(String log)
    {
        this.log = log;
    }

    public String getMobile_number()
    {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number)
    {
        this.mobile_number = mobile_number;
    }

    public String getDate_added()
    {
        return date_added;
    }

    public void setDate_added(String date_added)
    {
        this.date_added = date_added;
    }




}
