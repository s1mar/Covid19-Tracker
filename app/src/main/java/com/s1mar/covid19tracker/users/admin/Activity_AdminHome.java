package com.s1mar.covid19tracker.users.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.ActivityAdminHomeBinding;
import com.s1mar.covid19tracker.utils.LoaderUtil;

public class Activity_AdminHome extends AppCompatActivity {

    private static final String TAG = Activity_AdminHome.class.getSimpleName();
    private ActivityAdminHomeBinding mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        mBinder.headerBar.title.setText(getString(R.string.home));
        setContentView(mBinder.getRoot());
        hookListeners();
    }

    private void hookListeners() {
        mBinder.btnEmpManagement.setOnClickListener(v -> {
            LoaderUtil.loadAct(Activity_AdminHome.this, Activity_EmpStatus.class, null);
        });
        mBinder.btnFeed.setOnClickListener(v->{LoaderUtil.loadAct(Activity_AdminHome.this,Activity_FeedNews.class,null);});
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection(Constants.USERS).whereEqualTo("admin",false).whereEqualTo("client",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e==null){
                               if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()){
                                   //set everything to zero
                                   setEmployeesWorkingFromHome(0);
                                   setEmployeesWorkingOnClientSite(0);
                                   setEmpHealthStatus(0,0,0);
                                   return;
                               }

                               int numOnSite = 0;
                               int totalNumber = 0;

                               int numEmpNormal = 0;
                               int numEmpInfected = 0;
                               int numEmpInObservation = 0;
                                for (MUser employee: queryDocumentSnapshots.toObjects(MUser.class)){
                                        if(employee.isOnSite()){
                                            numOnSite++;
                                        }
                                        totalNumber++;



                                       if(employee.getHealthStatus()!=null) {
                                           switch (employee.getHealthStatus()) {

                                               case 0:
                                                   numEmpNormal++;
                                                   break;
                                               case 1:
                                                   numEmpInObservation++;
                                                   break;
                                               case 2:
                                                   numEmpInfected++;
                                                   break;
                                           }
                                       }
                                }

                                int workingFromHome = totalNumber - numOnSite;

                                setEmployeesWorkingFromHome(workingFromHome);
                                setEmployeesWorkingOnClientSite(numOnSite);
                                setEmpHealthStatus(numEmpNormal,numEmpInfected,numEmpInObservation);
                        }
                        else{
                            //TODO: More case handling when an exception occurs
                        }

                    }
                });

    }

    private void setEmpHealthStatus(int healthy, int infected, int underObservation){
        String formattedHealthy  = getString(R.string.health_normal,healthy);
        String formattedInfected = getString(R.string.health_infected,infected);
        String formattedUnderObs = getString(R.string.health_observation,underObservation);

        mBinder.txtHealthNormal.setText(formattedHealthy);
        mBinder.txtHealthInfected.setText(formattedInfected);
        mBinder.txtHealthObs.setText(formattedUnderObs);
    }

    private void setEmployeesWorkingFromHome(int number){
        String formattedString = getString(R.string.num_working_from_home,number);
        mBinder.txtWorkFromHome.setText(formattedString);
    }

    private void setEmployeesWorkingOnClientSite(int number){
        String formattedString = getString(R.string.num_working_on_client_site,number);
        mBinder.txtWorkOnSite.setText(formattedString);
    }



}
