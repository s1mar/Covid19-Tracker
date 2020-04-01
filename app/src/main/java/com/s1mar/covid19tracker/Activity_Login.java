package com.s1mar.covid19tracker;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.ActLoginBinding;
import com.s1mar.covid19tracker.users.admin.Activity_AdminHome;
import com.s1mar.covid19tracker.users.employees.EmployeePanel;
import com.s1mar.covid19tracker.utils.LoaderUtil;
import com.s1mar.covid19tracker.utils.LoadingAnimationHelper;
import com.s1mar.covid19tracker.utils.NetworkUtils;
import com.s1mar.covid19tracker.utils.PlayerPrefs;
import com.s1mar.covid19tracker.utils.TextUtils;
import com.s1mar.covid19tracker.utils.Toaster;

public class Activity_Login extends AppCompatActivity {
    final static private String TAG = Activity_Login.class.getSimpleName();

    private ActLoginBinding mBinder;
    private Toaster mToaster;
    private static int messageDuration = 2; //in seconds
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToaster = new Toaster(this);
        mBinder = ActLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinder.getRoot());
        initListeners();
    }

    private void initListeners(){
        mBinder.submit.setOnClickListener(v->{
            loginProtocol();
        });
    }

    private void loginProtocol(){
            String username = mBinder.usernameEtxt.getText().toString();
            String pass = mBinder.passwordEtxt.getText().toString();

            if(TextUtils.isStringEmpty(username) || TextUtils.isStringEmpty(pass)){
                mToaster.showToast("Please enter a username and password",messageDuration);
                return;
            }

        if(NetworkUtils.hasNetworkConnectivity(this)){
            //TODO: Freeze screen or show a loader
            LoadingAnimationHelper.showMessage(this,"Authenticating..");
            FireUsers.getUser(username,actionResult->{
        try {
            //TODO: Unfreeze screen
            LoadingAnimationHelper.dismissWithDelay(this,1000);
            if (actionResult instanceof Exception || actionResult==null) {
                //The operation failed or some error was encountered
               throw actionResult!=null ?(Exception) actionResult:new Exception("Operation failed");
            }else if(actionResult instanceof Boolean && !(Boolean) actionResult){
                //Username not found
                mToaster.showToast("Username or password seems to not exist",messageDuration);
            }
            else {
                MUser user = ((DocumentSnapshot)actionResult).toObject(MUser.class);
                if(user.getPassword().equals(pass)){
                        //Proceed;
                    mToaster.showToast("Login Successful",messageDuration);

                   Class<? extends AppCompatActivity> activityToLoad = null;
                   if(user.isAdmin()){activityToLoad = Activity_AdminHome.class; }
                   else {
                       activityToLoad = EmployeePanel.class;
                   }
                   PlayerPrefs.setString(Activity_Login.this,"muser",new Gson().toJson(user));

                    LoaderUtil.loadAct(this,activityToLoad,null);
                    //Finish this activity
                    finish();
                }
                else {
                    //Wrong password
                    mToaster.showToast("The username and password combination seems to be invalid! Please try again",messageDuration);
                }
        } } catch (Exception ex) {
            mToaster.showToast("Something wen't wrong please try again!", messageDuration);
            Log.e(TAG, "loginProtocol:ex: " + ex);
        }
            });
        }


    }

}
