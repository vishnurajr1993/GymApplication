package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.application.Activities.AddGymDataActivity;
import com.example.application.Activities.GymOwnerProfileActivity;
import com.example.application.Activities.MapActivity;
import com.example.application.Models.GymDetails;
import com.example.application.Utils.DataProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.IS_LOGGEDIN;
import static com.example.application.Utils.Constants.ROLE;

public class Index extends AppCompatActivity {

    DataProcessor dp;
    Button cust,owner;
    DatabaseReference databaseGym;
    List<GymDetails> gymDetails;
    boolean firstLoginOver=false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index2);
        dp=new DataProcessor(this);
        String gymownerId=dp.getStr(GYM_OWNER_ID);
        final boolean isLogedIn=dp.getBool(IS_LOGGEDIN);
        final int role=dp.getInt(ROLE);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait.");
        dialog.show();
        gymDetails=new ArrayList<>();
        if(role==0) {
            databaseGym = FirebaseDatabase.getInstance().getReference(GYM_DATA).child(gymownerId);
            databaseGym.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gymDetails.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        GymDetails g = d.getValue(GymDetails.class);
                        gymDetails.add(g);
                    }
                    if (!gymDetails.isEmpty()) {
                        firstLoginOver = true;
                    }
                    if (isLogedIn && firstLoginOver) {
                        startActivity(new Intent(Index.this, GymOwnerProfileActivity.class));
                        finish();
                    } else if (isLogedIn && !firstLoginOver) {
                        startActivity(new Intent(Index.this, AddGymDataActivity.class));
                        finish();
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else if(role==1){
            if(dp.getBool(IS_LOGGEDIN)){
                startActivity(new Intent(this, MapActivity.class));
                finish();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }else{
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }


        cust=findViewById(R.id.customer);
        owner =findViewById(R.id.owner);

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), GymOwnerLogin.class);
                i.putExtra(ROLE,1);
                startActivity(i);
                finish();

            }
        });

      owner.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {


              Intent j = new Intent(getApplicationContext(),GymOwnerLogin.class);
             j.putExtra(ROLE,0);
              startActivity(j);
              finish();
          }
      });
    }
}
