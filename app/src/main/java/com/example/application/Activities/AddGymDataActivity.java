package com.example.application.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application.Models.GymDetails;
import com.example.application.Models.GymOwnerData;
import com.example.application.Models.GymServiceDetails;
import com.example.application.R;
import com.example.application.Utils.DataProcessor;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.application.Utils.Constants.FIRST_LOGIN_OWNER;
import static com.example.application.Utils.Constants.GYM_DATA;
import static com.example.application.Utils.Constants.GYM_ID;
import static com.example.application.Utils.Constants.GYM_OWNER;
import static com.example.application.Utils.Constants.GYM_OWNER_ID;

public class AddGymDataActivity extends AppCompatActivity {
    private final static int PLACE_PICKER_REQUEST = 999;
    double latitude;
    double longitude;
    EditText gym_name,gym_address,coordinates,website,contactPerson,contactno,des;
    Button submit;
    DatabaseReference databaseGymData,databaseGymDataEdit;
    DataProcessor dataProcessor;
    String ownerId="";
    String from;
    List<GymDetails> myList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gym_data);
        dataProcessor=new DataProcessor(this);
        ownerId=dataProcessor.getStr(GYM_OWNER_ID);
        databaseGymData = FirebaseDatabase.getInstance().getReference(GYM_DATA).child(ownerId);

        initViews();
        Intent intent=getIntent();
         from=intent.getStringExtra("from");
        if(from.equals("E")){
          myList = (List<GymDetails>) getIntent().getSerializableExtra("listE");
            gym_name.setText(myList.get(0).getGymName());
            gym_address.setText(myList.get(0).getGymAddress());
            coordinates.setText(myList.get(0).getLattitude()+","+myList.get(0).getLongitude());
            website.setText(myList.get(0).getWebsite());
            contactPerson.setText(myList.get(0).getContactPeson());
            contactno.setText(myList.get(0).getContactNo());
            des.setText(myList.get(0).getDes());
            latitude=myList.get(0).getLattitude();
            longitude=myList.get(0).getLongitude();
            submit.setText("Update");
        }
    }

    private void initViews() {
        gym_name = findViewById(R.id.gym_name);
        gym_address = findViewById(R.id.gym_address);
        coordinates = findViewById(R.id.latling);
        website = findViewById(R.id.gym_url);
        contactPerson = findViewById(R.id.name);
        contactno = findViewById(R.id.contact_no);
        des = findViewById(R.id.des);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gym_name.getText().toString().length()==0){
                    gym_name.setError("Enter Gym Name");
                }else if(gym_address.getText().toString().length()==0){
                    gym_address.setError("Enter an Address");
                }else if(coordinates.getText().toString().length()==0){
                    coordinates.setError("Select a Place");
                }/*else if(website.getText().toString().length()==0){
                    website.setError("Enter a valid website URL");
                }*/else if(contactPerson.getText().toString().length()==0){
                    contactPerson.setError("Enter a Name");
                }else if(contactno.getText().toString().length()==0){
                    contactno.setError("Enter a contact Number");

                }else{

                    if (from.equals("A")){
                    //TODO:push to firebase
                    String id1=databaseGymData.push().getKey();
                    GymDetails gymDetails=new GymDetails(id1,ownerId,gym_name.getText().toString(),gym_address.getText().toString()
                            ,website.getText().toString(),
                            contactPerson.getText().toString(),contactno.getText().toString(),latitude,longitude,des.getText().toString());
                    databaseGymData.child(id1).setValue(gymDetails);
                    dataProcessor.setBool(FIRST_LOGIN_OWNER,true);
                    dataProcessor.setStr(GYM_ID,id1);
                    Toast.makeText(AddGymDataActivity.this, "Gym updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddGymDataActivity.this,GymOwnerProfileActivity.class));
                    finish();
                }else if(from.equals("E")){
                        databaseGymDataEdit = FirebaseDatabase.getInstance().getReference(GYM_DATA).
                                child(ownerId).child(myList.get(0).getId());
                        GymDetails gymDetails=new GymDetails(myList.get(0).getId(),ownerId,gym_name.getText().toString(),gym_address.getText().toString()
                                ,website.getText().toString(),
                                contactPerson.getText().toString(),contactno.getText().toString(),latitude,longitude,des.getText().toString());
                        databaseGymDataEdit.setValue(gymDetails);
                        finish();
                    }
                }
            }
        });
        coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    // for activty
                    startActivityForResult(builder.build(AddGymDataActivity.this), PLACE_PICKER_REQUEST);
                    // for fragment
                    //startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // checkPermissionOnActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    String placeName = String.format("Place: %s", place.getName());
                     latitude = place.getLatLng().latitude;
                     longitude = place.getLatLng().longitude;
                     coordinates.setText(latitude +","+longitude);

            }
        }
    }
}
