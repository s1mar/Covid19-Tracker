package com.s1mar.covid19tracker.users.clients;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.s1mar.covid19tracker.R;
import com.s1mar.covid19tracker.data.Constants;
import com.s1mar.covid19tracker.data.FireUsers;
import com.s1mar.covid19tracker.data.models.MUser;
import com.s1mar.covid19tracker.databinding.CustomerItemCardBinding;
import com.s1mar.covid19tracker.databinding.LayoutClientManagementBinding;

import com.s1mar.covid19tracker.utils.LoadingAnimationHelper;
import com.s1mar.covid19tracker.utils.NetworkUtils;
import com.s1mar.covid19tracker.utils.PlayerPrefs;
import com.s1mar.covid19tracker.utils.TextUtils;
import com.s1mar.covid19tracker.utils.Toaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Act_CustomerManagement extends AppCompatActivity {

    final static private String TAG = Act_CustomerManagement.class.getSimpleName();
    private LayoutClientManagementBinding mBinder;
    private Adapter mAdapter;
    private Toaster mToaster;
    private static final long  TIME_DELAY = 2000L;  private static final int TOAST_LENGTH = Toast.LENGTH_LONG;

    private MUser mUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = LayoutClientManagementBinding.inflate(getLayoutInflater());
        mUser = new Gson().fromJson(PlayerPrefs.getString(Act_CustomerManagement.this,"muser"),MUser.class);
        mAdapter = new Adapter(mUser);
        mBinder.recyclerView.setAdapter(mAdapter);
        initialization();
        mToaster = new Toaster(this);
        hookSaveButton();
        setContentView(mBinder.getRoot());
    }

    private void hookSaveButton(){
        mBinder.saveBtn.setOnClickListener(v->{
            try{

                if(!NetworkUtils.hasNetworkConnectivity(this)){
                    LoadingAnimationHelper.showMessage(this,"Please check your data and try again!");
                    LoadingAnimationHelper.dismissWithDelay(this,TIME_DELAY);
                    return;
                }

                //Commence update
                update();


            }catch (Exception ex){
                Log.e(TAG, "hookSaveButton: ",ex);
            }
        });
    }

    private void update(){


        HashMap<String,Integer> clientAndHealthDict = mAdapter.getClientData();

        //Set clients
        mUser.setClients(new ArrayList<>(clientAndHealthDict.keySet()));

        //Not an optimum way to update client infection status but for now it will do
        //TODO In the future extract the document reference when creating the client key and use it to update the client directly

        LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"Updating...");
        FireUsers.updateUser(mUser,actionResult->{
            LoadingAnimationHelper.dismissWithDelay(Act_CustomerManagement.this,TIME_DELAY);
            if(actionResult instanceof Boolean && (Boolean) actionResult){
                mToaster.showToast("Successfully Updated Clientele!",TOAST_LENGTH);
                PlayerPrefs.setString(Act_CustomerManagement.this,"muser",new Gson().toJson(mUser));

                //Now,let's update the clientele health data
              Iterator<Map.Entry<String,Integer>> iterator = clientAndHealthDict.entrySet().iterator();
              while (iterator.hasNext()){

                  Map.Entry<String ,Integer> entry = iterator.next();
                  String username = entry.getKey().split(";")[0];
                  FirebaseFirestore.getInstance().collection(Constants.USERS)
                          .whereEqualTo("username",username).get().addOnSuccessListener(queryDocumentSnapshots -> {
                              DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                              FirebaseFirestore.getInstance()
                                      .document(documentReference.getPath()).update("healthStatus",entry.getValue());
                          });
              }

            }
            else {
                mToaster.showToast("Update Failed.",TOAST_LENGTH);
            }

        });


    }

    private void initialization(){

        FirebaseFirestore.getInstance().collection(Constants.USERS).whereEqualTo("client",true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e==null){
                        if(queryDocumentSnapshots==null || queryDocumentSnapshots.isEmpty()){
                            Log.e(TAG, "onEvent: "+"No Clients found in the system" );
                            LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"It seems that there are yet no clients added into the system,\ntaking you back in about 2 seconds");
                            new Handler().postDelayed(()->{
                                LoadingAnimationHelper.dismiss(Act_CustomerManagement.this);
                                finish();
                            },TIME_DELAY);
                        }
                        else{
                                try{
                                    mAdapter.updateDataSet(queryDocumentSnapshots.toObjects(MUser.class));
                                }catch (Exception ex){
                                    Log.e(TAG, "onEvent: ",ex);
                                    LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"Something Went wrong, make sure you have data connection and then try again");
                                    new Handler().postDelayed(()->{
                                        LoadingAnimationHelper.dismiss(Act_CustomerManagement.this);
                                        finish();
                                    },TIME_DELAY);
                                }
                        }
                    }
                    else{
                        Log.e(TAG, "onEvent: ",e);
                        LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"Something Went wrong, make sure you have data connection and then try again");
                        new Handler().postDelayed(()->{
                            LoadingAnimationHelper.dismiss(Act_CustomerManagement.this);
                            finish();
                        },TIME_DELAY);
                    }
            }
        });
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private List<MUser> clients = new ArrayList<>(0);
        private HashMap<MUser,Boolean> Map_IsCheckedAsClient = new HashMap<>(0);
        private HashMap<MUser,Integer> Map_ClientHealth = new HashMap<>(0);

        private MUser user;

        Adapter(MUser user) {
            this.user = user;
        }

        private void updateDataSet(List<MUser> dataSet){
            if(dataSet!=null){
                clients.clear();
                clients.addAll(dataSet);

                //check the clients that this employee already contains
               if(user.getClients()!=null && !user.getClients().isEmpty() && !dataSet.isEmpty()){


                   for(String clientKey : user.getClients()){
                        String[] strArr = clientKey.split(";"); //username;user
                        String username = strArr[0];
                        String name = strArr[1];

                     for(MUser u : dataSet){

                            if(u.getUsername().equals(username) && u.getName().equals(name)){
                                Map_IsCheckedAsClient.put(u,true);
                            }
                     }

                   }
               }

                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_card,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MUser client = clients.get(position);
            holder.binder.txtName.setText(client.getName());
            String location = TextUtils.isStringEmpty(client.getCurrentLocation())?holder.itemView.getContext().getString(R.string.location_not_provided):client.getCurrentLocation();
            holder.binder.txtLocName.setText(location);
            Integer clientHealthStatus = client.getHealthStatus();

            clientHealthStatus = clientHealthStatus==null?0:clientHealthStatus;

            if(clientHealthStatus==0){
                holder.binder.green.setChecked(true);
            }
            else if(clientHealthStatus==1){
                holder.binder.yellow.setChecked(true);
            }
            else{
                holder.binder.red.setChecked(true);
            }

            if(Map_IsCheckedAsClient.containsKey(client) && Map_IsCheckedAsClient.get(client)!=null){
                    holder.binder.checkMyClient.setChecked(Map_IsCheckedAsClient.get(client));
                    holder.enableChangeHealthStatusView(true);
            }
            else {
                holder.enableChangeHealthStatusView(false);
            }

            holder.binder.checkMyClient.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    Map_IsCheckedAsClient.put(client,true);
                    holder.enableChangeHealthStatusView(true);
                }
                else{
                   Map_IsCheckedAsClient.remove(client);
                   Map_ClientHealth.remove(client); //Since it's a not client hence the privilege to change health data is also gone
                   holder.enableChangeHealthStatusView(false);
                }
            });

            holder.binder.healthStatusContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if(checkedId==R.id.green){
                        Map_ClientHealth.put(client,0);
                    }
                    else if(checkedId==R.id.yellow){
                        Map_ClientHealth.put(client,1);
                    }
                    else if(checkedId==R.id.red){
                        Map_ClientHealth.put(client,2);
                    }
                }
            });
        }

        @Override
        public void onViewRecycled(@NonNull ViewHolder holder) {
            super.onViewRecycled(holder);
            holder.binder.checkMyClient.setOnCheckedChangeListener(null);
        }

        HashMap<String,Integer> getClientData(){
            HashMap<String,Integer> clientAndHealthDataMap = new HashMap<>(0);
            if(Map_IsCheckedAsClient!=null && !Map_IsCheckedAsClient.isEmpty()){

                for(MUser user:Map_IsCheckedAsClient.keySet()){
                    String key = user.getUsername()+";"+user.getName();
                    Integer healthValue = Map_ClientHealth.get(user)!=null?Map_ClientHealth.get(user):0;
                    clientAndHealthDataMap.put(key,healthValue);
                }
            }

            return clientAndHealthDataMap;
        }

        @Override
        public int getItemCount() {
            return clients.size();
        }

        static class  ViewHolder extends RecyclerView.ViewHolder{
            private CustomerItemCardBinding binder;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                binder = CustomerItemCardBinding.bind(itemView);

            }

            void enableChangeHealthStatusView(boolean flag){
                int vFlag = flag?View.VISIBLE:View.GONE;
                binder.healthStatusContainer.setVisibility(vFlag);
            }
        }


    }


}
