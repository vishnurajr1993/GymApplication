package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.application.Utils.*;
import com.example.application.Models.GymOwnerData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.ROLE;

public class Register extends AppCompatActivity {


    EditText fname, lname, phone, email, password, repassword;
    Button SignUp;
    DatabaseReference databaseGymOwner;
    int role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseGymOwner = FirebaseDatabase.getInstance().getReference(GYM_OWNER);
        fname = findViewById(R.id.first);
        lname = findViewById(R.id.last);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        Intent mIntent = getIntent();
        role = mIntent.getIntExtra(ROLE, 0);
        SignUp = findViewById(R.id.signupbutton1);


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String FnameEntered = fname.getText().toString();
                String LnameEntered = lname.getText().toString();
                String PhoneEntered = phone.getText().toString();
                String EmailEntered = email.getText().toString();
                String PasswordEntered = password.getText().toString();
                String ReEntered = repassword.getText().toString();


                if (TextUtils.isEmpty(FnameEntered)) {
                    Toast.makeText(Register.this, "please enter first name", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(LnameEntered)) {
                    Toast.makeText(Register.this, "please enter Last name", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(PhoneEntered)) {
                    Toast.makeText(Register.this, "please enter phone ", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(EmailEntered)) {
                    Toast.makeText(Register.this, "please enter Email", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(PasswordEntered)) {
                    Toast.makeText(Register.this, "please enter Password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(ReEntered)) {
                    Toast.makeText(Register.this, "please Re-enter Password  ", Toast.LENGTH_LONG).show();
                    return;
                }


                if (!PasswordEntered.equals(ReEntered)) {
                    Toast.makeText(Register.this, "password doesnt match", Toast.LENGTH_LONG).show();
                    return;
                }

                String id = databaseGymOwner.push().getKey();
                if (FnameEntered.length()>0 && LnameEntered .length()>0 && EmailEntered.length()>0 &&
                        PasswordEntered.length()>0&& PhoneEntered.length()>0) {
                             GymOwnerData gymOwnerData = new GymOwnerData(id, FnameEntered, LnameEntered,
                            EmailEntered, PasswordEntered, PhoneEntered,role);
                             databaseGymOwner.child(id).setValue(gymOwnerData);
                    Toast.makeText(Register.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
