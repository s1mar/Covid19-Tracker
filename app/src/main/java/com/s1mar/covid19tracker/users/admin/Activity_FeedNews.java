package com.s1mar.covid19tracker.users.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.data.FireFeed;
import com.s1mar.covid19tracker.data.models.MFeedItem;
import com.s1mar.covid19tracker.databinding.ActivityFeedNewsBinding;
import com.s1mar.covid19tracker.users.auxiliary.RecyclerAdapter_FeedNews;
import com.s1mar.covid19tracker.utils.LoaderUtil;
import com.s1mar.covid19tracker.utils.Toaster;

import java.util.List;

public class Activity_FeedNews extends AppCompatActivity {

    private static final String TAG = Activity_FeedNews.class.getSimpleName();

    private RecyclerAdapter_FeedNews mAdapter;
    private Toaster mToaster;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedNewsBinding binder = ActivityFeedNewsBinding.inflate(getLayoutInflater());
        mToaster = new Toaster(this);
        initActionsAndAdapter(binder);
        initListeners(binder);
        setContentView(binder.getRoot());
    }

    private void initActionsAndAdapter(ActivityFeedNewsBinding binder){
        mAdapter = new RecyclerAdapter_FeedNews(feedItem->{
           //delete action
            FireFeed.deleteItem((MFeedItem) feedItem,result->{
                String msg = getString(R.string.delete_feed_succ);
                if(result instanceof Exception){
                    msg = getString(R.string.del_item_feed_failed);
                    Log.e(TAG, "initActionsAndAdapter: delete: ex"+ result );

                }

                mToaster.showToast(msg,2);
            });
        }, feedItem->{
            //update action
            LoaderUtil.loadAct(Activity_FeedNews.this,Activity_AdminNewPost.class,(MFeedItem)feedItem);
        });

        binder.recycler.setAdapter(mAdapter);

        //Populate with data
        FirebaseFirestore.getInstance().collection(Constants.FEED).addSnapshotListener((queryDocumentSnapshots, e) -> {

            if(queryDocumentSnapshots!=null){
                List<MFeedItem> feedNews = queryDocumentSnapshots.toObjects(MFeedItem.class);
                mAdapter.updateDataSet(feedNews,true);
        }

        });
    }

    private void initListeners(ActivityFeedNewsBinding binder){
        binder.post.setOnClickListener(v->{LoaderUtil.loadAct(this,Activity_AdminNewPost.class,null);});
    }
}
