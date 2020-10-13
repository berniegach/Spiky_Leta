/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.database;

public class QrCodes
{
    private int id;
    private int tableNumber;
    private String urlCode;
    private String dateAdded;
    private String dateChanged;

    public QrCodes(int id, int tableNumber, String urlCode, String dateAdded, String dateChanged)
    {
        this.id = id;
        this.tableNumber = tableNumber;
        this.urlCode = urlCode;
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

    public int getTableNumber()
    {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber)
    {
        this.tableNumber = tableNumber;
    }

    public String getUrlCode()
    {
        return urlCode;
    }

    public void setUrlCode(String urlCode)
    {
        this.urlCode = urlCode;
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
