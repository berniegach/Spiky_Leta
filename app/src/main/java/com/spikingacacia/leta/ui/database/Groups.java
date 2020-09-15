package com.spikingacacia.leta.ui.database;

import java.io.Serializable;

public class Groups implements Serializable
{
    private int id;
    private int categoryId;
    private String title;
    private String description;
    private String imageType;
    private String dateAdded;
    private String dateChanged;


    public Groups(int id, int categoryId, String title, String description, String imageType, String dateAdded, String dateChanged)
    {
        this.id = id;
        this.categoryId = categoryId;
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
    public int getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(int categoryId)
    {
        this.categoryId = categoryId;
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
