package com.spikingacacia.leta.ui.database;

public class SGroups
{
    private int id;
    private int category;
    private String group;
    private String description;
    private String dateadded;
    private String datechanged;

    public SGroups(int id, int category, String group,String description, String dateadded, String datechanged) {
        this.id = id;
        this.category = category;
        this.group=group;
        this.description = description;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
    }

    public SGroups(){}

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

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
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
