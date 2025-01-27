package com.s1mar.covid19tracker.users.auxiliary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.data.models.TaskClientHealth;
import com.s1mar.covid19tracker.databinding.ItemCardEmpStatusBinding;
import com.s1mar.covid19tracker.functional_interfaces.IAction;
import com.s1mar.covid19tracker.utils.LoadingAnimationHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class RecyclerAdapter_Employee extends RecyclerView.Adapter<RecyclerAdapter_Employee.ViewHolder> {

    final static private String TAG = RecyclerAdapter_Employee.class.getSimpleName();
    private List<MUser> mDataSet = new ArrayList<MUser>(0);
    private HashMap<MUser,Integer> clientHealthStatusMap = new HashMap<>(0);

    private Toast mToast;

    private IAction onClickEmp;
    public RecyclerAdapter_Employee(IAction onClickEmp) {
        this.onClickEmp = onClickEmp;
    }


    private  int filterView = 0; // 0->all, 1->Manager, 2->Employee,3-> Customer

    public void setFilterView(int filterView) {
        this.filterView = filterView;
    }

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


        holder.itemView.setOnClickListener(v->{
            onClickEmp.onResult(employee);
        });

        holder.binder.delete.setOnClickListener(v->{
            if(employee.isNotDeletable()){
                showToast(holder.itemView.getContext(),"User Can't be Deleted");
                return;
            }

            LoadingAnimationHelper.showMessage((Activity) v.getContext(),"Deleting..");
            FireUsers.deleteUser(employee.getUsername(),result -> {
            LoadingAnimationHelper.dismissWithDelay((Activity) v.getContext(),2000L);
                try {
                    if ((Boolean) result) {
                        showToast(holder.itemView.getContext(),"User Successfully Deleted");
                        notifyDataSetChanged();

                    } else {
                      showToast(holder.itemView.getContext(),"Couldn't delete user");

                    }
                }catch (Exception ex){
                    Log.e(TAG, "onBindViewHolder: ",ex);
                }
            });
        });
        if(filterView==0){
            holder.binder.workSite.setVisibility(View.GONE);
            holder.binder.familyStatus.setVisibility(View.GONE);
            holder.binder.clientStatus.setVisibility(View.GONE);
        }
        else if(filterView==1){
            holder.binder.workSite.setVisibility(View.GONE);
            holder.binder.familyStatus.setVisibility(View.GONE);
            holder.binder.clientStatus.setVisibility(View.GONE);
        }
        else if(filterView==3){
            holder.binder.familyStatus.setVisibility(View.GONE);
            holder.binder.clientStatus.setVisibility(View.GONE);
            holder.binder.workSite.setVisibility(View.GONE);
        }
        else{
            holder.binder.familyStatus.setVisibility(View.VISIBLE);
            holder.binder.clientStatus.setVisibility(View.VISIBLE);
            holder.binder.workSite.setVisibility(View.VISIBLE);
        }


        //Show the username and password
        String usernameTxt = employee.getUsername();
        String passwordTxt = employee.getPassword();
        Spannable spanUsrName = new SpannableString("Username: "+usernameTxt);
        Spannable spanPass = new SpannableString("Password: "+passwordTxt);
        spanPass.setSpan(new ForegroundColorSpan(ContextCompat.getColor(holder.itemView.getContext(),R.color.mediumTurquoise))
                ,0,spanPass.length()-passwordTxt.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanUsrName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(holder.itemView.getContext(),R.color.mediumTurquoise))
                ,0,spanUsrName.length()-usernameTxt.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


        holder.binder.bottomContainer.setVisibility(View.VISIBLE);
        holder.binder.username.setText(spanUsrName);
        holder.binder.password.setText(spanPass);
    }

    private void showToast(Context context, String msg){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        mToast.show();
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
       String health = "Health: ";
       String healthSub = "Normal";
        int color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_green);
       if(healthStatus==1){
        healthSub = "Observation";
           color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_orange);
       }else if(healthStatus==2){
           healthSub = "Infected";
           color = Color.RED;
       }
       String healthFinal = health+healthSub;
       Spannable span = new SpannableString(healthFinal);
       span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
       holder.binder.selfStatus.setText(span);

    }


    private void bindClientHealthStatus_Tres(ViewHolder holder, Integer healthStatus){
        //Integer healthStatus = clientHealthStatusMap.get(employee);
        if(holder==null){return;}

        if(healthStatus==null){healthStatus = 0;}
        String health = "Client Health: ";
        String healthSub = "Normal";
        int color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_green);
        if(healthStatus==1){
            healthSub = "Observation";
            color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_orange);
        }else if(healthStatus==2){
            healthSub = "Infected";
            color = Color.RED;
        }
        String healthFinal = health+healthSub;
        Spannable span = new SpannableString(healthFinal);
        span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.binder.clientStatus.setText(span);

    }


    private void bindClientHealthStatus(ViewHolder holder,MUser employee){

        if(employee.getClients()==null || employee.getClients().isEmpty())
        {
            bindClientHealthStatus_Tres(holder,0); //assume the client(s) to be healthy
        }
        else{

            //We have client health data in the map
            if(clientHealthStatusMap.containsKey(employee) && clientHealthStatusMap.get(employee)!=null){
                bindClientHealthStatus_Tres(holder,clientHealthStatusMap.get(employee));
            }
            else{
                //We'll query the database and update data
               new TaskClientHealth(holder.itemView.getContext(),employee,result -> {
                   clientHealthStatusMap.put(employee,(Integer)result);
                   bindClientHealthStatus_Tres(holder,(Integer)result);
               }).execute();
            }

        }

    }

    private void bindFamilyHealthStatus(ViewHolder holder, MUser employee){
        Integer healthStatus  = employee.getFamilyHealthStatus();
        if(healthStatus==null){healthStatus = 0;}
        String health = "Family Health: ";
        String healthSub = "Normal";

        //int color = Color.GREEN;
        int color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_green);
        if(healthStatus==1){
            healthSub = "Observation";

            //color = Color.argb(100,255,165,0);
            color = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_orange);
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
