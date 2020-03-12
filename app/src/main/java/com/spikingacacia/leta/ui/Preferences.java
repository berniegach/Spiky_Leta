package com.spikingacacia.leta.ui;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences
{

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
        persona=shared_preferences.getInt("persona",0);
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
    public int getPersona()
    {
        return persona;
    }

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

}
