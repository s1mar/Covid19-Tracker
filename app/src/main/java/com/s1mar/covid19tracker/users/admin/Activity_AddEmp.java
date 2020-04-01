package com.s1mar.covid19tracker.users.admin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.AddUserLayoutBinding;
import com.s1mar.covid19tracker.utils.LoadingAnimationHelper;
import com.s1mar.covid19tracker.utils.TextUtils;
import com.s1mar.covid19tracker.utils.Toaster;


public class Activity_AddEmp extends AppCompatActivity {

    private static final String TAG = Activity_AddEmp.class.getSimpleName();

    private AddUserLayoutBinding binder;
    private Toaster mToaster;
    private static final int  MSG_DURATION = 2;
    private static final int MIN_LENGTH_USER = 4;
    private static final int MIN_LENGTH_PASS = 8;
    private static final int MAX_LENGTH = 14;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = AddUserLayoutBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        initListeners(binder);
        mToaster = new Toaster(this);
    }

    private void initListeners(AddUserLayoutBinding binder){
        binder.submit.setOnClickListener(this::onAddUserButtonSubmit);
    }

    private void onAddUserButtonSubmit(View view){

        String name = binder.nameEtxt.getText().toString();
        String username = binder.usernameEtxt.getText().toString();
        String password = binder.passwordEtxt.getText().toString();

        //Check to see if all fields have been "checked"
        if(TextUtils.isStringEmpty(name) || TextUtils.isStringEmpty(username)
                || TextUtils.isStringEmpty(password) || binder.roleGroup.getCheckedRadioButtonId()==-1){
            mToaster.showToast("All fields are mandatory, please fill them up",MSG_DURATION);
            return;
        }


        if( !TextUtils.satisfiesLength(MIN_LENGTH_USER,MAX_LENGTH,username)
                || ! TextUtils.satisfiesLength(MIN_LENGTH_PASS,MAX_LENGTH,password)){

                //Toaster text to show that these fields can't be empty
                mToaster.showToast(getString(R.string.toast_length_user_pass),MSG_DURATION);
                return;
        }

        //TODO: Ajay, add more checks ;) if necessary and do some clean-up
        //Conditions meet;

        LoadingAnimationHelper.showMessage(this,"Processing..");

        MUser userToBeAdded = new MUser();
        userToBeAdded.setName(name);
        userToBeAdded.setPassword(password);
        userToBeAdded.setUsername(username);
        final int checkedRadioId = binder.roleGroup.getCheckedRadioButtonId();
        if(checkedRadioId == binder.radioManager.getId()){
            userToBeAdded.setAdmin(true);
        }else if(checkedRadioId == binder.radioCustomer.getId()){
            userToBeAdded.setIsClient(true);
        }



        FireUsers.addUser(userToBeAdded,result -> {
            LoadingAnimationHelper.dismissWithDelay(this,MSG_DURATION*1000);
            if(result instanceof Boolean){
                    if((Boolean) result){
                        //use toaster to show the user has been successfully added
                        mToaster.showToast(getString(R.string.toast_user_succ_added),MSG_DURATION);
                        LoadingAnimationHelper.showMessage(this,"Taking you back to the previous screen");
                        new Handler().postDelayed(()->{
                            LoadingAnimationHelper.dismiss(Activity_AddEmp.this);
                            finish();

                        },MSG_DURATION*100);
                    }
                    else {
                        //use toaster to show that a similar named user already exists
                        mToaster.showToast(getString(R.string.toast_user_already_exists),MSG_DURATION);
                    }
            }
            else{
                mToaster.showToast(getString(R.string.toast_op_failed),MSG_DURATION);
                Log.e(TAG, "onAddUserButtonSubmit: "+result);
            }

        });
    }

}
