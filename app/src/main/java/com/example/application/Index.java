package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
     int role;
    boolean isLogedIn;
    String gymownerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index2);
        dp=new DataProcessor(this);
         gymownerId=dp.getStr(GYM_OWNER_ID);
          isLogedIn=dp.getBool(IS_LOGGEDIN);
        role=dp.getInt(ROLE);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait.");
        dialog.show();
        gymDetails=new ArrayList<>();
        permissions();
        //logIn();
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

    private void permissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            logIn();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Index.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void logIn() {
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
                        Intent intent=new Intent(Index.this, AddGymDataActivity.class);

                        intent.putExtra("from","A");
                        startActivity(intent);
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
    }
}
