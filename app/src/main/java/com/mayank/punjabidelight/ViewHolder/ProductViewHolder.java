package com.mayank.punjabidelight.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mayank.punjabidelight.Interface.ItemClickListener;
import com.mayank.punjabidelight.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName , txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        imageView=(ImageView)itemView.findViewById(R.id.product_image);
        txtProductDescription=(TextView)itemView.findViewById(R.id.product_description);
        txtProductName=(TextView)itemView.findViewById(R.id.product_name);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_price);



    }


    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        listener.Onclick(v,getAdapterPosition(),false);
    }
}
