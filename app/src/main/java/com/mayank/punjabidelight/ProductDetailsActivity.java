package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayank.punjabidelight.Model.Products;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription,productName;
    private String productID="",state="Normal",category;
    private Button addToCartButton;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID=getIntent().getStringExtra("pid");
        category=getIntent().getStringExtra("category");

         currentUser = FirebaseAuth.getInstance().getCurrentUser();

        addToCartButton=(Button)findViewById(R.id.pd_add_to_cart_button);
        numberButton=(ElegantNumberButton)findViewById(R.id.number_btn);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        productPrice=(TextView)findViewById(R.id.product_price_details);
        productName=(TextView)findViewById(R.id.product_name_details);
        productDescription=(TextView)findViewById(R.id.product_description_details);

        Toolbar toolbar2= findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);


        ImageView imgs=toolbar2.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        ImageView imgss=toolbar2.findViewById(R.id.img_cart);
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("NO"))
                {
                    Toast.makeText(ProductDetailsActivity.this,"You can purchase more products once your order is shipped",Toast.LENGTH_LONG).show();
                }
                else {
                    addingToCartList();
                }

            }
        });

    }

    private void addingToCartList() {

        final String saveCurrentTime, saveCurrentDate;
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");


        final HashMap<String, Object> cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount", "");


        cartListRef.child("User View").child(currentUser.getPhoneNumber())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStat();
    }

    private void getProductDetails(final String productID) {

        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Products").child(category);

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    Toolbar toolbar2= findViewById(R.id.toolbar2);
                    setSupportActionBar(toolbar2);
                    TextView textView = toolbar2.findViewById(R.id.toolbar_title);
                    textView.setText(products.getPname());
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkOrderStat()
    {
        DatabaseReference ordersRef;

        ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser.getPhoneNumber());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                     state = "NO";
                }
                else{
                    state = "Yes";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
