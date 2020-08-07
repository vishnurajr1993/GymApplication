package com.example.application.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.application.Dialogs.AddGymServicesDialog;
import com.example.application.Models.GymDetails;
import com.example.application.Models.GymServiceDetails;
import com.example.application.R;
import com.example.application.Utils.DataProcessor;
import com.example.application.Utils.ServicesRecyclerView;
import com.example.application.Utils.SpacesItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_ID;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.GYM_SERVICES;

public class GymOwnerProfileActivity extends AppCompatActivity {
    DatabaseReference databaseGym,databaseServices;
    DataProcessor dp;
    List <GymDetails> gymDetails;
    List <GymServiceDetails> gymServiceDetailsList;
    TextView name,address,contactno,url,des;
    LinearLayout desContainer;
   RecyclerView recycleListView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_owner_profile);
        dp=new DataProcessor(this);
        final String gymownerId=dp.getStr(GYM_OWNER_ID);
        final String gymId=dp.getStr(GYM_ID);
        gymDetails=new ArrayList<>();
        databaseGym = FirebaseDatabase.getInstance().getReference(GYM_DATA).child(gymownerId);
        databaseServices = FirebaseDatabase.getInstance().getReference(GYM_SERVICES).child(gymId);
        initviews();
       databaseGym.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               gymDetails.clear();
               for(DataSnapshot d:dataSnapshot.getChildren()){
                   GymDetails g=d.getValue(GymDetails.class);
                   gymDetails.add(g);
               }
               if(!gymDetails.isEmpty()){
                   name.setText(gymDetails.get(0).getGymName());
                   address.setText(gymDetails.get(0).getGymAddress());
                   contactno.setText(gymDetails.get(0).getContactNo());
                   url.setText(gymDetails.get(0).getWebsite());
                   if(!gymDetails.get(0).getDes().equals("")) {
                       desContainer.setVisibility(View.VISIBLE);
                       des.setText(gymDetails.get(0).getDes());
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

        gymServiceDetailsList=new ArrayList<>();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gymServiceDetailsList.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    GymServiceDetails g=d.getValue(GymServiceDetails.class);
                    gymServiceDetailsList.add(g);
                }
                if(!gymServiceDetailsList.isEmpty()){
                    ServicesRecyclerView servicesRecyclerView=new ServicesRecyclerView(gymServiceDetailsList,
                            GymOwnerProfileActivity.this,getContentResolver());
                    recycleListView.setAdapter(servicesRecyclerView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initviews() {
        name=findViewById(R.id.name);
        address=findViewById(R.id.address);
        contactno=findViewById(R.id.contactno);
        url=findViewById(R.id.url);
        fab=findViewById(R.id.fab);
        des=findViewById(R.id.des);
        desContainer=findViewById(R.id.desC);
        recycleListView=findViewById(R.id.recyclerView);
        recycleListView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingInPixels = 25;
        recycleListView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(GymOwnerProfileActivity.this, AddGymServicesDialog.class));
            }
        });
    }
}
