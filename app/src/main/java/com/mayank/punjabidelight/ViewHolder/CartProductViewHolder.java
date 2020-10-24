package com.mayank.punjabidelight.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.mayank.punjabidelight.Interface.ItemClickListener;
import com.mayank.punjabidelight.R;

public class CartProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice,txttotal;
    public ImageView imageView;
    public ItemClickListener listener;
    public ElegantNumberButton numberButton1;
    public Button remove;

    public CartProductViewHolder(@NonNull View itemView) {
        super(itemView);

        txttotal=(TextView)itemView.findViewById(R.id.product_cart_totalprice) ;
        numberButton1=(ElegantNumberButton)itemView.findViewById(R.id.number_btn_cart);
        imageView=(ImageView)itemView.findViewById(R.id.product_image_cart);
        txtProductName=(TextView)itemView.findViewById(R.id.product_name_cart);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_cart_price);
        remove=(Button)itemView.findViewById(R.id.remove);
    }

    @Override
    public void onClick(View v) {

    }
}
