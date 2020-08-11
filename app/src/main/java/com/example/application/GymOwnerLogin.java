package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Activities.AddGymDataActivity;
import com.example.application.Activities.GymOwnerProfileActivity;
import com.example.application.Activities.MapActivity;
import com.example.application.Models.GymDetails;
import com.example.application.Models.GymOwnerData;
import com.example.application.Models.UserDetails;
import com.example.application.Utils.DataProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.IS_LOGGEDIN;
import static com.example.application.Utils.Constants.ROLE;
import static com.example.application.Utils.Constants.USER_DETAILS;
import static com.example.application.Utils.Constants.USER_ID;

public class GymOwnerLogin extends AppCompatActivity {


    EditText Email, Password;
    Button SignIn;
    TextView SignUp;
    DatabaseReference databaseGymOwner,databaseUser,databaseGym;
    int role;
    DataProcessor dataProcessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseGymOwner = FirebaseDatabase.getInstance().getReference(GYM_OWNER);
        databaseUser = FirebaseDatabase.getInstance().getReference(USER_DETAILS);
        Email = findViewById(R.id.emailentered);
        Password = findViewById(R.id.passwordentered);
        SignUp = findViewById(R.id.signupbutton);
        SignIn = findViewById(R.id.signinbutton);
        dataProcessor=new DataProcessor(this);

        Intent mIntent = getIntent();
        role = mIntent.getIntExtra(ROLE, 0);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Email.getText().toString().length() == 0) {
                    Email.setError("Enter a valid Email");
                } else if (Password.getText().toString().length() == 0) {
                    Password.setError("Enter a valid Password");
                }else {
                    if(role==0)
                    firebaseLoginSession();
                    else if(role==1)
                        firebaseUserLogin();
                }
            }
        });


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(getApplicationContext(), Register.class);
                i.putExtra(ROLE,role);
                startActivity(i);

            }
        });
    }

    private void firebaseLoginSession() {
        Query query = databaseGymOwner.orderByChild("userName").equalTo(Email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        GymOwnerData usersBean = user.getValue(GymOwnerData.class);

                        if (usersBean.getPassword().equals(Password.getText().toString().trim())) {
                            checkForFirstUser(usersBean.getId(),usersBean);

                        } else {
                            Toast.makeText(GymOwnerLogin.this, "Password is wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(GymOwnerLogin.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    List<GymDetails> gymDetails=new ArrayList<>();
    boolean firstLoginOver=false;
    private void checkForFirstUser(String gymownerId, final GymOwnerData usersBean){
        databaseGym = FirebaseDatabase.getInstance().getReference(GYM_DATA).child(gymownerId);
        databaseGym.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gymDetails.clear();
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    GymDetails g=d.getValue(GymDetails.class);
                    gymDetails.add(g);
                }
                if(!gymDetails.isEmpty()){
                    firstLoginOver=true;
                }
                if(!firstLoginOver) {
                    dataProcessor.setInt(ROLE,usersBean.getRole());
                    dataProcessor.setStr(GYM_OWNER_ID,usersBean.getId());
                    dataProcessor.setBool(IS_LOGGEDIN,true);
                    Intent intent = new Intent(GymOwnerLogin.this, AddGymDataActivity.class);
                    startActivity(intent);

                }else{
                    dataProcessor.setInt(ROLE,usersBean.getRole());
                    dataProcessor.setStr(GYM_OWNER_ID,usersBean.getId());
                    dataProcessor.setBool(IS_LOGGEDIN,true);
                    Intent intent = new Intent(GymOwnerLogin.this, GymOwnerProfileActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void firebaseUserLogin(){
        Query query = databaseUser.orderByChild("userName").equalTo(Email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        UserDetails usersBean = user.getValue(UserDetails.class);

                        if (usersBean.getPassword().equals(Password.getText().toString().trim())) {

                                dataProcessor.setInt(ROLE,usersBean.getRole());
                                //dataProcessor.setStr(GYM_OWNER_ID,usersBean.getId());
                                dataProcessor.setBool(IS_LOGGEDIN,true);
                                dataProcessor.setStr(USER_ID,usersBean.getId());
                                Intent intent = new Intent(GymOwnerLogin.this, MapActivity.class);
                                startActivity(intent);


                        } else {
                            Toast.makeText(GymOwnerLogin.this, "Password is wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(GymOwnerLogin.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
