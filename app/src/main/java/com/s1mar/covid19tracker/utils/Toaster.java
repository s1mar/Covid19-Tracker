package com.s1mar.covid19tracker.utils;

import android.content.Context;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

public class Toaster implements LifecycleObserver {

    private Toast mToast;
    private WeakReference<Context> weakReferenceContext;

    public Toaster(AppCompatActivity context) {
        weakReferenceContext = new WeakReference<>((Context) context);
        context.getLifecycle().addObserver(this);
    }

    public void showToast(String msg, Integer timeDuration){

        dismissToast();
        int duration = timeDuration==null?Toast.LENGTH_SHORT:timeDuration;
        mToast = Toast.makeText(weakReferenceContext.get(),msg,duration);
        mToast.show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void dismissToast(){

        if(mToast!=null){
            mToast.cancel();
            mToast = null;
        }

    }

}