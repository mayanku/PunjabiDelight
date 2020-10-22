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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mayank.punjabidelight.Model.Products;
import com.mayank.punjabidelight.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class ProductsViewActivity extends AppCompatActivity {

    String category;
    private DatabaseReference ProductsRef;
    private TextView dishheading;

    private ProgressDialog loadingBar;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ExtendedFloatingActionButton float_indian,float_shwarma,float_chinese,float_bakery,float_tandoor,float_menu,float_italian;
    private boolean isOpen= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_view);





        float_bakery=(ExtendedFloatingActionButton)findViewById(R.id.floating_bakery);
        float_chinese=(ExtendedFloatingActionButton)findViewById(R.id.floating_chinese);
        float_indian=(ExtendedFloatingActionButton)findViewById(R.id.floating_indian);
        float_shwarma=(ExtendedFloatingActionButton)findViewById(R.id.floating_shwarma);
        float_italian=(ExtendedFloatingActionButton)findViewById(R.id.floating_italian);
        float_tandoor=(ExtendedFloatingActionButton)findViewById(R.id.floating_tandoor);
        float_menu=(ExtendedFloatingActionButton)findViewById(R.id.menu);




        float_indian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
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
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
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
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
                intent.putExtra("category","bakery");
                startActivity(intent);
            }
        });

        float_tandoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductsViewActivity.this,ProductsViewActivity.class);
                intent.putExtra("category","tandoor");
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

                  isOpen=false;

                }
                else{

                    float_bakery.setVisibility(View.VISIBLE);
                    float_chinese.setVisibility(View.VISIBLE);
                    float_indian.setVisibility(View.VISIBLE);
                    float_shwarma.setVisibility(View.VISIBLE);
                    float_italian.setVisibility(View.VISIBLE);
                    float_tandoor.setVisibility(View.VISIBLE);

                    Toast.makeText(ProductsViewActivity.this,"Press menu again to close",Toast.LENGTH_SHORT).show();

                    isOpen=true;

                }
            }
        });


     //   dishheading=(TextView)findViewById(R.id.dishheading);

        category=getIntent().getStringExtra("category");

     //  dishheading.setText(""+category.toUpperCase()+" DISHES ");
        Toolbar toolbar1= findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText(""+category.toUpperCase()+" DISHES ");

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

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef,Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Products model) {

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("price = " +model.getPrice()+ " Rupees");
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        loadingBar.dismiss();


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(ProductsViewActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("category",category);
                                intent.putExtra("pid",model.getPid());
                                startActivity(intent);
                            }
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
