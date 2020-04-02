package com.s1mar.covid19tracker.data.models;

import android.os.AsyncTask;

import com.google.firebase.firestore.FirebaseFirestore;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.functional_interfaces.IAction;

import java.util.concurrent.atomic.AtomicInteger;


public class TaskClientHealth extends AsyncTask<Void,Void,Void> {

    private MUser emp;
    private IAction action;

    public TaskClientHealth(MUser emp, IAction action) {
        super();
        this.emp = emp;
        this.action = action;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        int[] clientHealth = {0}; //Assume all clients to be healthy
        AtomicInteger awaitingCallbacks = emp.getClients()!=null && !emp.getClients().isEmpty()?new AtomicInteger(emp.getClients().size()): new AtomicInteger();
        if(awaitingCallbacks.get() >0){
            for(String key:emp.getClients()) {
                String[] str = key.split(";");
                String username = str[0];
                String name = str[1];
                FirebaseFirestore.getInstance().collection(Constants.USERS)
                        .whereEqualTo("username",username).whereEqualTo("name",name)
                        .get().addOnCompleteListener(task -> {

                            int currentStateOfCallbacks = awaitingCallbacks.decrementAndGet();


                            if(task.isSuccessful() && task.getResult()!=null){
                                        MUser user = task.getResult().getDocuments().get(0).toObject(MUser.class);
                                        clientHealth[0] = user!=null && user.getHealthStatus()!=null&&user.getHealthStatus()>clientHealth[0]?user.getHealthStatus():clientHealth[0];
                            }


                            if(currentStateOfCallbacks<=0){
                                executeAction(clientHealth[0]);
                            }

                });

            }
        }
        else {
           executeAction(clientHealth[0]);
        }

        return null;
    }



    private void executeAction(Integer clientHealth) {
        if (action != null) {
            action.onResult(clientHealth);
        }
    }
}
