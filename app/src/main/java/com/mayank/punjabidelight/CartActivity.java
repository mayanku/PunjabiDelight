package com.mayank.punjabidelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mayank.punjabidelight.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private int overTotalPrice=0;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView=(RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        NextProcessBtn=(Button)findViewById(R.id.next_process_btn);
        txtTotalAmount=(TextView)findViewById(R.id.total_price);

        txtMsg1=(TextView)findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtTotalAmount.setText("Total Price");
                if (overTotalPrice==0){
                    Toast.makeText(CartActivity.this,"Add Atleast one item in your cart,",Toast.LENGTH_SHORT).show();
                }
                else{
                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();}
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStat();

        txtTotalAmount.setText("Total Products in order");

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        final DatabaseReference orderListRef = FirebaseDatabase.getInstance().getReference().child("Order List");




        FirebaseRecyclerOptions<Cart> options=
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
                    txtTotalAmount.setText("Order In Process ");
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
