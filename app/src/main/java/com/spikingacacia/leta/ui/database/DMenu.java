package com.spikingacacia.leta.ui.database;

public class DMenu
{
    private int id;
    private int categoryId;
    private int groupId;
    private String item;
    private String description;
    private double sellingPrice;
    private String imageType;
    private String dateAdded;
    private String datechanged;

    public DMenu(int id, int categoryId, int groupId, String item, String description, double sellingPrice, String imageType, String dateAdded, String datechanged)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.groupId = groupId;
        this.item = item;
        this.description = description;
        this.sellingPrice = sellingPrice;
        this.imageType = imageType;
        this.dateAdded = dateAdded;
        this.datechanged = datechanged;
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

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getSellingPrice()
    {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice)
    {
        this.sellingPrice = sellingPrice;
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

    public String getDatechanged()
    {
        return datechanged;
    }

    public void setDatechanged(String datechanged)
    {
        this.datechanged = datechanged;
    }







}
