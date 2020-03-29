package com.s1mar.covid19tracker.users.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.s1mar.covid19tracker.databinding.ActivityFeedNewsBinding;
import com.s1mar.covid19tracker.utils.LoaderUtil;

public class Activity_FeedNews extends AppCompatActivity {

    private static final String TAG = Activity_FeedNews.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedNewsBinding binder = ActivityFeedNewsBinding.inflate(getLayoutInflater());

        initListeners(binder);
    }

    private void initListeners(ActivityFeedNewsBinding binder){
        binder.post.setOnClickListener(v->{LoaderUtil.loadAct(this,Activity_AdminNewPost.class);});
    }
}
