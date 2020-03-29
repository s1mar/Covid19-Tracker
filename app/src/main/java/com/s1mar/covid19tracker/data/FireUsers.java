package com.s1mar.covid19tracker.data;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.data.models.MEmployee;
import com.s1mar.covid19tracker.functional_interfaces.IAction;

import static com.s1mar.covid19tracker.data.Constants.FIELD_UNAME;
import static com.s1mar.covid19tracker.data.Constants.USERS;


public class FireUsers {

    /**
     * what do the return types mean
     * Boolean-false: User doesn't exist
     * DocumentSnapshot: User exists and returns it's Snapshot
     * Exception or null: Operation Failed
     * @param userName
     * @param action
     */
    public static void getUser(String userName,IAction action){
        FirebaseFirestore.getInstance().collection(USERS).whereEqualTo(FIELD_UNAME,userName).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                         if(task.getResult()!=null && !task.getResult().isEmpty()) {
                             action.onResult(task.getResult().getDocuments().get(0));
                         }else{
                             action.onResult(false); //User doesn't exist
                         }
                    }
                    else{
                        //Task failed
                        action.onResult(new Exception("Operation Failed"));
                    }
                });


    }

    /**
     * What do the return types mean
     * Boolean-True: User successfully added
     * Exception: Operation Failed
     * Boolean-False: User already exists
     * @param username
     * @param password
     * @param isAdmin
     * @param action
     */
    public static void addUser(String name,String username,String password,boolean isAdmin,IAction action){

        //Check to see whether the user already exists in the system
        getUser(username,(result)->{

                    if(result instanceof Exception){
                        action.onResult(result); //operation failed
                    }
                    else if(result instanceof Boolean && !(Boolean) result)
                    {
                        //Since the user doesn't exist, let's insert it
                        MEmployee employee = new MEmployee(name,username,password);
                        FirebaseFirestore.getInstance().collection(USERS)
                                .add(employee)
                                .addOnSuccessListener(documentReference -> {action.onResult(true);})
                                .addOnFailureListener(action::onResult);
                    }
                    else{
                       action.onResult(false); //User already exists
                    }

        });

    }

    /**
     * If it returns true then it means the operation was successful, otherwise the operation failed
     * @param username
     * @param action
     */
    public static void deleteUser(String username,IAction action){
        getUser(username,result -> {
            if(result instanceof DocumentSnapshot){
                FirebaseFirestore.getInstance().document(((DocumentSnapshot) result).getReference().getPath())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                           action.onResult(true); //delete successful
                        })
                        .addOnFailureListener(action::onResult);
            }
            else {
                action.onResult(result);
            }
        });
    }

    /**
     * if returns true then it means the update operation succeeded, otherwise it failed
     * @param employee
     * @param action
     */
    public static void updateUser(MEmployee employee,IAction action){

        getUser(employee.getUsername(),result -> {

            if(result instanceof DocumentSnapshot){

                FirebaseFirestore.getInstance().document(((DocumentSnapshot) result).getReference().getPath())
                        .set(employee)
                        .addOnSuccessListener(aVoid -> {
                            action.onResult(true); //Successfuly update
                        })
                        .addOnFailureListener(action::onResult);
            }
            else {
                action.onResult(result);
            }

        });

    }

}
