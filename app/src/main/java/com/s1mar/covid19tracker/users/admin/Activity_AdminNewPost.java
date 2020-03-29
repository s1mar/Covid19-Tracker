package com.s1mar.covid19tracker.users.admin;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.FireFeed;
import com.s1mar.covid19tracker.data.models.MFeedItem;
import com.s1mar.covid19tracker.databinding.AdminNewPostLayoutBinding;
import com.s1mar.covid19tracker.utils.TextUtils;
import com.s1mar.covid19tracker.utils.Toaster;

public class Activity_AdminNewPost extends AppCompatActivity {

    private static final String TAG = Activity_AdminNewPost.class.getSimpleName();
    Toaster mToaster;
    boolean isUpdate;
    MFeedItem feedItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdminNewPostLayoutBinding binder = AdminNewPostLayoutBinding.inflate(getLayoutInflater());
        mToaster = new Toaster(this);
        processIntent(binder);
        initListeners(binder);
        setContentView(binder.getRoot());
    }


    private void processIntent(AdminNewPostLayoutBinding binder){
                if(getIntent()==null){
                    return;
                }
                try {
                    feedItem = getIntent().getParcelableExtra(getString(R.string.token_parcel));
                    binder.etxtSubject.setText(feedItem.getSubject());
                    binder.etxtDesc.setText(feedItem.getDesc());
                    binder.headerText.setText(R.string.update_post_token);
                    binder.post.setText(R.string.post_update_token);
                    isUpdate = true;
                }
                catch (Exception ex){
                    Log.e(TAG, "processIntent: "+ex);
                }
    }

    private void initListeners(AdminNewPostLayoutBinding binder){

        binder.post.setOnClickListener(v->{
            String subject = binder.etxtSubject.getText().toString();
            String description = binder.etxtDesc.getText().toString();

            if(TextUtils.isStringEmpty(subject) || TextUtils.isStringEmpty(description)){
                mToaster.showToast(getString(R.string.fields_cant_empty),4);
                return;
            }

            if(isUpdate){
                feedItem.setSubject(subject);
                feedItem.setDesc(description);
                FireFeed.updateItem(feedItem,result -> {
                    if(result instanceof Exception){
                        mToaster.showToast(getString(R.string.cant_update_post),4);
                        Log.e(TAG, "FireFreed.updateItem(): "+result);
                    }
                    else{
                        mToaster.showToast(getString(R.string.post_update),4);
                    }
                });
            }
            else{
                MFeedItem feedItem = new MFeedItem();
                feedItem.setSubject(subject);
                feedItem.setDesc(description);

                FireFeed.postItem(feedItem,result -> {
                    if(result instanceof Exception){
                        mToaster.showToast(getString(R.string.post_failed),4);
                    }
                    else{
                        mToaster.showToast(getString(R.string.succ_added_post),4);
                    }
                });
            }

        });

    }
}
