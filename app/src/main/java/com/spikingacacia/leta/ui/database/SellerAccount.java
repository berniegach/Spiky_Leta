package com.spikingacacia.leta.ui.database;

public class SellerAccount
{
    private int id;
    private String email;
    private String password;
    private String username;
    private int onlineVisibility;
    private int deliver;
    private String country;
    private String location;
    private int orderRadius;
    private int orderFormat;
    private int numberOfTables;
    private String dateadded;
    private String datechanged;
    private String dateToday;

    public SellerAccount(){}

    public SellerAccount(int id, String email, String password, String username, String country,int online, int deliver, String location, int orderRadius, int orderFormat, int numberOfTables, String dateadded, String datechanged, String dateToday) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.onlineVisibility=online;
        this.deliver=deliver;
        this.country = country;
        this.location = location;
        this.orderRadius=orderRadius;
        this.orderRadius=orderRadius;
        this.orderFormat=orderFormat;
        this.numberOfTables=numberOfTables;
        this.dateadded = dateadded;
        this.datechanged = datechanged;
        this.dateToday = dateToday;
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


}
