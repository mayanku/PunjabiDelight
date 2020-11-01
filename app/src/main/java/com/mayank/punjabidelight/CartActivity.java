package com.mayank.punjabidelight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayank.punjabidelight.Model.CartProducts;
import com.mayank.punjabidelight.ViewHolder.CartProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private int overTotalPrice=0;
    private int oneTypeProductTPrice=0;
    private ProgressDialog loadingBar;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar1= findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText("Total products in Order ");

        ImageView imgs=toolbar1.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgss=toolbar1.findViewById(R.id.img_cart);
        imgss.setVisibility(View.INVISIBLE);
        recyclerView=(RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        NextProcessBtn=(Button)findViewById(R.id.next_process_btn);
      //  txtTotalAmount=(TextView)findViewById(R.id.total_price);

        txtMsg1=(TextView)findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   txtTotalAmount.setText("Total Price");
               if (oneTypeProductTPrice==0){
                   Toast.makeText(CartActivity.this,"Add Atleast one item in your cart,",Toast.LENGTH_SHORT).show();
                }
                else{

                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
               // intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();}
           }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CartActivity.this,HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStat();

      //  txtTotalAmount.setText("Total Products in order");

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final DatabaseReference orderListRef = FirebaseDatabase.getInstance().getReference().child("Order List");


        FirebaseRecyclerOptions<CartProducts> options =
                new FirebaseRecyclerOptions.Builder<CartProducts>()
                        .setQuery(cartListRef.child("User View")
                                .child(currentUser.getPhoneNumber()).child("Products"), CartProducts.class).build();


        FirebaseRecyclerAdapter<CartProducts, CartProductViewHolder> adapter= new FirebaseRecyclerAdapter<CartProducts, CartProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartProductViewHolder cartProductViewHolder, int i, @NonNull final CartProducts cartProducts) {

                cartProductViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
                        cartListRef.child("User View")
                                .child(currentUser.getPhoneNumber())
                                .child("Products")
                                .child(cartProducts.getPid())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            //   Toast.makeText(CartActivity.this,"Item Removed Successfully,",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

                cartProductViewHolder.txtProductName.setText(cartProducts.getPname());
                cartProductViewHolder.txtProductPrice.setText("Rs " +cartProducts.getPrice()+" ");
                 oneTypeProductTPrice =((Integer.parseInt(cartProducts.getPrice()))) * Integer.parseInt(cartProducts.getQuantity());
                cartProductViewHolder.txttotal.setText("Total= " +oneTypeProductTPrice);
                cartProductViewHolder.numberButton2.setNumber(cartProducts.getQuantity());
                Picasso.get().load(cartProducts.getImage()).into(cartProductViewHolder.imageView);

                overTotalPrice=overTotalPrice+oneTypeProductTPrice;





                cartProductViewHolder.numberButton2.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        //   Toast.makeText(ProductsViewActivity.this,holder.numberButton1.getNumber(),Toast.LENGTH_SHORT).show();
                        if(cartProductViewHolder.numberButton2.getNumber().equals("0")){
                            final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
                            cartListRef.child("User View")
                                    .child(currentUser.getPhoneNumber())
                                    .child("Products")
                                    .child(cartProducts.getPid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                //   Toast.makeText(CartActivity.this,"Item Removed Successfully,",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }
                        else{

                            final String saveCurrentTime, saveCurrentDate;
                            Calendar calForDate= Calendar.getInstance();
                            SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
                            saveCurrentDate=currentDate.format(calForDate.getTime());

                            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
                            saveCurrentTime = currentTime.format(calForDate.getTime());

                            final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");


                            final HashMap<String, Object> cartMap=new HashMap<>();
                            cartMap.put("pid",cartProducts.getPid());
                            cartMap.put("pname",cartProducts.getPname());
                            cartMap.put("price",cartProducts.getPrice());
                            cartMap.put("image",cartProducts.getImage());
                            cartMap.put("date",saveCurrentDate);
                            cartMap.put("time",saveCurrentTime);
                            cartMap.put("quantity",cartProductViewHolder.numberButton2.getNumber());
                            cartMap.put("discount", "");


                            cartListRef.child("User View").child(currentUser.getPhoneNumber())
                                    .child("Products").child(cartProducts.getPid())
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                            }
                                        }
                                    });
                        }}

                });

            }


            @NonNull
            @Override
            public CartProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartproducts_items_layout,parent,false);
                CartProductViewHolder holder=new CartProductViewHolder(view);
                return holder;
            }
        };



        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    Toolbar toolbar1= findViewById(R.id.toolbar2);
                    setSupportActionBar(toolbar1);
                    TextView textView = toolbar1.findViewById(R.id.toolbar_title);
                    textView.setText("Order in Process ");
                    recyclerView.setVisibility(View.GONE);
                    txtMsg1.setVisibility(View.VISIBLE);
                    NextProcessBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
