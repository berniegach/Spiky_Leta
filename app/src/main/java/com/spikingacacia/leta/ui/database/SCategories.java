package com.spikingacacia.leta.ui.database;

public class SCategories
{
    private int id;
    private String category;
    private String description;
    private String dateadded;
    private String datechanged;

    public SCategories(int id, String category, String description, String dateadded, String datechanged) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
    }

    public SCategories(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
