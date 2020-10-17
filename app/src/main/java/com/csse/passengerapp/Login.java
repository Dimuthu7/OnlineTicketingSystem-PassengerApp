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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    TextView textViewRegi;
    EditText editTextNIC, editTextPwd;
    Button btnSubmit;
    ProgressBar progressBar;
    Passengers passenger;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passenger =new Passengers();
        progressBar = findViewById(R.id.progressBar2);
        textViewRegi = findViewById(R.id.tv_goRegi);
        btnSubmit = findViewById(R.id.btn_submit);
        editTextNIC = findViewById(R.id.et_foriName);
        editTextPwd = findViewById(R.id.et_passport);

        textViewRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, LocalHome.class);
                String nic = editTextNIC.getText().toString();
                String pwd = editTextPwd.getText().toString();

                if(nic.isEmpty()){
                    editTextNIC.setError(CommonConstants.VALIDATION_NIC);
                    editTextNIC.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_NIC, Toast.LENGTH_SHORT).show();
                }
                else if(pwd.isEmpty()){
                    editTextPwd.setError(CommonConstants.VALIDATION_PASSWORD);
                    editTextPwd.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_PASSWORD, Toast.LENGTH_SHORT).show();
                }
                else {
                    checkUserExist(nic, pwd);
                }
            }
        });
    }

    private void checkUserExist(String strNic, String strPwd) {
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .whereEqualTo(CommonConstants.PASSENGER_NIC,strNic)
                .whereEqualTo(CommonConstants.PASSENGER_PASSWORD, strPwd)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(!task.getResult().getDocuments().isEmpty()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Passengers passengers = task.getResult().getDocuments().get(0).toObject(Passengers.class);
                                startActivity(new Intent(Login.this, LocalHome.class).putExtra(CommonConstants.INTENT_NIC, strNic));
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Login.this, "Incorrect NIC or password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Login.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}