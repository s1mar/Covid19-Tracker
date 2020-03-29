package com.s1mar.covid19tracker.users.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.data.models.MEmployee;
import com.s1mar.covid19tracker.databinding.AdminEmpStatusLayoutBinding;
import com.s1mar.covid19tracker.users.auxiliary.RecyclerAdapter_Employee;
import com.s1mar.covid19tracker.utils.LoaderUtil;

import java.util.List;

public class Activity_EmpStatus extends AppCompatActivity {

    private RecyclerAdapter_Employee mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdminEmpStatusLayoutBinding binder = AdminEmpStatusLayoutBinding.inflate(getLayoutInflater(),null,false);
        mAdapter = new RecyclerAdapter_Employee();
        binder.recycler.setAdapter(mAdapter);
        binder.fab.setOnClickListener(v->{
            LoaderUtil.loadAct(this,Activity_AddEmp.class);
        });
        init();
        setContentView(binder.getRoot());

    }

    private void init(){

        FirebaseFirestore.getInstance().collection(Constants.USERS).addSnapshotListener((queryDocumentSnapshots, e) -> {

            if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){

                List<MEmployee> employees = queryDocumentSnapshots.toObjects(MEmployee.class);
                mAdapter.updateSet(employees,true);
            }

        });

    }
}
