package com.s1mar.covid19tracker.users.auxiliary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.models.MFeedItem;
import com.s1mar.covid19tracker.databinding.ItemCardFeedNewsItemBinding;
import com.s1mar.covid19tracker.functional_interfaces.IAction;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter_FeedNews extends RecyclerView.Adapter<RecyclerAdapter_FeedNews.ViewHolder> {

    private List<MFeedItem> mDataSet = new ArrayList<>(0);

    private IAction deleteAction;
    private IAction updateAction;
    private boolean isAdmin;

    public RecyclerAdapter_FeedNews(boolean isAdmin,IAction deleteAction, IAction updateAction){
            this.deleteAction = deleteAction;
            this.updateAction = updateAction;
            this.isAdmin = isAdmin;
    }

    public void updateDataSet(List<MFeedItem> dataSet,boolean isReset){
        if(dataSet!=null){
            if(isReset){mDataSet.clear();}
            mDataSet.addAll(dataSet);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_feed_news_item,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        holder.updateUI(isAdmin);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MFeedItem feedItem = mDataSet.get(position);
            holder.binder.subjectTitle.setText(feedItem.getSubject());
            holder.binder.contentText.setText(feedItem.getDesc());
            holder.binder.timeStamp.setText(feedItem.getTime().toString());
            if(isAdmin) {
                holder.binder.delete.setOnClickListener(v -> deleteAction.onResult(feedItem));
                holder.binder.edit.setOnClickListener(v -> updateAction.onResult(feedItem));
            }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ItemCardFeedNewsItemBinding binder;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            binder = ItemCardFeedNewsItemBinding.bind(itemView);
        }
        void updateUI(boolean isAdmin){
            if(!isAdmin){
                binder.delete.setVisibility(View.GONE);
                binder.edit.setVisibility(View.GONE);
            }
            else{
                binder.delete.setVisibility(View.VISIBLE);
                binder.edit.setVisibility(View.VISIBLE);
            }
        }
    }
}
