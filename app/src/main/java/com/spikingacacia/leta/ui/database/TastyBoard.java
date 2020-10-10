/*
 * Created by Benard Gachanja on 10/10/20 7:06 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 8/19/20 6:35 PM
 */

package com.spikingacacia.leta.ui.database;

import java.io.Serializable;

public class TastyBoard implements Serializable
{
    private int id;
    private String title;
    private String description;
    private int linkedItemId;
    private String sizeAndPrice;
    private String discountPrice;
    private String expiryDate;
    private String imageType;
    private int views;
    private int likes;
    private int comments;
    private int orders;
    private String dateAdded;

    public TastyBoard(int id, String title, String description, int linkedItemId, String sizeAndPrice, String discountPrice, String expiryDate, String imageType, int views, int likes, int comments, int orders, String dateAdded)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.linkedItemId = linkedItemId;
        this.sizeAndPrice = sizeAndPrice;
        this.discountPrice = discountPrice;
        this.expiryDate = expiryDate;
        this.imageType = imageType;
        this.views = views;
        this.likes = likes;
        this.comments = comments;
        this.orders = orders;
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

    public int getLinkedItemId()
    {
        return linkedItemId;
    }

    public void setLinkedItemId(int linkedItemId)
    {
        this.linkedItemId = linkedItemId;
    }

    public String getSizeAndPrice()
    {
        return sizeAndPrice;
    }

    public void setSizeAndPrice(String sizeAndPrice)
    {
        this.sizeAndPrice = sizeAndPrice;
    }

    public String getDiscountPrice()
    {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice)
    {
        this.discountPrice = discountPrice;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }

    public int getViews()
    {
        return views;
    }

    public void setViews(int views)
    {
        this.views = views;
    }

    public int getLikes()
    {
        return likes;
    }

    public void setLikes(int likes)
    {
        this.likes = likes;
    }

    public int getComments()
    {
        return comments;
    }

    public void setComments(int comments)
    {
        this.comments = comments;
    }

    public int getOrders()
    {
        return orders;
    }

    public void setOrders(int orders)
    {
        this.orders = orders;
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
