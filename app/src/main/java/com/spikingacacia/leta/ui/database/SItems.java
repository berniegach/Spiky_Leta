package com.spikingacacia.leta.ui.database;

public class SItems
{
    private int id;
    private int category;
    private int group;
    private String item;
    private String description;
    private double sellingPrice;
    private int available;
    private String dateadded;
    private String datechanged;

    public SItems(int id, int category, int group, String item, String description, double sellingPrice, int available, String dateadded, String datechanged) {
        this.id = id;
        this.category = category;
        this.group=group;
        this.item=item;
        this.description = description;
        this.sellingPrice=sellingPrice;
        this.available=available;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
    }

    public SItems(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getGroup()
    {
        return group;
    }

    public void setGroup(int group)
    {
        this.group = group;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public double getSellingPrice()
    {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice)
    {
        this.sellingPrice = sellingPrice;
    }

    public int getAvailable()
    {
        return available;
    }

    public void setAvailable(int available)
    {
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateadded() {
        return dateadded;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public String getDatechanged() {
        return datechanged;
    }

    public void setDatechanged(String datechanged) {
        this.datechanged = datechanged;
    }

}
