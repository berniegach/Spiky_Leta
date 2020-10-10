/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 6/27/20 8:56 AM
 */

package com.spikingacacia.leta.ui.database;

public class Waiters
{
    private int id;
    private String email;
    private String names;
    private double rating;
    private String imageType;

    public Waiters(int id, String email, String names, double rating, String imageType)
    {
        this.id = id;
        this.email = email;
        this.names = names;
        this.rating = rating;
        this.imageType = imageType;
    }

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
    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }




}
