package com.csse.passengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.csse.passengerapp.common.CommonConstants;
import com.csse.passengerapp.model.Passengers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ForeignLogin extends AppCompatActivity {

    EditText editTextName, editTextPassport, editTextCountry;
    Button btnSubmit;
    ProgressBar progressBar;
    Passengers passenger;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_login);

        passenger =new Passengers();
        progressBar = findViewById(R.id.progressBar3);
        editTextName = findViewById(R.id.et_foriName);
        editTextPassport = findViewById(R.id.et_passport);
        editTextCountry = findViewById(R.id.et_country);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForeignLogin.this, LocalHome.class);
                String strName = editTextName.getText().toString();
                String strPassport = editTextPassport.getText().toString();
                String strCountry = editTextCountry.getText().toString();

                if(strName.isEmpty()){
                    editTextName.setError(CommonConstants.VALIDATION_NAME);
                    editTextName.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_NAME, Toast.LENGTH_SHORT).show();
                }
                else if(strPassport.isEmpty()){
                    editTextPassport.setError(CommonConstants.VALIDATION_PASSPORT);
                    editTextPassport.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_PASSPORT, Toast.LENGTH_SHORT).show();
                }
                else if(strCountry.isEmpty()){
                    editTextCountry.setError(CommonConstants.VALIDATION_COUNTRY);
                    editTextCountry.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_COUNTRY, Toast.LENGTH_SHORT).show();
                }
                else {

                    progressBar.setVisibility(View.VISIBLE);
                    db = FirebaseFirestore.getInstance();
                    //dbRef = FirebaseDatabase.getInstance().getReference().child("Passengers");
                    try {
                        passenger.setPassengerName(editTextName.getText().toString().trim());
                        passenger.setPassengerAddress(editTextCountry.getText().toString().trim());
                        passenger.setPassengerCreditLimit(0.0);
                        passenger.setPassengerNIC(editTextPassport.getText().toString().trim());
                        passenger.setPassengerType(CommonConstants.PASSENGER_TYPE_FOREIGN);

                        //get current date
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                        String  date = dateFormat.format(calendar.getTime());
                        passenger.setRegisteredDate(date);

                        registerPassenger(passenger);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //register passenger and intent to the home page
    private void registerPassenger(Passengers passenger){
        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .add(passenger)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(ForeignLogin.this, LocalHome.class).putExtra(CommonConstants.INTENT_NIC, passenger.getPassengerNIC()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Error! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}