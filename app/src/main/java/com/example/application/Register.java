package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.Models.UserDetails;
import com.example.application.Utils.*;
import com.example.application.Models.GymOwnerData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;
import static com.example.application.Utils.Constants.ROLE;
import static com.example.application.Utils.Constants.USER_DETAILS;

public class Register extends AppCompatActivity {


    EditText fname, lname, phone, email, password, repassword;
    Button SignUp;
    DatabaseReference databaseGymOwner,databaseUser;
    int role;
    DataProcessor dataProcessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dataProcessor=new DataProcessor(this);
        databaseGymOwner = FirebaseDatabase.getInstance().getReference(GYM_OWNER);
        databaseUser = FirebaseDatabase.getInstance().getReference(USER_DETAILS);
        fname = findViewById(R.id.first);
        lname = findViewById(R.id.last);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        repassword.addTextChangedListener(mDateEntryWatcher);
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
                final String PasswordEntered = password.getText().toString();
                String ReEntered = repassword.getText().toString();


                if (TextUtils.isEmpty(FnameEntered)) {
                    fname.setError( "please enter first name");
                    //Toast.makeText(Register.this, "please enter first name", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(LnameEntered)) {
                    lname.setError("please enter Last name");
                    //Toast.makeText(Register.this, "please enter Last name", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(PhoneEntered) || PhoneEntered.length()<9 || !isValid(PhoneEntered) ) {
                    phone.setError("please enter a valid irish no");
                   // Toast.makeText(Register.this, "please enter a valid irish no", Toast.LENGTH_LONG).show();
                    return;
                }

                String emailPattern = "[a-zA-Z0-9_\\.+]+@(gmail|yahoo|hotmail)(\\.[a-z]{2,3}){1,2}";

                if (TextUtils.isEmpty(EmailEntered) || !EmailEntered.matches(emailPattern) ) {
                    email.setError("please enter a valid Email");
                    //Toast.makeText(Register.this, "please enter Email", Toast.LENGTH_LONG).show();
                    return;
                }


                if (TextUtils.isEmpty(PasswordEntered) || PasswordEntered.length()<8) {
                    password.setError("You must have 8 characters in your password");
                    //Toast.makeText(Register.this, "You must have 8 characters in your password", Toast.LENGTH_LONG).show();
                    return;
                }

               /* if (TextUtils.isEmpty(ReEntered)) {
                    repassword.setError( "please Re-enter Password");
                    //Toast.makeText(Register.this, "please Re-enter Password  ", Toast.LENGTH_LONG).show();
                    return;
                }
*/

                if (!PasswordEntered.equals(ReEntered)) {
                    Toast.makeText(Register.this, "password doesnt match", Toast.LENGTH_LONG).show();
                    return;
                }
                if(role==0){
                String id = databaseGymOwner.push().getKey();
                if (FnameEntered.length()>0 && LnameEntered .length()>0 && EmailEntered.length()>0 &&
                        PasswordEntered.length()>0&& PhoneEntered.length()>0) {
                             GymOwnerData gymOwnerData = new GymOwnerData(id, FnameEntered, LnameEntered,
                            EmailEntered, PasswordEntered, PhoneEntered,role);
                             databaseGymOwner.child(id).setValue(gymOwnerData);

                            // dataProcessor.setInt(ROLE,role);
                    Toast.makeText(Register.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }else if(role==1){
                    String id = databaseUser.push().getKey();
                    if (FnameEntered.length()>0 && LnameEntered .length()>0 && EmailEntered.length()>0 &&
                            PasswordEntered.length()>0&& PhoneEntered.length()>0) {
                        UserDetails userDetails = new UserDetails(id, FnameEntered, LnameEntered,
                                EmailEntered, PasswordEntered, PhoneEntered,role);
                        databaseUser.child(id).setValue(userDetails);

                        // dataProcessor.setInt(ROLE,role);
                        Toast.makeText(Register.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
    public static boolean isValid(String s)
    {
        String sub=s.substring(0,3);
       if(sub.equals("082") || sub.equals("084") || sub.equals("085")
       || sub.equals("086") || sub.equals("087")){
           return  true;
       }else{
           return  false;
       }


    }
    private TextWatcher mDateEntryWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            if(working.equals(password.getText().toString())){
                repassword.setTextColor(ContextCompat.getColor(Register.this, R.color.green));
            }else{
                repassword.setTextColor(ContextCompat.getColor(Register.this, R.color.red1));
            }

        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };
}
