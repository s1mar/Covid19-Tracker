package com.s1mar.covid19tracker.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.databinding.ActLoaderBinding;

public class LoadingAnimationHelper {

    private static final String TAG = LoadingAnimationHelper.class.getSimpleName();

    private static final int viewID = R.id.loader_view;


    public static void showMessage(Activity activity, @Nullable String message)
    {
        //create the view if necessary and then hook it with a message
        ActLoaderBinding binder = initiateSplinterSequence(activity);
        binder.loaderView.setVisibility(View.VISIBLE);
        binder.loaderText.setText(message);

    }

    public static void showMessage(Activity activity, @Nullable String message, Long millisecondsDelay)
    {
        //create the view if necessary and then hook it with a message
        ActLoaderBinding binder = initiateSplinterSequence(activity);
        binder.loaderView.setVisibility(View.VISIBLE);
        binder.loaderText.setText(message);
        dismissWithDelay(activity,millisecondsDelay);

    }

    private static ActLoaderBinding initiateSplinterSequence(Activity activity){
        View animatorView =   activity.getWindow().findViewById(viewID);
        if(animatorView==null){
            animatorView = LayoutInflater.from(activity).inflate(R.layout.act_loader,null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            activity.getWindow().addContentView(animatorView,layoutParams);
        }
        return DataBindingUtil.bind(animatorView);
    }

    public static void dismiss(Activity activity){

        ActLoaderBinding binder = initiateSplinterSequence(activity);
        binder.loaderView.setVisibility(View.GONE);

    }

    public static void dismissWithDelay(Activity activity, long millisecondsDelay){

        new Handler().postAtTime(()->{
            ActLoaderBinding binder = initiateSplinterSequence(activity);
            binder.loaderView.setVisibility(View.GONE);
        },millisecondsDelay);
    }


}