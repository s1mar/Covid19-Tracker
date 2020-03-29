package com.s1mar.covid19tracker.users.auxiliary;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.s1mar.covid19tracker.R;

import com.s1mar.covid19tracker.data.models.MEmployee;
import com.s1mar.covid19tracker.databinding.ItemCardEmpStatusBinding;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter_Employee extends RecyclerView.Adapter<RecyclerAdapter_Employee.ViewHolder> {

    private List<MEmployee> mDataSet = new ArrayList<MEmployee>(0);


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_emp_status,parent,false);
        return new ViewHolder(itemView);
    }

    public void updateSet(List<MEmployee> dataSet,boolean isReset){
        if(dataSet!=null && !dataSet.isEmpty()){
            if(isReset){
                mDataSet.clear();
            }
            mDataSet.addAll(dataSet);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MEmployee employee = mDataSet.get(position);
        holder.binder.txtName.setText(employee.getName());
        //bind health status
        bindHealthStatus(holder,employee);
        //bind family health status
        bindFamilyHealthStatus(holder,employee);
    }

    private void bindHealthStatus(ViewHolder holder,MEmployee employee){
       Integer healthStatus = employee.getHealthStatus();
       if(healthStatus==null){healthStatus = 0;}
       String health = "Health Status: ";
       String healthSub = "Healthy";
       int color = Color.GREEN;
       if(healthStatus==1){
        healthSub = "Suspected";
        color = Color.argb(100,255,165,0);
       }else if(healthStatus==2){
           healthSub = "Infected";
           color = Color.RED;
       }
       String healthFinal = health+healthSub;
       Spannable span = new SpannableString(healthFinal);
       span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
       holder.binder.selfStatus.setText(span);
    }

    private void bindFamilyHealthStatus(ViewHolder holder,MEmployee employee){
        Integer healthStatus  = employee.getFamilyHealthStatus();
        if(healthStatus==null){healthStatus = 0;}
        String health = "Family Health Status: ";
        String healthSub = "Healthy";
        int color = Color.GREEN;
        if(healthStatus==1){
            healthSub = "Suspected";
            color = Color.argb(100,255,165,0);
        }else if(healthStatus==2){
            healthSub = "Infected";
            color = Color.RED;
        }
        String healthFinal = health+healthSub;
        Spannable span = new SpannableString(healthFinal);
        span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.binder.familyStatus.setText(span);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

   static class ViewHolder extends RecyclerView.ViewHolder{
        ItemCardEmpStatusBinding binder;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            binder = ItemCardEmpStatusBinding.bind(itemView);
        }

    }
}
