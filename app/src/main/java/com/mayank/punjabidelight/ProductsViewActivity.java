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
import android.widget.Toast;

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

public class ProductsViewActivity extends AppCompatActivity {

    String category;
    private DatabaseReference ProductsRef;
    private TextView dishheading;


    FirebaseUser currentUser;
    private ProgressDialog loadingBar;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ExtendedFloatingActionButton float_indian,float_shwarma,float_chinese,float_bakery,float_tandoor,float_menu,float_italian,float_rice;
    private boolean isOpen= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_view);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        float_bakery=(ExtendedFloatingActionButton)findViewById(R.id.floating_bakery);
        float_chinese=(ExtendedFloatingActionButton)findViewById(R.id.floating_chinese);
        float_indian=(ExtendedFloatingActionButton)findViewById(R.id.floating_indian);
        float_shwarma=(ExtendedFloatingActionButton)findViewById(R.id.floating_shwarma);
        float_italian=(ExtendedFloatingActionButton)findViewById(R.id.floating_italian);
        float_tandoor=(ExtendedFloatingActionButton)findViewById(R.id.floating_tandoor);
        float_menu=(ExtendedFloatingActionButton)findViewById(R.id.menu);
        float_rice=(ExtendedFloatingActionButton)findViewById(R.id.floating_rices);



        float_indian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ProductsViewActivity.this,IndianProductsViewActivity.class);
                intent.putExtra("category","indian");
                startActivity(intent);
            }
        });

        float_shwarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
                intent.putExtra("category","shwarma");
                startActivity(intent);
            }
        });

        float_chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,ChineseProductsViewActivity.class);
                intent.putExtra("category","chinese");
                startActivity(intent);
            }
        });

        float_italian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
                intent.putExtra("category","italian");
                startActivity(intent);
            }
        });

        float_bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,BakeryProductsViewActivity.class);
                intent.putExtra("category","bakery");
                startActivity(intent);
            }
        });

        float_tandoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,TandoorProductsViewActivity.class);
                intent.putExtra("category","tandoor");
                startActivity(intent);
            }
        });

        float_rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
                intent.putExtra("category","rice");
                startActivity(intent);
            }
        });


        isOpen=false;

        float_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen){

                    float_bakery.setVisibility(View.INVISIBLE);
                    float_chinese.setVisibility(View.INVISIBLE);
                    float_indian.setVisibility(View.INVISIBLE);
                    float_shwarma.setVisibility(View.INVISIBLE);
                    float_italian.setVisibility(View.INVISIBLE);
                    float_tandoor.setVisibility(View.INVISIBLE);
                    float_rice.setVisibility(View.INVISIBLE);

                  isOpen=false;

                }
                else{

                    float_bakery.setVisibility(View.VISIBLE);
                    float_chinese.setVisibility(View.VISIBLE);
                    float_indian.setVisibility(View.VISIBLE);
                    float_shwarma.setVisibility(View.VISIBLE);
                    float_italian.setVisibility(View.VISIBLE);
                    float_tandoor.setVisibility(View.VISIBLE);
                    float_rice.setVisibility(View.VISIBLE);

                  //  Toast.makeText(ProductsViewActivity.this,"Press menu again to close",Toast.LENGTH_SHORT).show();

                    isOpen=true;

                }
            }
        });


     //   dishheading=(TextView)findViewById(R.id.dishheading);

        category=getIntent().getStringExtra("category");

        if(category.equals("shwarma")){
            category="shawarma";
        }

     //  dishheading.setText(""+category.toUpperCase()+" DISHES ");
        Toolbar toolbar1= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText(""+category.toUpperCase()+" DISHES ");

        if(category.equals("shawarma")){
            category="shwarma";
        }

        ImageView imgs=toolbar1.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgss=toolbar1.findViewById(R.id.img_cart);
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });


        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products").child(category);

        recyclerView=findViewById(R.id.cycler_menu);

        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }



    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ProductsViewActivity.this,HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

                        if(model.getAvailable().equals("0")){
                            holder.numberButton1.setVisibility(View.GONE);
                            holder.soldout.setVisibility(View.VISIBLE);
                        }


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
                                             /*          cartListRef.child("Admin View")
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
                                              /*      cartListRef.child("Admin View").child(currentUser.getPhoneNumber())
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
                                                                    }); */
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




}
