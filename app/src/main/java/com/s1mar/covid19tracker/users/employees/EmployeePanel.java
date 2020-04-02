package com.s1mar.covid19tracker.users.employees;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.data.models.TaskClientHealth;
import com.s1mar.covid19tracker.databinding.ActUserProfileBinding;
import com.s1mar.covid19tracker.functional_interfaces.IAction;
import com.s1mar.covid19tracker.users.admin.Activity_FeedNews;
import com.s1mar.covid19tracker.users.auxiliary.DialogFragment_HealthSelect;
import com.s1mar.covid19tracker.users.clients.Act_CustomerManagement;
import com.s1mar.covid19tracker.utils.LoaderUtil;
import com.s1mar.covid19tracker.utils.LoadingAnimationHelper;
import com.s1mar.covid19tracker.utils.NetworkUtils;
import com.s1mar.covid19tracker.utils.PlayerPrefs;
import com.s1mar.covid19tracker.utils.Toaster;

public class EmployeePanel extends AppCompatActivity {
    final static private String TAG = EmployeePanel.class.getSimpleName();

    private static final long DELAY_SECONDS = 2000L;
    private static final int TOAST_LENGTH = Toast.LENGTH_LONG;
    private ActUserProfileBinding mBinder;
    private MUser mUser;

    int myHealthStatus;
    int clientHealthStatus;
    int familyHealthStatus;

    private Toaster mToaster;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ActUserProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinder.getRoot());
        initialization();
        hookListeners();
    }

    void initialization(){
        try {
            mToaster = new Toaster(this);
            mUser = new Gson().fromJson(PlayerPrefs.getString(EmployeePanel.this,"muser"),MUser.class);
            initUI();

        }catch (Exception ex){
            Log.e(TAG, "initialization: ",ex );
        }
    }

    @SuppressLint("SetTextI18n")
    void initUI() {

        mBinder.headerBar.title.setText(mUser.getUsername());

        //Work from home or not
        setWorkFromHomeOrNot(mUser.isOnSite());

        //Determine health statuses
        mBinder.myHealthStatus.txtHolder.setText("Personal Health");
        mBinder.familyHealthStatus.txtHolder.setText("Family Health");
        mBinder.clientHealthStatus.txtHolder.setText("Client Health");

        myHealthStatus = mUser.getHealthStatus()==null?0:mUser.getHealthStatus();
        clientHealthStatus = determineClientHealthStatus(mUser,result -> {
            setHealthStatus(mBinder.clientHealthStatus.imgHolder,clientHealthStatus);
        });
        familyHealthStatus = mUser.getFamilyHealthStatus()==null?0:mUser.getFamilyHealthStatus();

        setHealthStatus(mBinder.myHealthStatus.imgHolder,myHealthStatus);
        setHealthStatus(mBinder.familyHealthStatus.imgHolder,familyHealthStatus);
        setHealthStatus(mBinder.clientHealthStatus.imgHolder,clientHealthStatus);

        //set Location data
        mBinder.edtCurrentPlace.getEditText().setText(mUser.getCurrentLocation());
        mBinder.edtHometown.getEditText().setText(mUser.getHomeTownAddress());
        mBinder.editTextPlacesVisited.setText(mUser.getPlacesVisited());

    }


    private void hookListeners(){

        mBinder.myHealthStatus.imgHolder.setOnClickListener(v->{

            DialogFragment_HealthSelect.showDialog(this,myHealthStatus,result -> {
                myHealthStatus = (int) result;
                setHealthStatus(mBinder.myHealthStatus.imgHolder, myHealthStatus);
            });


        });
        mBinder.familyHealthStatus.imgHolder.setOnClickListener(v->{
            DialogFragment_HealthSelect.showDialog(this,familyHealthStatus,result -> {
                familyHealthStatus = (int) result;
                setHealthStatus(mBinder.familyHealthStatus.imgHolder, familyHealthStatus);
            });
        });
        mBinder.clientHealthStatus.imgHolder.setOnClickListener(v -> {
            mToaster.showToast("Client Health can not be set from here, please use the client management screen",TOAST_LENGTH);
        });

        mBinder.btnSaveData.setOnClickListener(v->{

            if(!NetworkUtils.hasNetworkConnectivity(EmployeePanel.this)){
                Log.e(TAG, "hookListeners:saveBtn: No network connectivity!");
                mToaster.showToast("Please ensure that you have data connectivity before trying again", TOAST_LENGTH);
                return;
            }

            boolean workingOnSite = mBinder.radioGroupWorkingPlace.getCheckedRadioButtonId()==mBinder.radioBuilding.getId();
            int myHealthStatus = getHealthStatusFromView(mBinder.myHealthStatus.imgHolder);
            int familyHealthStatus = getHealthStatusFromView(mBinder.familyHealthStatus.imgHolder);
            int clientHealthStatus = getHealthStatusFromView(mBinder.clientHealthStatus.imgHolder);
            String currentLocationOfResidence = mBinder.edtCurrentPlace.getEditText().getText().toString();
            String hometown = mBinder.edtHometown.getEditText().getText().toString();
            String otherPlacesVisited = mBinder.edtPlacesVisited.getEditText().getText().toString();

            mUser.setOnSite(workingOnSite);
            mUser.setHealthStatus(myHealthStatus);
            mUser.setFamilyHealthStatus(familyHealthStatus);
            mUser.setClientHealthStatus(clientHealthStatus);
            mUser.setCurrentLocation(currentLocationOfResidence);
            mUser.setHomeTownAddress(hometown);
            mUser.setPlacesVisited(otherPlacesVisited);

            LoadingAnimationHelper.showMessage(EmployeePanel.this,"Saving Data..Please be patient");
            FireUsers.updateUser(mUser,actionResult->{
                LoadingAnimationHelper.dismissWithDelay(EmployeePanel.this,DELAY_SECONDS);
                if(actionResult instanceof Boolean && (Boolean) actionResult){
                        mToaster.showToast("Successfully Updated!",TOAST_LENGTH);
                        PlayerPrefs.setString(v.getContext(),"muser",new Gson().toJson(mUser));
                }
                else {
                    mToaster.showToast("Update Failed.",TOAST_LENGTH);
                }
            });

        });


        mBinder.clientCard.setOnClickListener(v->{
            LoaderUtil.loadAct(this, Act_CustomerManagement.class,null);
        });

        mBinder.feedCard.setOnClickListener(v->{
            LoaderUtil.loadAct(this, Activity_FeedNews.class,null);
        });

    }

    private void setWorkFromHomeOrNot(boolean onSite){
        if(onSite){
            mBinder.radioBuilding.setChecked(true);
        }
        else {
            mBinder.radioHome.setChecked(true);
        }
    }

    private int getHealthStatusFromView(ImageView imageView){
        int healthStatusAssumed = 0;
        int drawableId = (Integer) imageView.getTag();

        switch (drawableId){

            case R.drawable.observ_circle:
                healthStatusAssumed = 1;
                break;
            case R.drawable.infect_circle:
                healthStatusAssumed = 2;
        }

        return healthStatusAssumed;

    }

    private void setHealthStatus(ImageView imageView, int healthStatus){
        int drawableId = R.drawable.normal_circle;

        if(healthStatus>0){
            drawableId = healthStatus==1?R.drawable.observ_circle:R.drawable.infect_circle;
        }

        Drawable drawable = ContextCompat.getDrawable(this,drawableId);
        imageView.setImageDrawable(drawable);
        imageView.setTag(drawableId);
    }


    private Integer determineClientHealthStatus(MUser user, IAction action){
        new TaskClientHealth(user,action).execute();
        return 0; //Sync returns assumed health as healthy but, also runs a background async call to verify and update

    }


}
