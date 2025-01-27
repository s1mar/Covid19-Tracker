package com.s1mar.covid19tracker.users.clients;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Act_CustomerManagement extends AppCompatActivity {

    final static private String TAG = Act_CustomerManagement.class.getSimpleName();
    private LayoutClientManagementBinding mBinder;
    private Adapter mAdapter;
    private Toaster mToaster;
    private static final long  TIME_DELAY = 2000L;  private static final int TOAST_LENGTH = Toast.LENGTH_LONG;

    private MUser mUser;
    private int configValue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = LayoutClientManagementBinding.inflate(getLayoutInflater());
        mUser = new Gson().fromJson(PlayerPrefs.getString(Act_CustomerManagement.this,"muser"),MUser.class);

        mToaster = new Toaster(this);

        //process parcel
        configValue = getIntent().getIntExtra("config",0);
        mAdapter = new Adapter(mUser,configValue);
        mBinder.recyclerView.setAdapter(mAdapter);

        if(configValue==1){
            mBinder.headerText.setText(R.string.engineer);
            mBinder.saveBtn.setVisibility(View.GONE);

            try{

                mUser = getIntent().getParcelableExtra("parcel");
            }catch (Exception ex){
                Log.e(TAG, "onCreate: ", ex);
            }


            initializationCLient();
        }else {
            initialization();
            hookSaveButton();
        }
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

    @Deprecated
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
                mToaster.showToast("Successfully Updated Client!",TOAST_LENGTH);
                PlayerPrefs.setString(Act_CustomerManagement.this,"muser",new Gson().toJson(mUser));

                //Now,let's update the clientele health data
                for (Map.Entry<String, Integer> entry : clientAndHealthDict.entrySet()) {

                   /* if (entry.getValue() == null) {
                        continue;
                    }*/ //Since the value wasn't changed, I won't update it

                    Integer healthStatus = entry.getValue();
                    String username = entry.getKey().split(";")[0];
                    FirebaseFirestore.getInstance().collection(Constants.USERS)
                            .whereEqualTo("username", username).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        MUser userClient = queryDocumentSnapshots.getDocuments().get(0).toObject(MUser.class);

                        if(userClient!=null) {
                            if (healthStatus != null) {
                                userClient.setHealthStatus(healthStatus);
                            }

                        /*FirebaseFirestore.getInstance()
                                .document(documentReference.getPath()).update("healthStatus", entry.getValue());*/

                            userClient.addToAssignedEmployees(mUser.getUsername());

                            //Associate the client with its assigned employee
                            //FirebaseFirestore.getInstance().document(documentReference.getPath()).update("")
                            FirebaseFirestore.getInstance().document(documentReference.getPath()).set(userClient);
                        }
                            //TODO I've added the id func to the data model, these calls can now be reduced and optimized
                        //Unassociate all clients that are no longer associated with this employee
                        for(MUser userClientX : mAdapter.clientsThatNoLongerBelongToThisEmployee()){

                               FirebaseFirestore.getInstance().collection(Constants.USERS).
                                       whereEqualTo("username", userClientX.getUsername()).get().addOnSuccessListener(queryDocumentSnapshotsTwo -> {
                                   DocumentReference documentReferenceTwo = queryDocumentSnapshotsTwo.getDocuments().get(0).getReference();

                                   userClientX.removeFromAssignedEmployee(mUser.getUsername());
                                   FirebaseFirestore.getInstance().document(documentReferenceTwo.getPath()).set(userClientX);
                               });
                        }

                            });
                }

            }
            else {
                mToaster.showToast("Update Failed.",TOAST_LENGTH);
            }

        });


    }


    private void updateX(){


        if(!NetworkUtils.hasNetworkConnectivity(this))
        {
            LoadingAnimationHelper.showMessage(this,"Please ensure that you have data connectivity and try again!",TIME_DELAY);
            return;
        }



        //Clients who do not belong to the selected employee
        List<MUser> notClientsAnymore = mAdapter.clientsThatNoLongerBelongToThisEmployee();

        //Clients that belong
        HashMap<String,Integer> clientsUpdateWithHealth = mAdapter.getClientData();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                //Task 1; deal with clients
                if(clientsUpdateWithHealth!=null && !clientsUpdateWithHealth.isEmpty()){
                    //We have some updates to make
                    List<DocumentReference> clientsAsReference = new ArrayList<>(0);
                    for(String clientUsername :clientsUpdateWithHealth.keySet()){
                        mUser.addClient(clientUsername);
                        db.collection(Constants.USERS).whereEqualTo("username",clientUsername).get().getResult().getDocuments().get(0);
                    }






                    //Update the user with the client data
                    transaction.set(db.collection(Constants.USERS).document(mUser.getId()),mUser);

                    //Update the clients with assigned employees




                }

                //Task 2; deal with no longer clients

                return null;
            }
        });


    }

    private void initializationCLient(){


        if(mUser.getEmployeesAssigned()==null || mUser.getEmployeesAssigned().isEmpty()){
            LoadingAnimationHelper.showMessage(this,"No Employees Assigned");
            return;
        }

         FirebaseFirestore db = FirebaseFirestore.getInstance();
        Collection<Task<QuerySnapshot>> tasks  = new ArrayList<>(0);
         for(String username : mUser.getEmployeesAssigned()){
              tasks.add(db.collection(Constants.USERS).whereEqualTo("username",username).get());
         }

         LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"Initializing...");

         Tasks.whenAllSuccess(tasks).addOnSuccessListener(listUsers->{

             LoadingAnimationHelper.dismiss(Act_CustomerManagement.this);
            if(listUsers==null || listUsers.isEmpty()){
                LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"No Employees Assigned");
            }    else{


              List<MUser> dataSet = Stream.of(listUsers).map(new Function<Object, MUser>() {
                    @Override
                    public MUser apply(Object o) {
                       QuerySnapshot qs =  (QuerySnapshot)o;
                       return qs.getDocuments().get(0).toObject(MUser.class);

                    }
                }).toList();



            mAdapter.updateDataSet(dataSet);
            }

        }).addOnFailureListener(e->{
           LoadingAnimationHelper.showMessage(Act_CustomerManagement.this,"Something went wrong");
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
        private HashMap<String,Boolean> Map_IsCheckedAsClient = new HashMap<>(0);  //username,boolean
        private HashMap<String,Integer> Map_ClientHealth = new HashMap<>(0);      //username,health-status

        private MUser user;
        private int configValue;
        Adapter(MUser user,int configValue) {
            this.configValue = configValue;
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
                                Map_IsCheckedAsClient.put(username,true);
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

           if(configValue==1){

               MUser emp = clients.get(position);
               holder.binder.checkMyClient.setChecked(true);
               holder.binder.checkMyClient.setEnabled(false);
               holder.binder.txtName.setText(emp.getName());

               //String location = TextUtils.isStringEmpty(emp.getCurrentLocation())?holder.itemView.getContext().getString(R.string.location_not_provided):emp.getCurrentLocation();
               //holder.binder.txtLocName.setText(location);

               String location = TextUtils.isStringEmpty(emp.getCurrentLocation())?holder.itemView.getContext().getString(R.string.location_not_provided):emp.getCurrentLocation();
               location= "Location: "+ location +"\n";

               int healthStatus = emp.getHealthStatus()==null?0:emp.getHealthStatus();
               if(healthStatus==0){
                   holder.binder.green.setChecked(true);
               }
               else if(healthStatus == 1){
                   holder.binder.yellow.setChecked(true);
               }
               else if(healthStatus==2){
                   holder.binder.red.setChecked(true);
               }

               String onSite = emp.isOnSite()?"On-site":"Home";
               onSite =  "Working-From: "+onSite;

               String personalHealthStatus = "Normal";


               int colorHealthStatus = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_green);
               if(emp.getHealthStatus()!=null){
                   if(emp.getHealthStatus()==1){
                       personalHealthStatus = "Observation";
                       colorHealthStatus = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_orange);
                   }else if(emp.getHealthStatus() == 2){
                        personalHealthStatus = "Infected";
                        colorHealthStatus = Color.RED;
                   }
               }
               personalHealthStatus = "Personal Health: "+personalHealthStatus+"\n";


               int colorFamilyStatus = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_green);
               String familyHealthStatus = "Normal";

               if(emp.getFamilyHealthStatus()!=null){
                   if(emp.getFamilyHealthStatus()==1){
                       familyHealthStatus = "Observation";
                       colorFamilyStatus = ContextCompat.getColor(holder.itemView.getContext(),R.color.ak_orange);
                   }else if(emp.getFamilyHealthStatus() == 2){
                       familyHealthStatus = "Infected";
                       colorFamilyStatus = Color.RED;
                   }
               }
               familyHealthStatus="Family Health: "+familyHealthStatus+"\n";

               Spannable personalHealth = new SpannableString(personalHealthStatus);
               personalHealth.setSpan(new ForegroundColorSpan(colorHealthStatus),15,personalHealth.length()
                       , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

               Spannable familyHealth = new SpannableString(familyHealthStatus);
               familyHealth.setSpan(new ForegroundColorSpan(colorFamilyStatus),15,familyHealth.length()
                       , Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

              ;
               //Spannable span = new SpannableString(healthFinal);
               //span.setSpan(new ForegroundColorSpan(color), health.length(), healthFinal.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
               //String str = personalHealthStatus+"\n"+familyHealthStatus+"\n"+onSite;
               holder.binder.txtLocName.setText( android.text.TextUtils.concat(location,personalHealth,familyHealth,onSite));
               holder.binder.txtLocName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
               holder.binder.txtLocName.setMaxLines(5);
               holder.binder.txtLocName.setMaxEms(12);
               holder.binder.healthStatusContainer.setVisibility(View.GONE);
               holder.binder.red.setEnabled(false);
               holder.binder.green.setEnabled(false);
               holder.binder.yellow.setEnabled(false);
               holder.binder.healthStatusContainer.setActivated(false);
               holder.binder.healthStatusContainer.setEnabled(false);

               holder.binder.tickLabel.setVisibility(View.GONE);

               return;
           }



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

            if(Map_IsCheckedAsClient.containsKey(client.getUsername()) && Map_IsCheckedAsClient.get(client.getUsername())!=null){
                    holder.binder.checkMyClient.setChecked(Map_IsCheckedAsClient.get(client.getUsername()));
                    holder.enableChangeHealthStatusView(true);
            }
            else {
                holder.enableChangeHealthStatusView(false);
            }

            holder.binder.checkMyClient.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    Map_IsCheckedAsClient.put(client.getUsername(),true);
                    holder.enableChangeHealthStatusView(true);
                }
                else{
                   Map_IsCheckedAsClient.put(client.getUsername(),false);
                   Map_ClientHealth.remove(client.getUsername());//Since it's a not client hence the privilege to change health data is also gone
                   holder.enableChangeHealthStatusView(false);
                }
            });

            holder.binder.healthStatusContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if(checkedId==R.id.green){
                        Map_ClientHealth.put(client.getUsername(),0);
                    }
                    else if(checkedId==R.id.yellow){
                        Map_ClientHealth.put(client.getUsername(),1);
                    }
                    else if(checkedId==R.id.red){
                        Map_ClientHealth.put(client.getUsername(),2);
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
               Stream<MUser> userStream = Stream.of(clients);
               List<MUser> relevantUsers = new ArrayList<>(0);
               List<String> clientsChecked = Stream.of(Map_IsCheckedAsClient.keySet()).filter(value -> {
                    return Map_IsCheckedAsClient.get(value)!=null && Map_IsCheckedAsClient.get(value);
               }).toList();
                userStream.close();

               for(String userName: clientsChecked)
               {
                   MUser user = Stream.of(clients).filter(new Predicate<MUser>() {
                       @Override
                       public boolean test(MUser value) {
                           return value.getUsername().equals(userName);
                       }
                   }).findFirst().orElse(null);



                   if(user!=null){
                       relevantUsers.add(user);}

               }


                for(MUser user:relevantUsers){
                    String key = user.getUsername()+";"+user.getName();
                    //String key = user.getUsername();
                    Integer healthValue = Map_ClientHealth.get(user.getUsername());  //If this sends NUll value, that means the state of that client wasn't changed
                    clientAndHealthDataMap.put(key,healthValue);
                }
            }

            return clientAndHealthDataMap;
        }

        List<MUser> clientsThatNoLongerBelongToThisEmployee(){

            List<MUser> usersNotClientsAnymore = new ArrayList<>(0);
            Stream<MUser> userStream = Stream.of(clients);

            if(Map_IsCheckedAsClient!=null && !Map_IsCheckedAsClient.isEmpty()) {
                for(Map.Entry<String,Boolean> entry : Map_IsCheckedAsClient.entrySet()){
                        if(entry.getValue()!=null && !entry.getValue()){
                            MUser userRec = userStream.filter(new Predicate<MUser>() {
                                @Override
                                public boolean test(MUser value) {
                                    return value.getUsername().equals(entry.getKey());
                                }
                            }).findFirst().orElse(null);
                            if(userRec!=null){usersNotClientsAnymore.add(userRec);}
                    }
                }

            }
            userStream.close();
                return usersNotClientsAnymore;
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
