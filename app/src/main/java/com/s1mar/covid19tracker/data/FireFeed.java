package com.s1mar.covid19tracker.data;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.data.models.MFeedItem;
import com.s1mar.covid19tracker.functional_interfaces.IAction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FireFeed {

    /**
     * Returns the document reference in case of success and Exception in case of failure
     * @param feedItem
     * @param action
     */
    public static void postItem(MFeedItem feedItem, IAction action){

        FirebaseFirestore.getInstance().collection(Constants.FEED)
                .add(feedItem)
                .addOnSuccessListener(action::onResult)
                .addOnFailureListener(action::onResult);
    }

    public static void updateItem(MFeedItem feedItem,IAction action){

        FirebaseFirestore.getInstance().collection(Constants.FEED).whereEqualTo("id",feedItem.getId()).get()
                .addOnCompleteListener(task -> {
                   try{
                       task.getResult().getDocuments().get(0).getReference().set(feedItem)
                               .addOnSuccessListener(action::onResult)
                               .addOnFailureListener(action::onResult);

                   }catch (Exception ex){
                        action.onResult(ex);
                   }
                });

    }

    public static void deleteItem(MFeedItem item,IAction action){
        FirebaseFirestore.getInstance().collection(Constants.FEED).document("/"+item.getId()).delete()
                .addOnSuccessListener(action::onResult).addOnFailureListener(action::onResult);
    }

    public static void getFeed(IAction action){
        FirebaseFirestore.getInstance().collection(Constants.FEED).get()
                .addOnCompleteListener(task -> {
                    try{
                      List<MFeedItem> feedList = task.getResult().toObjects(MFeedItem.class);
                        Collections.reverse(feedList);
                        action.onResult(feedList);
                    }catch (Exception ex){
                        action.onResult(ex);
                    }

                });
    }

}
