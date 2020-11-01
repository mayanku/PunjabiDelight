package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayank.punjabidelight.Model.Products;
import com.mayank.punjabidelight.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class IndianProductsViewActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;

    FirebaseUser currentUser;
    private ProgressDialog loadingBar;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ExtendedFloatingActionButton float_dhabe,float_paneer,float_roti,float_menu,float_all;
    private boolean isOpen= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indian_products_view);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        float_dhabe=(ExtendedFloatingActionButton)findViewById(R.id.floating_dhabha);
        float_paneer=(ExtendedFloatingActionButton)findViewById(R.id.floating_paneer);
        float_roti=(ExtendedFloatingActionButton)findViewById(R.id.floating_roti);
        float_all=(ExtendedFloatingActionButton)findViewById(R.id.floating_all);


        float_menu=(ExtendedFloatingActionButton)findViewById(R.id.menu);


        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products").child("indian");

        float_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen){

                    float_dhabe.setVisibility(View.INVISIBLE);
                    float_paneer.setVisibility(View.INVISIBLE);
                    float_roti.setVisibility(View.INVISIBLE);
                    float_all.setVisibility(View.INVISIBLE);

                    isOpen=false;

                }
                else{

                    float_dhabe.setVisibility(View.VISIBLE);
                    float_paneer.setVisibility(View.VISIBLE);
                    float_roti.setVisibility(View.VISIBLE);
                    float_all.setVisibility(View.VISIBLE);

                    //  Toast.makeText(ProductsViewActivity.this,"Press menu again to close",Toast.LENGTH_SHORT).show();

                    isOpen=true;

                }
            }
        });

        float_dhabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_menu("dhaba");
            }
        });

        float_paneer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_menu("paneer");
            }
        });

        float_roti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_menu("roti");
            }
        });

        float_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_all();
            }
        });


        Toolbar toolbar1= findViewById(R.id.toolbar_indian);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText("INDIAN DISHES");

        ImageView imgs=toolbar1.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndianProductsViewActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgss=toolbar1.findViewById(R.id.img_cart);
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndianProductsViewActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        recyclerView=findViewById(R.id.cycler_menu);

        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void show_all() {

        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Showing Delicious dishes");
        loadingBar.setMessage("Please wait we are updating our menu");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef,Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int i, @NonNull final Products model) {

                        cartListRef.child("User View")
                                .child(currentUser.getPhoneNumber())
                                .child("Products")
                                .child(model.getPid()).child("quantity").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    String s= dataSnapshot.getValue().toString();
                                    holder.numberButton1.setNumber(s);
                                    // loadingBar.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Rs = " +model.getPrice()+ " ");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        loadingBar.dismiss();


                        holder.numberButton1.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                            @Override
                            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                                //   Toast.makeText(ProductsViewActivity.this,holder.numberButton1.getNumber(),Toast.LENGTH_SHORT).show();
                                if(holder.numberButton1.getNumber().equals("0")){
                                    final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
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
                                            /*            cartListRef.child("Admin View")
                                                                .child(currentUser.getPhoneNumber())
                                                                .child("Products")
                                                                .child(model.getPid())
                                                                .removeValue();*/
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
                                    cartMap.put("pid",model.getPid());
                                    cartMap.put("pname",model.getPname());
                                    cartMap.put("price",model.getPrice());
                                    cartMap.put("image",model.getImage());
                                    cartMap.put("date",saveCurrentDate);
                                    cartMap.put("time",saveCurrentTime);
                                    cartMap.put("quantity",holder.numberButton1.getNumber());
                                    cartMap.put("discount", "");


                                    cartListRef.child("User View").child(currentUser.getPhoneNumber())
                                            .child("Products").child(model.getPid())
                                            .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {
                                                     /*   cartListRef.child("Admin View").child(currentUser.getPhoneNumber())
                                                                .child("Products").child(model.getPid())
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
                                                                        });*/
                                                    }
                                                }
                                            });
                                }}

                        });

                    }
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    private void filter_menu(String sub) {


        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Showing Delicious dishes");
        loadingBar.setMessage("Please wait we are updating our menu");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Products> options=new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("subcategory").equalTo(sub), Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int i, @NonNull final Products model) {

                        cartListRef.child("User View")
                                .child(currentUser.getPhoneNumber())
                                .child("Products")
                                .child(model.getPid()).child("quantity").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    String s= dataSnapshot.getValue().toString();
                                    holder.numberButton1.setNumber(s);
                                    // loadingBar.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Rs = " +model.getPrice()+ " ");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        loadingBar.dismiss();


                        holder.numberButton1.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                            @Override
                            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                                //   Toast.makeText(ProductsViewActivity.this,holder.numberButton1.getNumber(),Toast.LENGTH_SHORT).show();
                                if(holder.numberButton1.getNumber().equals("0")){
                                    final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
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
                                                  /*      cartListRef.child("Admin View")
                                                                .child(currentUser.getPhoneNumber())
                                                                .child("Products")
                                                                .child(model.getPid())
                                                                .removeValue();*/
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
                                    cartMap.put("pid",model.getPid());
                                    cartMap.put("pname",model.getPname());
                                    cartMap.put("price",model.getPrice());
                                    cartMap.put("image",model.getImage());
                                    cartMap.put("date",saveCurrentDate);
                                    cartMap.put("time",saveCurrentTime);
                                    cartMap.put("quantity",holder.numberButton1.getNumber());
                                    cartMap.put("discount", "");


                                    cartListRef.child("User View").child(currentUser.getPhoneNumber())
                                            .child("Products").child(model.getPid())
                                            .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {
                                                /*        cartListRef.child("Admin View").child(currentUser.getPhoneNumber())
                                                                .child("Products").child(model.getPid())
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
                                                                        });*/
                                                    }
                                                }
                                            });
                                }}

                        });

                    }
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    protected void onStart() {
        super.onStart();
        show_all();
    }


}
