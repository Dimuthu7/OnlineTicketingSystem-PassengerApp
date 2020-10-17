package com.csse.passengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.csse.passengerapp.common.CommonConstants;
import com.csse.passengerapp.model.Passengers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Register extends AppCompatActivity {
    EditText editTextName, editTextAddress, editTextMobile, editTextNic, editTextpwd;
    Button btnSubmit;
    TextView textViewLogin;
    ProgressBar progressBar;
    Passengers passenger;
//    DatabaseReference dbRef;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        passenger =new Passengers();
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.tv_goLogin);
        editTextName = findViewById(R.id.et_name);
        editTextAddress = findViewById(R.id.et_address);
        editTextMobile = findViewById(R.id.et_mobile);
        editTextNic = findViewById(R.id.et_foriName);
        editTextpwd = findViewById(R.id.et_passport);
        btnSubmit = findViewById(R.id.btn_submit);

        fnClearInput();
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = editTextName.getText().toString();
                String strAddress = editTextAddress.getText().toString();
                String strMobile = editTextMobile.getText().toString();
                String strNic = editTextNic.getText().toString();
                String strPwd = editTextpwd.getText().toString();

                if(strName.isEmpty()){
                    editTextName.setError(CommonConstants.VALIDATION_NAME);
                    editTextName.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_NAME, Toast.LENGTH_SHORT).show();
                }
                else if(strAddress.isEmpty()){
                    editTextAddress.setError(CommonConstants.VALIDATION_ADDRESS);
                    editTextAddress.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_ADDRESS, Toast.LENGTH_SHORT).show();
                }
                else if(strMobile.isEmpty()){
                    editTextMobile.setError(CommonConstants.VALIDATION_MOBILE);
                    editTextMobile.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_MOBILE, Toast.LENGTH_SHORT).show();
                }
                else if(strNic.isEmpty()){
                    editTextNic.setError(CommonConstants.VALIDATION_NIC);
                    editTextNic.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_NIC, Toast.LENGTH_SHORT).show();
                }
                else if(strPwd.isEmpty()){
                    editTextpwd.setError(CommonConstants.VALIDATION_PASSWORD);
                    editTextpwd.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_PASSWORD, Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    db = FirebaseFirestore.getInstance();

                    //dbRef = FirebaseDatabase.getInstance().getReference().child("Passengers");
                    try {
                        db.collection(CommonConstants.PASSENGER_COLLECTION)
                                .whereEqualTo(CommonConstants.PASSENGER_NIC,strNic)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            if(task.getResult().getDocuments().isEmpty()) {
                                                passenger.setPassengerName(editTextName.getText().toString().trim());
                                                passenger.setPassengerAddress(editTextAddress.getText().toString().trim());
                                                passenger.setPassengerMobileNo(Integer.parseInt(editTextMobile.getText().toString().trim()));
                                                passenger.setPassengerCreditLimit(0.0);
                                                passenger.setPassengerNIC(editTextNic.getText().toString().trim());
                                                passenger.setPassengerPassword(editTextpwd.getText().toString().trim());
                                                passenger.setPassengerType(CommonConstants.PASSENGER_TYPE_LOCAL);

                                                //get current date
                                                Calendar calendar = Calendar.getInstance();
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
                                                String  date = dateFormat.format(calendar.getTime());

                                                passenger.setRegisteredDate(date);

                                                registerPassenger(passenger);

                                            }
                                            else {
                                                editTextNic.setError(CommonConstants.VALIDATION_NIC_EXIST);
                                                editTextNic.requestFocus();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(Register.this, CommonConstants.VALIDATION_NIC_EXIST, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception ex) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(Register.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    catch (Exception e){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    public void fnClearInput() {
        editTextName.setText("");
        editTextAddress.setText("");
        editTextMobile.setText("");
        editTextNic.setText("");
        editTextpwd.setText("");
    }

    private void registerPassenger(Passengers passenger) {
        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .add(passenger)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),CommonConstants.PASSENGER_SUCCESS_REGISTER, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));
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