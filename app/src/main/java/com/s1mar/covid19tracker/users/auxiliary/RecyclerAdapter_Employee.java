package com.s1mar.covid19tracker.users.auxiliary;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.s1mar.covid19tracker.R;

import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.ItemCardEmpStatusBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerAdapter_Employee extends RecyclerView.Adapter<RecyclerAdapter_Employee.ViewHolder> {

    private List<MUser> mDataSet = new ArrayList<MUser>(0);
    private HashMap<MUser,Integer> clientHealthStatusMap = new HashMap<>(0);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_emp_status,parent,false);
        return new ViewHolder(itemView);
    }

    public void updateSet(List<MUser> dataSet, boolean isReset){
        if(dataSet!=null){
            if(isReset){
                mDataSet.clear();
            }
            mDataSet.addAll(dataSet);
            clientHealthStatusMap.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MUser employee = mDataSet.get(position);
        holder.binder.txtName.setText(employee.getName());
        //bind health status
        bindHealthStatus(holder,employee);
        //bind family health status
        bindFamilyHealthStatus(holder,employee);
        //bind client health status
        bindClientHealthStatus(holder,employee);
        //bind emp location
        bindLocationData(holder,employee);
    }


    private void bindLocationData(ViewHolder holder,MUser employee){
        if (employee.isOnSite()){
            holder.binder.workSite.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.onsite_48dp));
        }
        else {
            holder.binder.workSite.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.home_48dp));
        }
    }

    private void bindHealthStatus(ViewHolder holder, MUser employee){
       Integer healthStatus = employee.getHealthStatus();
       if(healthStatus==null){healthStatus = 0;}
       String health = "Health Status: ";
       String healthSub = "Normal";
       int color = Color.GREEN;
       if(healthStatus==1){
        healthSub = "Observation";
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

    private void bindClientHealthStatus(ViewHolder holder,MUser employee){
        if(employee.getClients()==null || employee.getClients().isEmpty()){
            holder.binder.clientStatus.setVisibility(View.GONE);
            return;
        }
        if(!clientHealthStatusMap.containsKey(employee)){

            int clientHealthAssumed = 0; //0->stands for healthy
            for(MUser client: employee.getClients()){
                clientHealthAssumed = client.getHealthStatus()>clientHealthAssumed?client.getHealthStatus():clientHealthAssumed;
            }
            clientHealthStatusMap.put(employee,clientHealthAssumed);
        }
        Integer healthStatus = clientHealthStatusMap.get(employee);
        if(healthStatus==null){healthStatus = 0;}
        String health = "Client Health Status: ";
        String healthSub = "Normal";
        int color = Color.GREEN;
        if(healthStatus==1){
            healthSub = "Observation";
            color = Color.argb(100,255,165,0);
        }else if(healthStatus==2){
            healthSub = "Infected";
            color = Color.RED;
        }
        String healthFinal = health+healthSub;
        Spannable span = new SpannableString(healthFinal);
        span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.binder.clientStatus.setText(span);

    }

    private void bindFamilyHealthStatus(ViewHolder holder, MUser employee){
        Integer healthStatus  = employee.getFamilyHealthStatus();
        if(healthStatus==null){healthStatus = 0;}
        String health = "Family Health Status: ";
        String healthSub = "Normal";
        int color = Color.GREEN;
        if(healthStatus==1){
            healthSub = "Observation";
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
