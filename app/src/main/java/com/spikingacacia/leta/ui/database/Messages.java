/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui.database;

public class Messages
{
    private int id;
    private int persona;
    private int status;
    private String message;
    private String dateAdded;

    public Messages(int id, int persona, int status, String message, String dateAdded)
    {
        this.id = id;
        this.persona = persona;
        this.status = status;
        this.message = message;
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

    public int getPersona()
    {
        return persona;
    }

    public void setPersona(int persona)
    {
        this.persona = persona;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
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
