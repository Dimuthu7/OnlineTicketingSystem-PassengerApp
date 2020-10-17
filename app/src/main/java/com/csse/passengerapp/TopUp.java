package com.csse.passengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

public class TopUp extends AppCompatActivity {

    EditText editTextCard, editTextExpire, editTextCvc, editTextAmount;
    Button btnAddCredit;
    String strNic;
    Passengers passenger;
    FirebaseFirestore db;
    ProgressBar progressBar;
    double dblAmount = 0.0;
//    String docPathId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        passenger =new Passengers();
        progressBar = findViewById(R.id.progressBar4);
        editTextAmount = findViewById(R.id.et_amount);
        editTextCard = findViewById(R.id.et_cardNo);
        editTextCvc = findViewById(R.id.et_cvc);
        editTextExpire = findViewById(R.id.et_expire);
        btnAddCredit = findViewById(R.id.btn_submit);

        btnAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopUp.this, LocalHome.class);
                String strCardNo = editTextCard.getText().toString();
                String strExpire = editTextExpire.getText().toString();
                String strCVC = editTextCvc.getText().toString();
                String strAmount = editTextAmount.getText().toString();

                if(strCardNo.isEmpty()){
                    editTextCard.setError(CommonConstants.VALIDATION_CARD_NO);
                    editTextCard.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_CARD_NO, Toast.LENGTH_SHORT).show();
                }
                else if(strExpire.isEmpty()){
                    editTextExpire.setError(CommonConstants.VALIDATION_EX_DATE);
                    editTextExpire.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_EX_DATE, Toast.LENGTH_SHORT).show();
                }
                else if(strCVC.isEmpty()){
                    editTextCvc.setError(CommonConstants.VALIDATION_CVC);
                    editTextCvc.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_CVC, Toast.LENGTH_SHORT).show();
                }
                else if(strAmount.isEmpty()){
                    editTextAmount.setError(CommonConstants.VALIDATION_AMOUNT);
                    editTextAmount.requestFocus();
                    Toast.makeText(getApplicationContext(),CommonConstants.VALIDATION_AMOUNT, Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    //get nic from the intent
                    strNic = getIntent().getStringExtra(CommonConstants.INTENT_NIC);
                    fnGetExistBalance(strNic);

                }
            }
        });
    }

    public void fnGetExistBalance(String nic){
        db = FirebaseFirestore.getInstance();
        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .whereEqualTo(CommonConstants.PASSENGER_NIC,nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(!task.getResult().getDocuments().isEmpty()) {
                                Passengers passengers = task.getResult().getDocuments().get(0).toObject(Passengers.class);
                                double amount = Double.parseDouble(editTextAmount.getText().toString());
                                dblAmount = amount + passengers.getPassengerCreditLimit();
                                updateCreditLimit(dblAmount);
                            }
                        }
                        else {
                            Toast.makeText(TopUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        Toast.makeText(TopUp.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateCreditLimit(double creditLimit){

        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .whereEqualTo(CommonConstants.PASSENGER_NIC,strNic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String docPathId = task.getResult().getDocuments().get(0).getId();
                            DocumentReference docRef = db.collection(CommonConstants.PASSENGER_COLLECTION).document(docPathId);
                            docRef.update(CommonConstants.PASSENGER_CREDIT_LIMIT, creditLimit);
                            Toast.makeText(TopUp.this, "Successfully top up", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            clearField();
                            startActivity(new Intent(TopUp.this, LocalHome.class).putExtra(CommonConstants.INTENT_NIC, strNic));
                        }
                        else {
                            Toast.makeText(TopUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void clearField(){
        editTextAmount.setText("");
        editTextCard.setText("");
        editTextCvc.setText("");
        editTextExpire.setText("");
    }
}