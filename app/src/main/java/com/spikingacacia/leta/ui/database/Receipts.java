/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.database;

public class Receipts
{
    private int id;
    private String amount;
    private String mobile;
    private String receipt;
    private String transaction_date;
    private String names;
    private String date_added;

    public Receipts(int id, String amount, String mobile, String receipt, String transaction_date, String names, String date_added)
    {
        this.id = id;
        this.amount = amount;
        this.mobile = mobile;
        this.receipt = receipt;
        this.transaction_date = transaction_date;
        this.names = names;
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

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getReceipt()
    {
        return receipt;
    }

    public void setReceipt(String receipt)
    {
        this.receipt = receipt;
    }

    public String getTransaction_date()
    {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date)
    {
        this.transaction_date = transaction_date;
    }

    public String getNames()
    {
        return names;
    }

    public void setNames(String names)
    {
        this.names = names;
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
