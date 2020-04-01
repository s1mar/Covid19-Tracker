package com.s1mar.covid19tracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author simar
 * Simplifying the process of saving key-val pairs in the shared preferences
 */
@SuppressWarnings("ApplySharedPref")
public class PlayerPrefs {

    private final static String fileName = "prefs";
    private final static String TAG = PlayerPrefs.class.getSimpleName();


    private static SharedPreferences getAHandleToTheSharedPrefsObject(Context context){
        return context.getApplicationContext().getSharedPreferences(fileName,Context.MODE_PRIVATE);
    }

    public static void clear(Context context){
        try{
            getAHandleToTheSharedPrefsObject(context).edit().clear().apply();
        }catch (Exception ex){
            Log.e(TAG, "clear: ",ex);
        }
    }

    public static boolean setString(Context context,String key,String value){

        boolean opResult;

        try{
            opResult = getAHandleToTheSharedPrefsObject(context).edit().putString(key,value).commit();
        }catch (Exception ex){
            Log.e(TAG, "setString: ", ex);
            opResult = false;
        }

        return opResult;
    }

    public static String getString(Context context,String key){

        String toReturn = "";
        try{
            toReturn = getAHandleToTheSharedPrefsObject(context).getString(key,"");
        }catch (Exception ex){
            Log.e(TAG, "getString: ",ex);
        }

        return toReturn;
    }

    public static boolean setInteger(Context context,String key,int value){

        boolean opResult;

        try{
            opResult = getAHandleToTheSharedPrefsObject(context).edit().putInt(key,value).commit();
        }catch (Exception ex){
            Log.e(TAG, "setInteger: ", ex);
            opResult = false;
        }

        return opResult;
    }


    public static int getInteger(Context context,String key){

        int toReturn = -1;

        try{
            toReturn = getAHandleToTheSharedPrefsObject(context).getInt(key,-1);
        }catch (Exception ex){
            Log.e(TAG, "getString: ",ex);
        }

        return toReturn;
    }


    public static boolean setLong(Context context,String key,long value){

        boolean opResult;

        try{
            opResult = getAHandleToTheSharedPrefsObject(context).edit().putLong(key,value).commit();
        }catch (Exception ex){
            Log.e(TAG, "setLong: ", ex);
            opResult = false;
        }

        return opResult;
    }


    public static long getLong(Context context,String key){

        long toReturn = -1;

        try{
            toReturn = getAHandleToTheSharedPrefsObject(context).getLong(key,-1L);
        }catch (Exception ex){
            Log.e(TAG, "getLong: ",ex);
        }

        return toReturn;
    }


    public static boolean setBoolean(Context context,String key,boolean val){

        boolean opResult = false;
        try{
            opResult =  getAHandleToTheSharedPrefsObject(context).edit().putBoolean(key,val).commit();
        }catch (Exception ex){
            Log.e(TAG, "setBoolean: ",ex);
        }

        return opResult;
    }

    public static boolean getBoolean(Context context,String key){

        boolean valToReturn = false;
        try{
            valToReturn = getAHandleToTheSharedPrefsObject(context).getBoolean(key,false);
        }catch (Exception ex ){
            Log.e(TAG, "getBoolean: ", ex);
        }
        return valToReturn;
    }


}