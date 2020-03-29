package com.s1mar.covid19tracker.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.s1mar.covid19tracker.R;

public class LoaderUtil {


    private static String TAG = LoaderUtil.class.getSimpleName();

    public static void loadAct(Context context, Class activityClass, @Nullable Parcelable parcel){

        try {
            Intent intent = new Intent(context, activityClass);
            if(parcel!=null){
                intent.putExtra(context.getString(R.string.parcel),parcel);
            }
            context.startActivity(intent);
        }
        catch (Exception ex){
            Log.e(TAG, "loadAct: error loading activity",ex);
        }
    }



    public static DialogFragment loadDialog(AppCompatActivity context , Class<? extends DialogFragment> dialogClass, @Nullable Bundle args){

        try {
            DialogFragment dialogFragment = dialogClass.newInstance();
            if(args!=null){
                dialogFragment.setArguments(args);
            }

            dialogFragment.show(context.getSupportFragmentManager(),dialogClass.getSimpleName());
            return dialogFragment;
        }
        catch (Exception ex){
            Log.e(TAG, "loadDialog: ",ex);
        }

        return null;
    }


    public static boolean loadFragment(AppCompatActivity context, Class<? extends  Fragment> fragmentClass,
                                       @Nullable Bundle args, boolean addToBackStack, int frag_container){

        try {
            FragmentManager fm = context.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment fragment = fragmentClass.newInstance();
            if(args!=null){
                fragment.setArguments(args);
            }

            ft.replace(frag_container,fragment);

            if(addToBackStack){
                ft.addToBackStack(fragmentClass.getSimpleName());
            }


            ft.commit();
            return true;


        }catch (Exception ex){
            Log.e(TAG, "loadFragment: ",ex);
            return false;
        }
    }


}