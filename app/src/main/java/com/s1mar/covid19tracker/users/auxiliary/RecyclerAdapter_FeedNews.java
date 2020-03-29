package com.s1mar.covid19tracker.users.auxiliary;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s1mar.covid19tracker.databinding.ItemCardEmpStatusBinding;

public class RecyclerAdapter_FeedNews {


    static class ViewHolder extends RecyclerView.ViewHolder{
        ItemCardEmpStatusBinding binder;
        public ViewHolder(@NonNull View itemView) {
            binder = ItemCardEmpStatusBinding.bind(itemView);
            super(itemView);
        }
    }
}
