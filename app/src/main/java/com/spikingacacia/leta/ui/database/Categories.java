/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 10:11 PM
 */

package com.spikingacacia.leta.ui.database;

import java.io.Serializable;

public class Categories  implements Serializable
{
    private int id;
    private int idIndex;
    private String title;
    private String description;
    private String imageType;
    private String dateAdded;
    private String dateChanged;


    public Categories(int id, int idIndex, String title, String description, String imageType, String dateAdded, String dateChanged)
    {
        this.id = id;
        this.idIndex = idIndex;
        this.title = title;
        this.description = description;
        this.imageType = imageType;
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
    public int getIdIndex()
    {
        return idIndex;
    }

    public void setIdIndex(int idIndex)
    {
        this.idIndex = idIndex;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
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
