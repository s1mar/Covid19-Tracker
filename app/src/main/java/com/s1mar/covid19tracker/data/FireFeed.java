package com.s1mar.covid19tracker.data;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.ServerTimestamps;
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

        FirebaseFirestore.getInstance().collection(Constants.FEED).document("/"+feedItem.getId()).get()
                .addOnCompleteListener(task -> {
                   try{
                     DocumentReference documentReference = task.getResult().getReference();
                     Task updateTheItem = documentReference.set(feedItem);
                     Task updateTheTimeField = documentReference.update("time",com.google.firebase.firestore.FieldValue.serverTimestamp());

                      Tasks.whenAllSuccess(updateTheItem,updateTheTimeField)
                                .addOnSuccessListener(action::onResult)
                                .addOnFailureListener(action::onResult);

                   /* .addOnCompleteListener(taskAlpha->{
                                  if(taskAlpha.isSuccessful() && taskAlpha.getResult()!=null){
                                      action.onResult(true);
                                  }
                                  else{
                                      action.onResult(taskAlpha.getException());
                                  }
                              });*/


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
