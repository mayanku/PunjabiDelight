package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private static final int UPI_PAYMENT = 0;
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn, confirmpayonline;
    String TAG = "Payment Error";
    private TextView total;
    private String totalAmount = "";
    FirebaseUser currentUser;
    DatabaseReference transRef;


    private String orderRandomKey, payment_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


       transRef = FirebaseDatabase.getInstance().getReference()
                .child("TransactionsReceipt");
        totalAmount = getIntent().getStringExtra("Total Price");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(this, " Total Price = " + totalAmount, Toast.LENGTH_LONG).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_order_btn);
        confirmpayonline = (Button) findViewById(R.id.confirm_order_btn_online);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);
        total = (TextView) findViewById(R.id.totalprice);
        total.setText("Total Amount=" + totalAmount);

        confirmpayonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameEditText.getText().toString())) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide full Name", Toast.LENGTH_SHORT).show();
                }
                else if (phoneEditText.length()<10) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide Correct Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide full Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide full Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide full Name", Toast.LENGTH_SHORT).show();
                } else {
                    String amount = totalAmount;
                    String upiId = "arpitverma026@okaxis";
                    String name = "Punjabi Delight";
                    String note = "Food Bill";

                    payment_mode = "Online";
                    payUsingUpi(name, upiId,note,amount);
                }


            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_mode = "On Delivery";
                Check();
            }
        });

    }
        void payUsingUpi (String name, String upiId, String note, String amount){
            Log.e("main ", "name " + name + "--up--" + upiId + "--" + note + "--" + amount);
            Uri uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", upiId)
                    .appendQueryParameter("pn", name)
                    //.appendQueryParameter("mc", "")
                    //.appendQueryParameter("tid", "02125412")
                    //.appendQueryParameter("tr", "25584584")
                    .appendQueryParameter("tn", note)
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("cu", "INR")
                    //.appendQueryParameter("refUrl", "blueapp")
                    .build();


            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
            upiPayIntent.setData(uri);

            // will always show a dialog to user to choose an app
            Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

            // check if intent resolves
            if (null != chooser.resolveActivity(getPackageManager())) {
                startActivityForResult(chooser, UPI_PAYMENT);
            } else {
                Toast.makeText(ConfirmFinalOrderActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("main ", "response " + resultCode);
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
            switch (requestCode) {
                case UPI_PAYMENT:
                    if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                        if (data != null) {
                            String trxt = data.getStringExtra("response");
                            Log.e("UPI", "onActivityResult: " + trxt);
                            ArrayList<String> dataList = new ArrayList<>();
                            dataList.add(trxt);
                            upiPaymentDataOperation(dataList);
                        } else {
                            Log.e("UPI", "onActivityResult: " + "Return data is null");
                            ArrayList<String> dataList = new ArrayList<>();
                            dataList.add("nothing");
                            upiPaymentDataOperation(dataList);
                        }
                    } else {
                        //when user simply back without payment
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                    break;
            }
        }

        private void upiPaymentDataOperation (ArrayList < String > data) {
            if (isConnectionAvailable(ConfirmFinalOrderActivity.this)) {
                String str = data.get(0);
                Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
                String paymentCancel = "";
                if (str == null) str = "discard";
                String status = "";
                String approvalRefNo = "";
                String response[] = str.split("&");
                for (int i = 0; i < response.length; i++) {
                    String equalStr[] = response[i].split("=");
                    if (equalStr.length >= 2) {
                        if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                            status = equalStr[1].toLowerCase();
                        } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                            approvalRefNo = equalStr[1];
                        }
                    } else {
                        paymentCancel = "Payment cancelled by user.";
                    }
                }

                if (status.equals("success")) {
                    //Code to handle successful transaction here.
                    confirmOrder();
                    final String saveCurrentDate, saveCurrentTime;
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calForDate.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calForDate.getTime());

                    String ndomKey = saveCurrentDate + saveCurrentTime;
                    HashMap <String,Object> trans=new HashMap<>();
                    trans.put("status",status);
                    trans.put("approvalNo",approvalRefNo);

                    transRef.child(currentUser.getPhoneNumber()).child(ndomKey).updateChildren(trans);
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "payment successfull: " + approvalRefNo);
                } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "Cancelled by user: " + approvalRefNo);

                } else {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "failed payment: " + approvalRefNo);

                }
            } else {
                Log.e("UPI", "Internet issue: ");

                Toast.makeText(ConfirmFinalOrderActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            }
        }

        public static boolean isConnectionAvailable (Context context){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()
                        && netInfo.isConnectedOrConnecting()
                        && netInfo.isAvailable()) {
                    return true;
                }
            }
            return false;
        }













    private void Check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this,"Please Provide your full name",Toast.LENGTH_LONG).show();

        }
        else if (phoneEditText.length()<10) {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Please Provide Correct Phone Number", Toast.LENGTH_SHORT).show();
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
