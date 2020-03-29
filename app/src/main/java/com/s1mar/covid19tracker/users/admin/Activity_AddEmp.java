package com.s1mar.covid19tracker.users.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.databinding.AddUserLayoutBinding;
import com.s1mar.covid19tracker.utils.TextUtils;
import com.s1mar.covid19tracker.utils.Toaster;


public class Activity_AddEmp extends AppCompatActivity {

    private static final String TAG = Activity_AddEmp.class.getSimpleName();

    private AddUserLayoutBinding binder;
    private Toaster mToaster;
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

        if( TextUtils.isStringEmpty(name) || !TextUtils.satisfiesLength(4,14,username)
                || ! TextUtils.satisfiesLength(8,14,password)){

                //Toaster text to show that these fields can't be empty
                mToaster.showToast(getString(R.string.toast_length_user_pass),2);
                return;
        }
        //TODO: Ajay, add more checks ;) if necessary and do some clean-up
        //Conditions meet;

        FireUsers.addUser(name,username,password,false,result -> {

            if(result instanceof Boolean){
                    if((Boolean) result){
                        //use toaster to show the user has been successfully added
                        mToaster.showToast(getString(R.string.toast_user_succ_added),2);
                    }
                    else {
                        //use toaster to show that a similar named user already exists
                        mToaster.showToast(getString(R.string.toast_user_already_exists),2);
                    }
            }
            else{
                mToaster.showToast(getString(R.string.toast_op_failed),2);
                Log.e(TAG, "onAddUserButtonSubmit: "+result);
            }

        });
    }

}
