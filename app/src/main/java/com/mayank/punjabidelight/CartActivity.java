package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mayank.punjabidelight.Model.Cart;
import com.mayank.punjabidelight.Model.CartProducts;
import com.mayank.punjabidelight.Model.Products;
import com.mayank.punjabidelight.ViewHolder.CartProductViewHolder;
import com.mayank.punjabidelight.ViewHolder.CartViewHolder;
import com.mayank.punjabidelight.ViewHolder.ProductViewHolder;
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
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
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


                final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
                cartListRef.child("User View")
                        .child(currentUser.getPhoneNumber())
                        .child("Products")
                        .child(cartProducts.getPid()).child("quantity").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            String s= dataSnapshot.getValue().toString();
                            cartProductViewHolder.numberButton1.setNumber(s);
                            oneTypeProductTPrice =((Integer.parseInt(cartProducts.getPrice()))) * Integer.parseInt(cartProductViewHolder.numberButton1.getNumber());
                            cartProductViewHolder.txttotal.setText("Total= " +oneTypeProductTPrice);


                            // loadingBar.dismiss();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




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
                                            cartListRef.child("Admin View")
                                                    .child(currentUser.getPhoneNumber())
                                                    .child("Products")
                                                    .child(cartProducts.getPid())
                                                    .removeValue();
                                            //   Toast.makeText(CartActivity.this,"Item Removed Successfully,",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }



                });

                cartProductViewHolder.txtProductName.setText(cartProducts.getPname());
                cartProductViewHolder.txtProductPrice.setText("price = " +cartProducts.getPrice()+ " Rupees");
           //     int oneTypeProductTPrice =((Integer.parseInt(cartProducts.getPrice()))) * Integer.parseInt(cartProductViewHolder.numberButton1.getNumber());
                cartProductViewHolder.txttotal.setText("Total= " +oneTypeProductTPrice);
                Picasso.get().load(cartProducts.getImage()).into(cartProductViewHolder.imageView);
                overTotalPrice=overTotalPrice+oneTypeProductTPrice;





                cartProductViewHolder.numberButton1.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        //   Toast.makeText(ProductsViewActivity.this,holder.numberButton1.getNumber(),Toast.LENGTH_SHORT).show();
                        if(cartProductViewHolder.numberButton1.getNumber().equals("0")){
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
                            cartListRef.child("Admin View")
                                    .child(currentUser.getPhoneNumber())
                                    .child("Products")
                                    .child(cartProducts.getPid())
                                    .removeValue();

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
                            cartMap.put("quantity",cartProductViewHolder.numberButton1.getNumber());
                            cartMap.put("discount", "");


                            cartListRef.child("User View").child(currentUser.getPhoneNumber())
                                    .child("Products").child(cartProducts.getPid())
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                cartListRef.child("Admin View").child(currentUser.getPhoneNumber())
                                                        .child("Products").child(cartProducts.getPid())
                                                        .updateChildren(cartMap)
                                                        .addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {

                                                                            //    Toast.makeText(ProductDetailsActivity.this,"Added to Cart",Toast.LENGTH_LONG).show();

                                                                        }
                                                                    }
                                                                });
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










     /*   FirebaseRecyclerOptions<Cart> options=
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(currentUser.getPhoneNumber()).child("Products"),Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantity = " +model.getQuantity());
                holder.txtProductPrice.setText("Price = "+model.getPrice());
                holder.txtProductName.setText("Product = " +model.getPname());

                int oneTypeProductTPrice =((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());

                overTotalPrice=overTotalPrice+oneTypeProductTPrice;


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (i==1)
                                {


                                    String key = orderListRef.child(currentUser.getPhoneNumber()).getKey();
                                    orderListRef.child(currentUser.getPhoneNumber()).child(key).child("Products")
                                            .child(model.getPid()).removeValue();

                                    cartListRef.child("Admin View").child(currentUser.getPhoneNumber()).child("Products")
                                            .child(model.getPid()).removeValue();



                                    cartListRef.child("User View")
                                            .child(currentUser.getPhoneNumber())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this,"Item Removed Successfully,",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }*/
     //   };
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
                    textView.setText("Order in Proces ");
               //     txtTotalAmount.setText("Order In Process ");
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
