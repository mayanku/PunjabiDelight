package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn,confirmpayonline;
    String TAG="Payment Error";
    private TextView total;
    private String totalAmount="";
    FirebaseUser currentUser;

    private String orderRandomKey,payment_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount=getIntent().getStringExtra("Total Price");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(this," Total Price = "+totalAmount,Toast.LENGTH_LONG).show();

        confirmOrderBtn=(Button)findViewById(R.id.confirm_order_btn);
        confirmpayonline=(Button)findViewById(R.id.confirm_order_btn_online);
        nameEditText=(EditText)findViewById(R.id.shipment_name);
        phoneEditText=(EditText)findViewById(R.id.shipment_phone_number);
        addressEditText=(EditText)findViewById(R.id.shipment_address);
        cityEditText=(EditText)findViewById(R.id.shipment_city);
        total=(TextView)findViewById(R.id.totalprice);
        total.setText("Total Amount="+totalAmount);

        confirmpayonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameEditText.getText().toString()))
                {
                    Toast.makeText(ConfirmFinalOrderActivity.this,"Please Provide full Name",Toast.LENGTH_SHORT).show();

                }
                else  if (TextUtils.isEmpty(phoneEditText.getText().toString()))
                {
                    Toast.makeText(ConfirmFinalOrderActivity.this,"Please Provide full Name",Toast.LENGTH_SHORT).show();
                }
                else  if (TextUtils.isEmpty(addressEditText.getText().toString()))
                {
                    Toast.makeText(ConfirmFinalOrderActivity.this,"Please Provide full Name",Toast.LENGTH_SHORT).show();
                }
                else  if (TextUtils.isEmpty(cityEditText.getText().toString()))
                {
                    Toast.makeText(ConfirmFinalOrderActivity.this,"Please Provide full Name",Toast.LENGTH_SHORT).show();
                }
                else
                {

                  //  startPayment();
                }


            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_mode="On Delivery";
               Check();
            }
        });
    }

    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this,"Please Provide your full name",Toast.LENGTH_LONG).show();

        }

        else  if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this,"Please Provide your phone",Toast.LENGTH_LONG).show();
        }
        else  if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this,"Please Provide your address",Toast.LENGTH_LONG).show();

        }
        else  if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this,"Please Provide your city",Toast.LENGTH_LONG).show();

        }
        else
        {
            confirmOrder();
        }
    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderRandomKey = saveCurrentDate + saveCurrentTime;

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(currentUser.getPhoneNumber());


        final DatabaseReference ordersuserview = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("User View")
                .child(currentUser.getPhoneNumber());

        final DatabaseReference MyordersRef = FirebaseDatabase.getInstance().getReference()
                .child("MyOrders")
                .child(currentUser.getPhoneNumber())
                .child(orderRandomKey);

        final DatabaseReference MyordersProduct = FirebaseDatabase.getInstance().getReference()
                .child("MyOrdersProducts")
                .child(currentUser.getPhoneNumber())
                .child(orderRandomKey);


        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameEditText.getText().toString());
        orderMap.put("phone", phoneEditText.getText().toString());
        orderMap.put("address", addressEditText.getText().toString());
        orderMap.put("city", cityEditText.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("paymentmode", payment_mode);
        orderMap.put("state", "not shipped");

        moveGameRoom(ordersuserview,MyordersProduct);

        MyordersRef.updateChildren(orderMap);
        ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(currentUser.getPhoneNumber())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "YourFinal Order has been Placed Successfully", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });


    }

    private void moveGameRoom(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");

                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
