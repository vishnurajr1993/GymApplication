package com.example.application.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.GymOwnerLogin;
import com.example.application.Models.GymOwnerData;
import com.example.application.Models.UserDetails;
import com.example.application.R;
import com.example.application.Utils.AESCrypt;
import com.example.application.Utils.DataProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.ROLE;
import static com.example.application.Utils.Constants.TOKEN;
import static com.example.application.Utils.Constants.USER_DETAILS;
import static com.example.application.Utils.Constants.USER_NAME;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText token,pwd,rpwd;
    Button update;
    int role=3;
    DatabaseReference databaseGymOwner,databaseUser;
    ProgressDialog dialog;
    DataProcessor dp;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        token=findViewById(R.id.token);
        pwd=findViewById(R.id.pwd);
        rpwd=findViewById(R.id.rpwd);

        update=findViewById(R.id.update);
        dp=new DataProcessor(this);
        Intent intent=getIntent();
        role=intent.getIntExtra(ROLE,3);
        username=intent.getStringExtra(USER_NAME);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role==3)
                    Toast.makeText(ForgetPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                else if(role==0)
                    updateOwnerpwd();
                else
                    updateUserpwd();
            }
        });
    }

    private void updateOwnerpwd() {
        String pwdEntered=pwd.getText().toString();
        if(token.getText().toString().equals("") || token.getText().toString().length()==0){
            token.setError("Enter token");
        }


        else if (!specailCharPatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!UpperCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!lowerCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!digitCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (TextUtils.isEmpty(pwdEntered) || pwdEntered.length()<8) {
            pwd.setError(warning);
            //Toast.makeText(Register.this, "You must have 8 characters in your pwd", Toast.LENGTH_LONG).show();

        }
        else if(!pwd.getText().toString().equals(rpwd.getText().toString())){
            rpwd.setError("assword doesn't match");
        }
        else{
            if(token.getText().toString().equals(dp.getStr(TOKEN))){
                firebaseSession();
            }else{
                Toast.makeText(ForgetPasswordActivity.this, "invalid token", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseSession() {
        dialog = new ProgressDialog(ForgetPasswordActivity.this);
        dialog.setMessage("Please wait.");
        dialog.show();
        databaseGymOwner = FirebaseDatabase.getInstance().getReference(GYM_OWNER);
        Query query = databaseGymOwner.orderByChild("userName").equalTo(username);
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
                        String id=usersBean.getId();
                        try {
                            String pwd= AESCrypt.encrypt(rpwd.getText().toString());

                        databaseGymOwner.child(id).child("password").setValue(pwd);
                            Toast.makeText(ForgetPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
                            dp.setStr(TOKEN,"");
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "User not exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }
    Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
    String warning="Your password must include at least 8 characters " +
            "a mixture of Upper and Lower case characters including at least 1 number and " +
            "special character";
    private void updateUserpwd() {
        String pwdEntered=pwd.getText().toString();
        if(token.getText().toString().equals("") || token.getText().toString().length()==0){
            token.setError("Enter token");
        }
       

        else if (!specailCharPatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!UpperCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!lowerCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (!digitCasePatten.matcher(pwdEntered).find()) {
            pwd.setError(warning);

        }
        else if (TextUtils.isEmpty(pwdEntered) || pwdEntered.length()<8) {
            pwd.setError(warning);
            //Toast.makeText(Register.this, "You must have 8 characters in your pwd", Toast.LENGTH_LONG).show();

        }
        else if(!pwd.getText().toString().equals(rpwd.getText().toString())){
            rpwd.setError("assword doesn't match");
        }
        else{
            if(token.getText().toString().equals(dp.getStr(TOKEN))){
                firebaseSessionUser();
            }else{
                Toast.makeText(ForgetPasswordActivity.this, "invalid token", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseSessionUser() {
        dialog = new ProgressDialog(ForgetPasswordActivity.this);
        dialog.setMessage("Please wait.");
        dialog.show();
        databaseUser = FirebaseDatabase.getInstance().getReference(USER_DETAILS);
        Query query = databaseUser.orderByChild("userName").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0

                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        UserDetails usersBean = user.getValue(UserDetails.class);
                        String id=usersBean.getId();
                        try {
                            String pwd= AESCrypt.encrypt(rpwd.getText().toString());

                            databaseUser.child(id).child("password").setValue(pwd);
                            Toast.makeText(ForgetPasswordActivity.this, "Password Updated", Toast.LENGTH_LONG).show();
                            dp.setStr(TOKEN,"");
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "User not exists", Toast.LENGTH_LONG).show();
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
