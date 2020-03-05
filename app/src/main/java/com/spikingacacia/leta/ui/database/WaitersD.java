package com.spikingacacia.leta.ui.database;

public class WaitersD
{
    private int id;
    private String email;
    private String names;
    private double rating;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getNames()
    {
        return names;
    }

    public void setNames(String names)
    {
        this.names = names;
    }

    public double getRating()
    {
        return rating;
    }

    public void setRating(double rating)
    {
        this.rating = rating;
    }
    public WaitersD(int id, String email, String names, double rating)
    {
        this.id = id;
        this.email = email;
        this.names = names;
        this.rating = rating;
    }



}
