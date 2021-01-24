package com.mayank.punjabidelight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ChooseDeliveryActivity extends AppCompatActivity {

    RadioButton radio_pickup,radio_delivery;
    Button button_choose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_delivery);
        button_choose=(Button)findViewById(R.id.next_process);

        Toolbar toolbar1= findViewById(R.id.toolbar_choose);
        setSupportActionBar(toolbar1);
        TextView textView = toolbar1.findViewById(R.id.toolbar_title);
        textView.setText("CHOOSE DELIVERY");

        ImageView imgs=toolbar1.findViewById(R.id.img_back);
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseDeliveryActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgss=toolbar1.findViewById(R.id.img_cart);
        imgss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseDeliveryActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
        button_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio_pickup.isChecked()){
                    Intent intent=new Intent(ChooseDeliveryActivity.this,ConfirmFinalOrderPickupActivity.class);
                    startActivity(intent);
                    finish();

                }
                if(radio_delivery.isChecked()){
                    Intent intent=new Intent(ChooseDeliveryActivity.this,ConfirmFinalOrderActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}
