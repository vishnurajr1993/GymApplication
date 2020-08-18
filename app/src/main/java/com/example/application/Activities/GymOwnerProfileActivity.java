package com.example.application.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.application.Dialogs.AddGymServicesDialog;
import com.example.application.Models.GymDetails;
import com.example.application.Models.GymServiceDetails;
import com.example.application.Models.RatingModel;
import com.example.application.R;
import com.example.application.Utils.CommomDataArea;
import com.example.application.Utils.CommonCallback;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_ID;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.GYM_SERVICES;
import static com.example.application.Utils.Constants.RATING;
import static com.example.application.Utils.Constants.ROLE;

public class GymOwnerProfileActivity extends AppCompatActivity {
    DatabaseReference databaseGym, databaseServices;
    DataProcessor dp;
    List<GymDetails> gymDetails;
    List<GymServiceDetails> gymServiceDetailsList;
    TextView name, address, contactno, url, des;
    LinearLayout desContainer;
    RecyclerView recycleListView;
    FloatingActionButton fab;
    String gymownerId;
    String gymId;
    RatingBar ratingBar;
    int role;
    TextView title;
    Float rating = 0.0f;
    List<RatingModel> ratingList;
    ImageView logout, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_owner_profile);
        dp = new DataProcessor(this);
        role = dp.getInt(ROLE);
        if (role == 0) {
            gymownerId = dp.getStr(GYM_OWNER_ID);
            gymId = dp.getStr(GYM_ID);
        } else {
            Intent intent = getIntent();
            gymownerId = intent.getStringExtra(GYM_OWNER_ID);
            gymId = intent.getStringExtra(GYM_ID);
        }
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        logout = findViewById(R.id.logout);
        title.setText("Gym Owner Profole");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommomDataArea.logOut(GymOwnerProfileActivity.this);
                finish();
            }
        });
        gymDetails = new ArrayList<>();
        databaseGym = FirebaseDatabase.getInstance().getReference(GYM_DATA).child(gymownerId);
        databaseServices = FirebaseDatabase.getInstance().getReference(GYM_SERVICES).child(gymId);
        initviews();
        loadRating();
        databaseGym.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gymDetails.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    GymDetails g = d.getValue(GymDetails.class);
                    gymDetails.add(g);
                }
                if (!gymDetails.isEmpty()) {
                    name.setText(gymDetails.get(0).getGymName());
                    address.setText(gymDetails.get(0).getGymAddress());
                    contactno.setText(gymDetails.get(0).getContactNo());
                    if (gymDetails.get(0).getWebsite() != null)
                        url.setText(gymDetails.get(0).getWebsite());
                    if (!gymDetails.get(0).getDes().equals("")) {
                        desContainer.setVisibility(View.VISIBLE);
                        des.setText(gymDetails.get(0).getDes());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gymServiceDetailsList = new ArrayList<>();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gymServiceDetailsList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    GymServiceDetails g = d.getValue(GymServiceDetails.class);
                    gymServiceDetailsList.add(g);
                }
                if (!gymServiceDetailsList.isEmpty()) {
                    ServicesRecyclerView servicesRecyclerView = new ServicesRecyclerView(gymServiceDetailsList,
                            GymOwnerProfileActivity.this, new CommonCallback() {
                        @Override
                        public void adapterPosition(int position) {
                            Intent intent = new Intent(GymOwnerProfileActivity.this, ShowServiceActivity.class);
                            intent.putExtra("list", (Serializable) gymServiceDetailsList.get(position));
                            startActivity(intent);
                        }

                        @Override
                        public void editPosition(int position) {
                            Intent intent=  new Intent(GymOwnerProfileActivity.this, AddGymServicesDialog.class);
                            intent.putExtra("from","E");
                            intent.putExtra("list", (Serializable) gymServiceDetailsList.get(position));
                            startActivity(intent);
                        }
                    });
                    recycleListView.setAdapter(servicesRecyclerView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void initviews() {
        name = findViewById(R.id.name);
        ratingBar = findViewById(R.id.ratingBar);
        address = findViewById(R.id.address);
        contactno = findViewById(R.id.contactno);
        url = findViewById(R.id.url);
        fab = findViewById(R.id.fab);
        des = findViewById(R.id.des);
        desContainer = findViewById(R.id.desC);
        recycleListView = findViewById(R.id.recyclerView);
        recycleListView.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingInPixels = 25;
        recycleListView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        if (role == 1) {
            fab.setVisibility(View.GONE);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GymOwnerProfileActivity.this, AddGymDataActivity.class);
                intent.putExtra("listE", (Serializable) gymDetails);
                intent.putExtra("from", "E");
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=  new Intent(GymOwnerProfileActivity.this, AddGymServicesDialog.class);
              intent.putExtra("from","A");
                startActivity(intent);
            }
        });

    }

    public void loadRating() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(RATING);
        Query query1 = rootRef.orderByChild("gymId").equalTo(gymId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                        RatingModel g = d.getValue(RatingModel.class);
                        ratingList.add(g);
                        rating += Float.parseFloat(g.getRating());
                    }
                    ratingBar.setRating(rating / ratingList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query1.addListenerForSingleValueEvent(valueEventListener);
    }
}
