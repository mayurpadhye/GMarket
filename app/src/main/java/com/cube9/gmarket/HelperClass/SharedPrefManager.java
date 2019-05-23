package com.cube9.gmarket.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class SharedPrefManager {
    //the constants
    private static final String SHARED_PREF_NAME = "gmarket_session";

    private static final String EMAIL = "email";
    private static final String PASSWORD = "email";
    private static final String MOBILE = "mobile";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String CUSTOMER_ID = "customer_id";

    private static final String ISLOGIN = "isLogin";
    private static final String IS_PASSWORD_REMEMBERED = "password_remember";
    private static final String LANGUAGE = "language";
    private static final String LANGUAGE_CODE = "language_code";
    private static final String CART_COUNTER = "cart_counter";
    private static final String IS_ICON_CREATED = "icon_created";
    private static final String IS_FB_LOGIN = "fb_loginn";
    private static final boolean ICON_CREATED = false;
    private static final boolean FB_LOGIN = false;

    public static SharedPrefManager mInstance;
    public static Context mCtx;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor ;
    public SharedPrefManager(Context context) {
        mCtx = context;
        sharedPref= PreferenceManager.getDefaultSharedPreferences(mCtx);
        editor = sharedPref.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);

        }
        return mInstance;
    }
    public void setMobileNumber(String mobile_number)
    {
        editor.putString(MOBILE, mobile_number);
        editor.commit();
    }

    public String getMobileNumber()
    {
        return sharedPref.getString(MOBILE,"");
    }




    public void setUserFirstName(String firstName)
    {
        editor.putString(FIRSTNAME, firstName);
        editor.commit();
    }

    public String getUserFirstName()
    {
        return sharedPref.getString(FIRSTNAME,"");
    }

    public void setUserLastName(String lastName)
    {
        editor.putString(LASTNAME, lastName);
        editor.commit();
    }

    public String getUserLastName()
    {
        return sharedPref.getString(LASTNAME,"");
    }


    public void SetCustomerId( String customer_id) {
        editor.putString(CUSTOMER_ID, customer_id);
        editor.commit();
    }//SetCustomerId

    public String GetCustomerId()
    {
        return  sharedPref.getString(CUSTOMER_ID,"");
    }//GetCustomerId

    public void SetisLogin(boolean value)
    {
        editor.putBoolean(ISLOGIN,value).commit();
    }
    public boolean IsLogin()
    {
        return sharedPref.getBoolean(ISLOGIN,false);
    }



    public void setEmail(String Email)
    {
        editor.putString(EMAIL, Email);
        editor.commit();
    }
    public String getEmail()
    {
        return sharedPref.getString(EMAIL,"");
    }

    public void setPassword(String password)
    {
        editor.putString(PASSWORD,password).commit();
    }

    public String getPassword()
    {
        return sharedPref.getString(PASSWORD,"");
    }

    public void SetPassRemeber(boolean value)
    {
        editor.putBoolean(IS_PASSWORD_REMEMBERED,value).commit();
    }
    public boolean IsPassRemeber()
    {
        return sharedPref.getBoolean(IS_PASSWORD_REMEMBERED,false);
    }


    public String getLanguage()
    {
        return sharedPref.getString(LANGUAGE,"");
    }

    public void setLanguage(String value)
    {
        editor.putString(LANGUAGE,value).commit();
    }

    public String getAppLanguageCode()
    {
        return sharedPref.getString(LANGUAGE_CODE,"");
    }

    public void setAppLanguageCode(String value)
    {
        editor.putString(LANGUAGE_CODE,value).commit();
    }

    public String getCartCount()
    {
        return sharedPref.getString(CART_COUNTER,"");
    }

    public void setCartCount(String value)
    {
        editor.putString(CART_COUNTER,value).commit();
    }

    public boolean IsIconCreated()
    {
        return sharedPref.getBoolean(IS_ICON_CREATED,ICON_CREATED);
    }

    public void setIsIconCreated(boolean value)
    {
        editor.putBoolean(IS_ICON_CREATED,value).commit();
    }


    public boolean IsFbLogin()
    {
        return sharedPref.getBoolean(IS_FB_LOGIN,FB_LOGIN);
    }

    public void setIsFbLogin(boolean value)
    {
        editor.putBoolean(IS_FB_LOGIN,value).commit();
    }

    public void Logout()
    {

            Map<String,?> prefs = sharedPref.getAll();
            for(Map.Entry<String,?> prefToReset : prefs.entrySet()){

                if(prefToReset.getKey().equals(IS_PASSWORD_REMEMBERED) || prefToReset.getKey().equals(MOBILE) || prefToReset.getKey().equals(LANGUAGE) || prefToReset.getKey().equals(LANGUAGE_CODE))
                {
                    if (IsPassRemeber())
                    {

                    }
                    else if (!IsPassRemeber())
                    {
                        if (prefToReset.getKey().equals(IS_PASSWORD_REMEMBERED))
                        editor.remove(prefToReset.getKey()).commit();
                    }


                }
                else
                {
                    editor.remove(prefToReset.getKey()).commit();
                }

            }


    }

}
