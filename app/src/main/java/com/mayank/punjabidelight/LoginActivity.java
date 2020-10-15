package com.mayank.punjabidelight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText phonenumber;
    Button next;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phonenumber=(TextInputEditText)findViewById(R.id.phone_verification);
        next=(Button)findViewById(R.id.next);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number=phonenumber.getText().toString();
                if(number.length()==10) {

                    Intent intent = new Intent(LoginActivity.this, PhoneVerificationActivity.class);
                    intent.putExtra("mobile", number);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Enter correct phone number",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (currentUser != null) {
            //if logged in the start the Profile activity

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            String mobile=currentUser.getPhoneNumber();
            intent.putExtra("mobile",mobile);
            startActivity(intent);
            finish();
        }
    }

}
