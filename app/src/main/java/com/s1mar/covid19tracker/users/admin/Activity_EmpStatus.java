package com.s1mar.covid19tracker.users.admin;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.annimon.stream.Stream;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.AdminEmpStatusLayoutBinding;
import com.s1mar.covid19tracker.users.auxiliary.RecyclerAdapter_Employee;
import com.s1mar.covid19tracker.users.employees.EmployeePanel;
import com.s1mar.covid19tracker.utils.LoaderUtil;

import java.util.List;

public class Activity_EmpStatus extends AppCompatActivity {

    private RecyclerAdapter_Employee mAdapter;
    AdminEmpStatusLayoutBinding binder;
    private List<MUser> fullDataSet;
    private List<MUser> employees;
    private List<MUser> customers;
    private List<MUser> managers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = AdminEmpStatusLayoutBinding.inflate(getLayoutInflater(),null,false);
        mAdapter = new RecyclerAdapter_Employee((user)->{
            onClick_launchDetails((MUser) user);
        });

        binder.recycler.setAdapter(mAdapter);
        binder.fab.setOnClickListener(v->{
            LoaderUtil.loadAct(this,Activity_AddEmp.class,null);
        });

        binder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            List<MUser> dataSetToPopulate = fullDataSet;
            switch (checkedId){
                case R.id.rb_All:
                    dataSetToPopulate = fullDataSet;
                    mAdapter.setFilterView(0);
                    break;


                case R.id.rb_Managers:
                    dataSetToPopulate = managers;
                    mAdapter.setFilterView(1);
                    break;

                case R.id.rb_Employees:
                    dataSetToPopulate = employees;
                    mAdapter.setFilterView(2);
                    break;

                case R.id.rb_Customers:
                    dataSetToPopulate = customers;
                    mAdapter.setFilterView(3);
                    break;
            }
            mAdapter.updateSet(dataSetToPopulate,true);

        });

        init();
        setContentView(binder.getRoot());

    }

    private void init(){

        FirebaseFirestore.getInstance().collection(Constants.USERS).addSnapshotListener((queryDocumentSnapshots, e) -> {

            if(queryDocumentSnapshots!=null){
                fullDataSet = queryDocumentSnapshots.toObjects(MUser.class);

                employees =  Stream.of(fullDataSet).filter(user-> !user.isClient() && !user.isAdmin()).toList();
                customers =  Stream.of(fullDataSet).filter(user-> user.isClient() && !user.isAdmin()).toList();
                managers =  Stream.of(fullDataSet).filter(MUser::isAdmin).toList();

                List<MUser> dataSetToPopulate = fullDataSet;
                switch (binder.rgOptions.getCheckedRadioButtonId()){

                    case R.id.rb_Managers:
                        dataSetToPopulate = managers;
                        break;

                    case R.id.rb_Employees:
                        dataSetToPopulate = employees;
                        break;

                    case R.id.rb_Customers:
                        dataSetToPopulate = customers;
                        break;

                }

                mAdapter.updateSet(dataSetToPopulate,true);
            }

        });


    }

    private void onClick_launchDetails(MUser user){
        if(user.isAdmin()){return;}
        Intent launchIntent = new Intent(this, EmployeePanel.class);
        launchIntent.putExtra("config",2);
        launchIntent.putExtra("parcel",user);
        startActivity(launchIntent);
    }
}
