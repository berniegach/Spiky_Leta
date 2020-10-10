/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 9/9/19 12:18 PM
 */

package com.spikingacacia.leta.ui.database;

public class SMessages
{
    private int id;
    private int classes;
    private String message;
    private String dateAdded;

    public SMessages()
    {
    }

    public SMessages(int id, int classes, String message, String dateAdded)
    {
        this.id = id;
        this.classes = classes;
        this.message = message;
        this.dateAdded = dateAdded;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getClasses()
    {
        return classes;
    }

    public void setClasses(int classes)
    {
        this.classes = classes;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getDateAdded()
    {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded)
    {
        this.dateAdded = dateAdded;
    }

}
