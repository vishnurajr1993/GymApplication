package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Activities.AddGymDataActivity;
import com.example.application.Activities.ForgetPasswordActivity;
import com.example.application.Activities.GymOwnerProfileActivity;
import com.example.application.Activities.MapActivity;
import com.example.application.Models.GymDetails;
import com.example.application.Models.GymOwnerData;
import com.example.application.Models.UserDetails;
import com.example.application.Utils.AESCrypt;
import com.example.application.Utils.DataProcessor;
import com.example.application.Utils.SendMail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.IS_LOGGEDIN;
import static com.example.application.Utils.Constants.ROLE;
import static com.example.application.Utils.Constants.TOKEN;
import static com.example.application.Utils.Constants.USER_DETAILS;
import static com.example.application.Utils.Constants.USER_ID;
import static com.example.application.Utils.Constants.USER_NAME;

public class GymOwnerLogin extends AppCompatActivity {


    EditText Email, Password;
    Button SignIn;
    TextView SignUp,forget;
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
        forget = findViewById(R.id.forget);
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

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role==1)
                    customerPasswordReset();
                else
                    ownerPasswordReset();
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

    private void customerPasswordReset() {

    }
private void ownerPasswordReset(){
    Random r = new Random();
    String randomNumber = String.format("%04d", r.nextInt(1001));
    dataProcessor.setStr(TOKEN,randomNumber);
    if (Email.getText().toString().length() == 0) {
        Email.setError("Enter a valid Email");
    }else{
        sendEmail("Saurabh's Gym App Password Reset Token",
                "Hi, \n \nYou recently requested to change your password for " +
                "Saurabh's Gym App.And the token for resetting your password is " +
                        ""+ Html.fromHtml("<font><b>" + randomNumber + "</b></font>"
                ));
        Intent intent=new Intent(GymOwnerLogin.this, ForgetPasswordActivity.class);
        intent.putExtra(ROLE,role);
        intent.putExtra(USER_NAME,Email.getText().toString());
        startActivity(intent);
    }

}
    private void sendEmail(String subject,String message) {
        //Getting content for email
        String email = Email.getText().toString().trim();


        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }
    private void firebaseLoginSession() {
        dialog = new ProgressDialog(GymOwnerLogin.this);
        dialog.setMessage("Please wait.");
        dialog.show();
        Query query = databaseGymOwner.orderByChild("userName").equalTo(Email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        GymOwnerData usersBean = user.getValue(GymOwnerData.class);
                        try {
                            String pwd= AESCrypt.decrypt(usersBean.getPassword());

                        if (pwd.equals(Password.getText().toString().trim())) {
                            checkForFirstUser(usersBean.getId(),usersBean);

                        } else {
                            Toast.makeText(GymOwnerLogin.this, "Password is wrong", Toast.LENGTH_LONG).show();
                        }
                        } catch (Exception e) {
                            Toast.makeText(GymOwnerLogin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(GymOwnerLogin.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (dialog.isShowing())
                    dialog.dismiss();
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
                    intent.putExtra("from","A");
                    startActivity(intent);
                    finish();
                }else{
                    dataProcessor.setInt(ROLE,usersBean.getRole());
                    dataProcessor.setStr(GYM_OWNER_ID,usersBean.getId());
                    dataProcessor.setBool(IS_LOGGEDIN,true);
                    Intent intent = new Intent(GymOwnerLogin.this, GymOwnerProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ProgressDialog dialog;
    private void firebaseUserLogin(){
        dialog = new ProgressDialog(GymOwnerLogin.this);
        dialog.setMessage("Please wait.");
        dialog.show();
        Query query = databaseUser.orderByChild("userName").equalTo(Email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dialog.isShowing())
                    dialog.dismiss();
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        UserDetails usersBean = user.getValue(UserDetails.class);
                        try {
                            String pwd= AESCrypt.decrypt(usersBean.getPassword());

                        if (pwd.equals(Password.getText().toString().trim())) {

                                dataProcessor.setInt(ROLE,usersBean.getRole());
                                //dataProcessor.setStr(GYM_OWNER_ID,usersBean.getId());
                                dataProcessor.setBool(IS_LOGGEDIN,true);
                                dataProcessor.setStr(USER_ID,usersBean.getId());
                                Intent intent = new Intent(GymOwnerLogin.this, MapActivity.class);
                                startActivity(intent);
                                finish();

                        } else {
                            Toast.makeText(GymOwnerLogin.this, "Password is wrong", Toast.LENGTH_LONG).show();
                        }
                        } catch (Exception e) {
                            Toast.makeText(GymOwnerLogin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(GymOwnerLogin.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }
}
