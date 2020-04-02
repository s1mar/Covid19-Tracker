package com.s1mar.covid19tracker.users.employees;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.s1mar.covid19tracker.utils.Utils;

import java.util.List;

public class EmployeePanel extends AppCompatActivity {
    final static private String TAG = EmployeePanel.class.getSimpleName();

    private static final long DELAY_SECONDS = 2000L;
    private static final int TOAST_LENGTH = Toast.LENGTH_LONG;
    private ActUserProfileBinding mBinder;
    private MUser mUser;

    int myHealthStatus;
    int familyHealthStatus;

    private Toaster mToaster;

    private int configValue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ActUserProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinder.getRoot());
        processParcel();
        hookListeners();

        if(configValue>1){
            //Admin access; make it all read-only
            setUiConfigReadOnly();
            mBinder.btnSaveData.setVisibility(View.GONE);
            mBinder.clientCard.setVisibility(View.GONE);
            mBinder.feedCard.setVisibility(View.GONE);
        }
    }


    //config means
    //0->self
    //2->admin
    //1->customer
    private void processParcel(){

        try{
            Intent intent = getIntent();
            if(intent == null || intent.getIntExtra("config",0) == 0
                    || intent.getParcelableExtra("parcel") == null){

                mUser = new Gson().fromJson(PlayerPrefs.getString(EmployeePanel.this,"muser"),MUser.class);

            }
            else{
                    configValue = intent.getIntExtra("config",0);
                    mUser = intent.getParcelableExtra("parcel");

            }

            initialization();

        }catch (Exception ex){
            Log.e(TAG, "processParcel: ",ex);
        }
    }

    void initialization(){
        try {

            mToaster = new Toaster(this);
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

        familyHealthStatus = mUser.getFamilyHealthStatus()==null?0:mUser.getFamilyHealthStatus();

        setHealthStatus(mBinder.myHealthStatus.imgHolder,myHealthStatus);
        setHealthStatus(mBinder.familyHealthStatus.imgHolder,familyHealthStatus);


        //set Location data
        mBinder.edtCurrentPlace.getEditText().setText(mUser.getCurrentLocation());
        mBinder.edtHometown.getEditText().setText(mUser.getHomeTownAddress());
        mBinder.editTextPlacesVisited.setText(mUser.getPlacesVisited());


    }

    private void setUiConfigReadOnly(){
        setReadOnlyTheWholeView();
    }

    private void setReadOnlyTheWholeView(){
        List<View> viewListRoot = Utils.getAllChildren(mBinder.getRoot());
        for(View v : viewListRoot){
            v.setOnClickListener(null);
            if(v instanceof EditText){
                ((EditText)v).setInputType(InputType.TYPE_NULL);
            }else if(v instanceof RadioGroup){
                ((RadioGroup)v).setOnCheckedChangeListener(null);
                ((RadioGroup)v).setActivated(false);
            }else if(v instanceof RadioButton){
                ((RadioButton)v).setEnabled(false);
                ((RadioButton)v).setClickable(false);
                ((RadioButton)v).setFocusable(false);
            }

        }
        mBinder.getRoot().setActivated(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        determineAndSetClientHealthStatus(mUser,result -> {
            setHealthStatus(mBinder.clientHealthStatus.imgHolder,(Integer)result);
        });
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


    private void determineAndSetClientHealthStatus(MUser user, IAction action){
        new TaskClientHealth(EmployeePanel.this,user,action).execute();
    }


}
