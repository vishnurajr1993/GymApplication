package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.application.Utils.Constants.ROLE;

public class Index extends AppCompatActivity {


    Button cust,owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index2);



        cust=findViewById(R.id.customer);
        owner =findViewById(R.id.owner);

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Owner.class);
                getIntent().putExtra(ROLE,0);
                startActivity(i);


            }
        });

      owner.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {


              Intent j = new Intent(getApplicationContext(),GymOwnerLogin.class);
              getIntent().putExtra(ROLE,0);
              startActivity(j);
          }
      });
    }
}
