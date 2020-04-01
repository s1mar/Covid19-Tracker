package com.s1mar.covid19tracker.users.auxiliary;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.databinding.DialogInfectedBinding;
import com.s1mar.covid19tracker.functional_interfaces.IAction;

public class DialogFragment_HealthSelect extends DialogFragment {
    final static String TAG = DialogFragment_HealthSelect.class.getSimpleName();
    private DialogInfectedBinding binding;
    private IAction actionOnResult;

    public DialogFragment_HealthSelect() {
    }

    public static DialogFragment_HealthSelect newInstance(Integer healthStatus) {
        if(healthStatus ==null){healthStatus =0;}
        DialogFragment_HealthSelect dialogFragment_healthSelect = new DialogFragment_HealthSelect();
        Bundle args = new Bundle();
        args.putInt("health", healthStatus);
        dialogFragment_healthSelect.setArguments(args);
        return dialogFragment_healthSelect;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  DialogInfectedBinding.bind(inflater.inflate(R.layout.dialog_infected, container));
        return binding.getRoot();
    }

    public static void showDialog(AppCompatActivity appCompatActivity, Integer healthStatus, IAction actionOnResult) {
        FragmentManager fm = appCompatActivity.getSupportFragmentManager();
        DialogFragment_HealthSelect dialog = DialogFragment_HealthSelect.newInstance(healthStatus);
        dialog.setActionOnResult(actionOnResult);
        dialog.show(fm, dialog.getClass().getSimpleName());
    }

    private void setActionOnResult(IAction actionOnResult) {
        this.actionOnResult = actionOnResult;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int healthIndicator = getArguments().getInt("health", 0);

        if(healthIndicator==0){
            binding.normal.setChecked(true);
        }
        else if(healthIndicator==1){
            binding.observation.setChecked(true);
        }else{
            binding.infected.setChecked(true);
        }

        binding.radioManager.setOnCheckedChangeListener((group, checkedId) -> {

            int updatedHealthStatus = 0;
            if(checkedId == binding.observation.getId()){
                updatedHealthStatus = 1;
            }
            else if(checkedId == binding.infected.getId()){
                updatedHealthStatus = 2;
            }

            actionOnResult.onResult(updatedHealthStatus);
            dismissAllowingStateLoss();
        });


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
