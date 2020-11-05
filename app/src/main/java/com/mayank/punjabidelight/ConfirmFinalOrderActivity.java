package com.mayank.punjabidelight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mayank.punjabidelight.Model.CartProducts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private static final int UPI_PAYMENT = 0;

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn, confirmpayonline;
    String TAG = "Payment Error";
    private TextView total;
    private int totalAmount = 0;
    FirebaseUser currentUser;
    private ProgressDialog loadingBar;
    DatabaseReference transRef;
    private APIService apiService;
    String refreshToken="";

    private String orderRandomKey, payment_mode;
    private static final String CHANNEL_ID="Punjabi Delight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        Toolbar toolbar1= findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText("Confirm Shipment Details");

        ImageView imgs=toolbar1.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConfirmFinalOrderActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgss=toolbar1.findViewById(R.id.img_cart);
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ConfirmFinalOrderActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });


        transRef = FirebaseDatabase.getInstance().getReference()
                .child("TransactionsReceipt");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    //    Toast.makeText(this, " Total Price = " + totalAmount, Toast.LENGTH_LONG).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_order_btn);
        confirmpayonline = (Button) findViewById(R.id.confirm_order_btn_online);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);
        total = (TextView) findViewById(R.id.totalprice);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        refreshToken= FirebaseInstanceId.getInstance().getToken();


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
                }
                else if (totalAmount<180) {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Order Amount should be greater than 180", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(ConfirmFinalOrderActivity.this, "We will soon start with Online Payment Currently we are accepting only COD Please use below button", Toast.LENGTH_SHORT).show();
                   // startPayment();
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
        else if (totalAmount<180) {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Order Amount should be greater than 180", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar=new ProgressDialog(ConfirmFinalOrderActivity.this);
            loadingBar.setTitle("Placing the order");
            loadingBar.setMessage("Please wait we are placing the order");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
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


        final DatabaseReference adminuserview = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin View")
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
        orderMap.put("totalAmount", Integer.toString(totalAmount));
        orderMap.put("name", nameEditText.getText().toString());
        orderMap.put("phone", phoneEditText.getText().toString());
        orderMap.put("address", addressEditText.getText().toString());
        orderMap.put("city", cityEditText.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("token",refreshToken);
        orderMap.put("paymentmode", payment_mode);
        orderMap.put("state", "not shipped");

        moveGameRoom(ordersuserview,MyordersProduct);
        moveGameRoom(ordersuserview,adminuserview);

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
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your Final Order has been Placed Successfully", Toast.LENGTH_LONG).show();
                                       loadingBar.dismiss();
                                        notification1();

                                        FirebaseDatabase.getInstance().getReference().child("Tokens").child("Admin").child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String usertoken=dataSnapshot.getValue(String.class);
                                                sendNotifications(usertoken, "New Order","Received New Order");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


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

    private void notification1() {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_notification)
                .setContentTitle("Your Order is Placed")
                .setContentText("Tasty food will soon be delivered")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
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

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Showing Delicious dishes");
        loadingBar.setMessage("Please wait we are updating our menu");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        totalAmount=30;

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        cartListRef.child("User View")
                .child(currentUser.getPhoneNumber())
                .child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartProducts cart = snapshot.getValue(CartProducts.class);
                    int oneTypeProductTPrice =((Integer.parseInt(cart.getPrice()))) * Integer.parseInt(cart.getQuantity());
                    System.out.println(oneTypeProductTPrice);
                    totalAmount=totalAmount+oneTypeProductTPrice;
                    System.out.println(totalAmount);
                    total.setText("Total Amount="+ totalAmount);
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // Toast.makeText(this, " Total Price = " + Integer.toString(totalAmount), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ConfirmFinalOrderActivity.this,CartActivity.class);
        startActivity(intent);
    }
}
