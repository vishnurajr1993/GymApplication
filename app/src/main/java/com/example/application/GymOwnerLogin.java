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
import com.example.application.Models.GymOwnerData;
import com.example.application.Utils.DataProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.application.Utils.Constants.FIRST_LOGIN_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.ROLE;

public class GymOwnerLogin extends AppCompatActivity {


    EditText Email, Password;
    Button SignIn;
    TextView SignUp;
    DatabaseReference databaseGymOwner;
    int role;
    DataProcessor dataProcessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseGymOwner = FirebaseDatabase.getInstance().getReference(GYM_OWNER);
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
                    firebaseLoginSession();
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
                            if(dataProcessor.getBool(FIRST_LOGIN_OWNER)) {
                                Intent intent = new Intent(GymOwnerLogin.this, AddGymDataActivity.class);
                                startActivity(intent);
                                DataProcessor.setInt(ROLE,usersBean.getRole());
                            }else{
                                Intent intent = new Intent(GymOwnerLogin.this, GymOwnerProfileActivity.class);
                                startActivity(intent);
                                dataProcessor.setInt(ROLE,usersBean.getRole());
                            }
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
