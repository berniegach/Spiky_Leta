/*
 * Created by Benard Gachanja on 10/13/20 5:23 PM
 * Copyright (c) 2020 . Spiking Acacia.  All rights reserved.
 * Last modified 10/10/20 7:07 PM
 */

package com.spikingacacia.leta.ui;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences
{
    private boolean dark_theme_enabled=false;
    private boolean verify_email =false;
    private boolean reset_password=false;
    private boolean remember_me=false;
    private SharedPreferences shared_preferences;
    private SharedPreferences.Editor preferences_editor;
    private String email_to_verify;
    private String email_to_reset_password;
    private String email_to_remember;
    private String password_to_remember;
    int persona;
    int order_format_to_show_count;
    //order types
    //-2 = unpaid, -1 = paid, 0 = deleted, 1 = pending, 2 = payment, 3 = in progress, 4 = delivery,  5 = finished
    int unpaid_count;
    int paid_count;
    int deleted_count;
    int pending_count;
    int payment_count;
    int in_progress_count;
    int delivery_count;
    int finished_count;
    //wallet
    private String mobileNumber;
    public Preferences(Context context)
    {
        shared_preferences=context.getSharedPreferences("loginPrefs",MODE_PRIVATE);
        preferences_editor=shared_preferences.edit();

        dark_theme_enabled=shared_preferences.getBoolean("dark_theme",false);
        remember_me= shared_preferences.getBoolean("rememberme",false);
        email_to_verify = shared_preferences.getString("email_verify","");
        verify_email = shared_preferences.getBoolean("verify_email",false);
        reset_password = shared_preferences.getBoolean("reset_password",false);
        email_to_remember = shared_preferences.getString("email","");
        password_to_remember = shared_preferences.getString("password","");
        order_format_to_show_count = shared_preferences.getInt("order_format_to_show_count",0);
        persona=shared_preferences.getInt("persona",2);

        unpaid_count = shared_preferences.getInt("unpaid_count",0);
        paid_count = shared_preferences.getInt("paid_count",0);
        deleted_count = shared_preferences.getInt("deleted_count",0);
        pending_count = shared_preferences.getInt("pending_count",0);
        payment_count = shared_preferences.getInt("payment_count",0);
        in_progress_count = shared_preferences.getInt("in_progress_count",0);
        delivery_count = shared_preferences.getInt("delivery_count",0);
        finished_count = shared_preferences.getInt("finished_count",0);
        mobileNumber = shared_preferences.getString("mobile_number",null);
    }
    public boolean isDark_theme_enabled()
    {
        return dark_theme_enabled;
    }

    public void setDark_theme_enabled(boolean dark_theme_enabled)
    {
        this.dark_theme_enabled = dark_theme_enabled;
        preferences_editor.putBoolean("dark_theme",dark_theme_enabled);
        preferences_editor.commit();
    }

    public boolean isVerify_email()
    {
        return verify_email;
    }

    public void setVerify_email(boolean verify_email)
    {
        this.verify_email = verify_email;
        preferences_editor.putBoolean("verify_email",verify_email);
        preferences_editor.commit();
    }

    public boolean isReset_password()
    {
        return reset_password;
    }

    public void setReset_password(boolean reset_password)
    {
        this.reset_password = reset_password;
        preferences_editor.putBoolean("reset_password",reset_password);
        preferences_editor.commit();
    }
    public boolean isRemember_me()
    {
        return remember_me;
    }

    public void setRemember_me(boolean remember_me)
    {
        this.remember_me = remember_me;
        preferences_editor.putBoolean("rememberme",remember_me);
        preferences_editor.commit();
    }
    public String getEmail_to_verify()
    {
        return email_to_verify;
    }

    public void setEmail_to_verify(String email_to_verify)
    {
        this.email_to_verify = email_to_verify;
        preferences_editor.putString("email_verify",email_to_verify);
        preferences_editor.commit();
    }
    public String getEmail_to_reset_password()
    {
        return email_to_reset_password;
    }

    public void setEmail_to_reset_password(String email_to_reset_password)
    {
        this.email_to_reset_password = email_to_reset_password;
        preferences_editor.putString("email_reset_password",email_to_reset_password);
        preferences_editor.commit();
    }
    public String getEmail_to_remember()
    {
        return email_to_remember;
    }

    public void setEmail_to_remember(String email_to_remember)
    {
        this.email_to_remember = email_to_remember;
        preferences_editor.putString("email",email_to_remember);
        preferences_editor.commit();
    }

    public String getPassword_to_remember()
    {
        return password_to_remember;
    }

    public void setPassword_to_remember(String password_to_remember)
    {
        this.password_to_remember = password_to_remember;
        preferences_editor.putString("password",password_to_remember);
        preferences_editor.commit();
    }
    /*
     * persona 1 is admin,
     * persona 2 is waiter
     */
    public int getPersona()
    {
        return persona;
    }
/*
* persona 1 is admin,
* persona 2 is waiter
 */
    public void setPersona(int persona)
    {
        this.persona = persona;
        preferences_editor.putInt("persona",persona);
        preferences_editor.commit();
    }
    public int getOrder_format_to_show_count()
    {
        return order_format_to_show_count;
    }

    public void setOrder_format_to_show_count(int order_format_to_show_count)
    {
        this.order_format_to_show_count = order_format_to_show_count;
        preferences_editor.putInt("order_format_to_show_count",order_format_to_show_count);
        preferences_editor.commit();
    }
    public int getUnpaid_count()
    {
        return unpaid_count;
    }

    public void setUnpaid_count(int unpaid_count)
    {
        this.unpaid_count = unpaid_count;
        preferences_editor.putInt("unpaid_count", unpaid_count);
        preferences_editor.commit();
    }

    public int getPaid_count()
    {
        return paid_count;
    }

    public void setPaid_count(int paid_count)
    {
        this.paid_count = paid_count;
        preferences_editor.putInt("paid_count", paid_count);
        preferences_editor.commit();
    }

    public int getDeleted_count()
    {
        return deleted_count;
    }

    public void setDeleted_count(int deleted_count)
    {
        this.deleted_count = deleted_count;
        preferences_editor.putInt("deleted_count", deleted_count);
        preferences_editor.commit();
    }

    public int getPending_count()
    {
        return pending_count;
    }

    public void setPending_count(int pending_count)
    {
        this.pending_count = pending_count;
        preferences_editor.putInt("pending_count", pending_count);
        preferences_editor.commit();

    }

    public int getPayment_count()
    {
        return payment_count;
    }

    public void setPayment_count(int payment_count)
    {
        this.payment_count = payment_count;
        preferences_editor.putInt("payment_count", payment_count);
        preferences_editor.commit();
    }

    public int getIn_progress_count()
    {
        return in_progress_count;
    }

    public void setIn_progress_count(int in_progress_count)
    {
        this.in_progress_count = in_progress_count;
        preferences_editor.putInt("in_progress_count", in_progress_count);
        preferences_editor.commit();
    }

    public int getDelivery_count()
    {
        return delivery_count;
    }

    public void setDelivery_count(int delivery_count)
    {
        this.delivery_count = delivery_count;
        preferences_editor.putInt("delivery_count", delivery_count);
        preferences_editor.commit();

    }

    public int getFinished_count()
    {
        return finished_count;
    }

    public void setFinished_count(int finished_count)
    {
        this.finished_count = finished_count;
        preferences_editor.putInt("finished_count", finished_count);
        preferences_editor.commit();
    }
    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
        preferences_editor.putString("mobile_number", mobileNumber);
        preferences_editor.commit();
    }



}
