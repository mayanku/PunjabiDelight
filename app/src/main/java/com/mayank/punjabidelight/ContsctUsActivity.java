package com.mayank.punjabidelight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContsctUsActivity extends AppCompatActivity {

    EditText review;
    Button button;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contsct_us);
        review=(EditText)findViewById(R.id.review);
        button=(Button)findViewById(R.id.button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rate=review.getText().toString();
                final DatabaseReference ReviewRef = FirebaseDatabase.getInstance().getReference()
                        .child("Review");
                ReviewRef.child(currentUser.getPhoneNumber()).setValue(rate);
                Toast.makeText(ContsctUsActivity.this,"We have Received your feedback",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ContsctUsActivity.this,HomeActivity.class);
                startActivity(intent);

            }
        });
    }
}
