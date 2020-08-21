package com.example.application.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.application.Adapters.GymServiceAdapterA;
import com.example.application.Adapters.GymServiceAdapterB;
import com.example.application.Index;
import com.example.application.Models.GymDetails;
import com.example.application.Models.GymServiceDetails;
import com.example.application.Models.RatingModel;
import com.example.application.R;
import com.example.application.Utils.CommomDataArea;
import com.example.application.Utils.CommonCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.application.Utils.Constants.GYM_SERVICES;
import static com.example.application.Utils.Constants.RATING;

public class CompareGymActivity extends AppCompatActivity {
    ArrayList<GymDetails> comparelist;
    TextView nameA,nameB,addA,addB,conA,conB,ownerA,ownerB,webA,webB,title;
    RecyclerView recyclerViewA,recyclerViewB;
    RatingBar ratingA,ratingB;
    List <RatingModel> ratingList,ratingListB;
        TextView logout;
    Float rating=0.0f;
    Float rating2=0.0f;
    ImageView back;
    DatabaseReference databaseServicesA,databaseServicesB;
    List<GymServiceDetails> gymServiceDetailsListA=new ArrayList<>();
    List<GymServiceDetails> gymServiceDetailsListB=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_gym);
        comparelist=(ArrayList<GymDetails>) getIntent().getSerializableExtra("list");
        initViews();
        loadData();
        title.setText("Compare Gyms");
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });
    }

    private void loadData() {
        if(comparelist!=null && !comparelist.isEmpty() && comparelist.size()==2){
            nameA.setText(comparelist.get(0).getGymName());
            nameB.setText(comparelist.get(1).getGymName());
            addA.setText(comparelist.get(0).getGymAddress());
            addB.setText(comparelist.get(1).getGymAddress());
            conA.setText(comparelist.get(0).getContactNo());
            conB.setText(comparelist.get(1).getContactNo());
            ownerA.setText(comparelist.get(0).getContactPeson());
            ownerB.setText(comparelist.get(1).getContactPeson());
            webA.setText(comparelist.get(0).getWebsite());
            webB.setText(comparelist.get(1).getWebsite());
            databaseServicesA = FirebaseDatabase.getInstance().getReference(GYM_SERVICES).child(comparelist.get(0).getOwnerId());
            databaseServicesB = FirebaseDatabase.getInstance().getReference(GYM_SERVICES).child(comparelist.get(1).getOwnerId());
            databaseServicesA.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gymServiceDetailsListA.clear();
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        GymServiceDetails g=d.getValue(GymServiceDetails.class);
                        gymServiceDetailsListA.add(g);
                    }
                    if(!gymServiceDetailsListA.isEmpty()){
                        GymServiceAdapterA servicesRecyclerView=new GymServiceAdapterA(gymServiceDetailsListA,
                                CompareGymActivity.this, new CommonCallback() {
                            @Override
                            public void adapterPosition(int position) {
                                Intent intent=   new Intent(CompareGymActivity.this, ShowServiceActivity.class);
                                intent.putExtra("list", (Serializable) gymServiceDetailsListA.get(position));
                                startActivity(intent);
                            }

                            @Override
                            public void editPosition(int position) {

                            }
                        });
                        recyclerViewA.setAdapter(servicesRecyclerView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseServicesB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gymServiceDetailsListB.clear();
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        GymServiceDetails g=d.getValue(GymServiceDetails.class);
                        gymServiceDetailsListB.add(g);
                    }
                    if(!gymServiceDetailsListA.isEmpty()){
                        GymServiceAdapterB servicesRecyclerView=new GymServiceAdapterB(gymServiceDetailsListB,
                                CompareGymActivity.this, new CommonCallback() {
                            @Override
                            public void adapterPosition(int position) {
                                Intent intent=   new Intent(CompareGymActivity.this, ShowServiceActivity.class);
                                intent.putExtra("list", (Serializable) gymServiceDetailsListA.get(position));
                                startActivity(intent);
                            }

                            @Override
                            public void editPosition(int position) {

                            }
                        });
                        recyclerViewB.setAdapter(servicesRecyclerView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(RATING);
            Query query1 = rootRef.orderByChild("gymId").equalTo(comparelist.get(0).getId());
            Query query2 = rootRef.orderByChild("gymId").equalTo(comparelist.get(1).getId());
            ValueEventListener valueEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ratingList=new ArrayList<>();

                    if (dataSnapshot.exists()){
                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                            RatingModel g = d.getValue(RatingModel.class);
                            ratingList.add(g);
                             rating+=Float.parseFloat(g.getRating());
                        }
                        ratingA.setRating(rating/ratingList.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            ValueEventListener valueEventListener2=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ratingListB=new ArrayList<>();

                    if (dataSnapshot.exists()){
                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                            RatingModel g = d.getValue(RatingModel.class);
                            ratingListB.add(g);
                            rating2+=Float.parseFloat(g.getRating());
                        }
                        ratingB.setRating(rating2/ratingListB.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            query1.addListenerForSingleValueEvent(valueEventListener);
            query2.addListenerForSingleValueEvent(valueEventListener2);

        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommomDataArea.logOut(CompareGymActivity.this);
                startActivity(new Intent(CompareGymActivity.this, Index.class));
                finish();
            }
        });


    }

    private void initViews() {
       nameA=findViewById(R.id.nameA);
       nameB=findViewById(R.id.nameB);
        addA=findViewById(R.id.addA);
        addB=findViewById(R.id.addB);
        conA=findViewById(R.id.conA);
        conB=findViewById(R.id.conB);
        ownerA=findViewById(R.id.ownerA);
        ownerB=findViewById(R.id.ownerB);
        webA=findViewById(R.id.webA);
        webB=findViewById(R.id.webB);
        recyclerViewA=findViewById(R.id.recyclerA);
        recyclerViewB=findViewById(R.id.recyclerB);
        ratingA=findViewById(R.id.ratingA);
        ratingB=findViewById(R.id.ratingB);
        LayerDrawable stars = (LayerDrawable) ratingA.getProgressDrawable();
        LayerDrawable stars2 = (LayerDrawable) ratingB.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars2.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        recyclerViewA.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerViewB.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        title=findViewById(R.id.title);
        logout=findViewById(R.id.logout);
    }
}
