/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.database;

import java.io.Serializable;

public class ServerAccount implements Serializable
{
    private int persona=0; // 0= seller, 1= waiter
    private int id;
    private String email;
    private String password;
    private String username;
    private int onlineVisibility;
    private int deliver;
    private String diningOptions;
    private String country;
    private String location;
    private int orderRadius;
    private int orderFormat;
    private int numberOfTables;
    private String imageType;
    private String dateadded;
    private String datechanged;
    private String dateToday;
    //these are waiters fields from user account
    private int waiter_id;
    private String waiter_email;
    private String waiter_names;
    private String waiterImageType;
    private String mCode;
    private double commision;
    private String mpesaMobile;
    private String mFirebaseTokenId;

    public ServerAccount(){}

    public ServerAccount(int persona, int id, String email, String password, String username, String country, int online, int deliver, String diningOptions, String location, int orderRadius, int orderFormat,
                         int numberOfTables, String imageType, String dateadded, String datechanged, String dateToday,
                         int waiter_id, String waiter_email, String waiter_names, String waiterImageType, String mCode, double commision, String mpesaMobile, String firebaseTokenId)
    {
        this.persona=persona;
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.onlineVisibility=online;
        this.deliver=deliver;
        this.diningOptions = diningOptions;
        this.country = country;
        this.location = location;
        this.orderRadius=orderRadius;
        this.orderFormat=orderFormat;
        this.numberOfTables=numberOfTables;
        this.imageType = imageType;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
        this.dateToday = dateToday;
        this.waiter_id=waiter_id;
        this.waiter_email = waiter_email;
        this.waiter_names=waiter_names;
        this.waiterImageType = waiterImageType;
        this.mCode = mCode;
        this.commision = commision;
        this.mpesaMobile = mpesaMobile;
        this.mFirebaseTokenId = firebaseTokenId;
    }
    public int getPersona()
    {
        return persona;
    }

    public void setPersona(int persona)
    {
        this.persona = persona;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDeliver() {
        return deliver;
    }

    public void setDeliver(int deliver) {
        this.deliver = deliver;
    }
    public String getDiningOptions()
    {
        return diningOptions;
    }

    public void setDiningOptions(String diningOptions)
    {
        this.diningOptions = diningOptions;
    }
    public int getOnlineVisibility() {
        return onlineVisibility;
    }

    public void setOnlineVisibility(int onlineVisibility) {
        this.onlineVisibility = onlineVisibility;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOrderRadius()
    {
        return orderRadius;
    }

    public void setOrderRadius(int orderRadius)
    {
        this.orderRadius = orderRadius;
    }

    public int getOrderFormat()
    {
        return orderFormat;
    }

    public void setOrderFormat(int orderFormat)
    {
        this.orderFormat = orderFormat;
    }

    public int getNumberOfTables()
    {
        return numberOfTables;
    }

    public void setNumberOfTables(int numberOfTables)
    {
        this.numberOfTables = numberOfTables;
    }
    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
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

    public String getDateToday() {
        return dateToday;
    }

    public void setDateToday(String dateToday) {
        this.dateToday = dateToday;
    }

    public int getWaiter_id()
    {
        return waiter_id;
    }

    public void setWaiter_id(int waiter_id)
    {
        this.waiter_id = waiter_id;
    }
    public String getWaiter_email()
    {
        return waiter_email;
    }

    public void setWaiter_email(String waiter_email)
    {
        this.waiter_email = waiter_email;
    }

    public String getWaiter_names()
    {
        return waiter_names;
    }

    public void setWaiter_names(String waiter_names)
    {
        this.waiter_names = waiter_names;
    }
    public String getWaiterImageType()
    {
        return waiterImageType;
    }

    public void setWaiterImageType(String waiterImageType)
    {
        this.waiterImageType = waiterImageType;
    }
    public String getmCode()
    {
        return mCode;
    }

    public void setmCode(String mCode)
    {
        this.mCode = mCode;
    }
    public double getCommision()
    {
        return commision;
    }

    public void setCommision(double commision)
    {
        this.commision = commision;
    }

    public String getMpesaMobile()
    {
        return mpesaMobile;
    }

    public void setMpesaMobile(String mpesaMobile)
    {
        this.mpesaMobile = mpesaMobile;
    }
    public String getmFirebaseTokenId()
    {
        return mFirebaseTokenId;
    }

    public void setmFirebaseTokenId(String mFirebaseTokenId)
    {
        this.mFirebaseTokenId = mFirebaseTokenId;
    }
}
