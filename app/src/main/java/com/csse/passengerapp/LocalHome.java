package com.csse.passengerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.csse.passengerapp.common.CommonConstants;
import com.csse.passengerapp.model.Passengers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class LocalHome extends AppCompatActivity {

    Button btnTopUp, btnLogOut;
    TextView textViewName, textViewBalance;
    ImageView imageViewQR;
    Bitmap bitmap;
    private String inputValue;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    private QRGEncoder qrgEncoder;
    private AppCompatActivity activity;

    Passengers passenger;
    FirebaseFirestore db;
    String strNic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_home);

        passenger =new Passengers();
        textViewName = findViewById(R.id.tv_name);
        imageViewQR = findViewById(R.id.qr_image);
        textViewBalance = findViewById(R.id.tv_balance);
        btnTopUp = findViewById(R.id.btn_topUp);
        btnLogOut = findViewById(R.id.btn_log_out);

        //get nic from the intent
        strNic = getIntent().getStringExtra(CommonConstants.INTENT_NIC);

        //get passenger details by id
        db = FirebaseFirestore.getInstance();
        db.collection(CommonConstants.PASSENGER_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> list = new ArrayList<>();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Passengers p = doc.toObject(Passengers.class);
                                if(p.getPassengerNIC().equalsIgnoreCase(strNic)){
                                    textViewName.setText(p.getPassengerName());
                                    textViewBalance.setText("Rs. "+p.getPassengerCreditLimit());
                                }
                            }
                        }
                    }
                });


        //generate QR code
        inputValue = strNic;
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            qrgEncoder.setColorBlack(Color.RED);
            qrgEncoder.setColorWhite(Color.YELLOW);
            try {
                bitmap = qrgEncoder.getBitmap();
                imageViewQR.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //button top-up click listener
        btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocalHome.this, TopUp.class).putExtra(CommonConstants.INTENT_NIC, strNic));
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocalHome.this, MainActivity.class));
            }
        });

    }

}