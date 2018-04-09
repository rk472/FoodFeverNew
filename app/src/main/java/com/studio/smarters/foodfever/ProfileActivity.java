package com.studio.smarters.foodfever;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView nameText,phoneText,emailText;
    Button historyButton,logOut;
    FirebaseAuth mAuth;
    String uid,name,email,phone;
    DatabaseReference d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameText=findViewById(R.id.profile_name);
        phoneText=findViewById(R.id.profile_phone);
        emailText=findViewById(R.id.profile_email);
        historyButton=findViewById(R.id.history_button);
        logOut=findViewById(R.id.log_out_button);
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        d= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        d.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                email=dataSnapshot.child("email").getValue().toString();
                phone=dataSnapshot.child("phone_no").getValue().toString();
                nameText.setText(name);
                emailText.setText(email);
                phoneText.setText(phone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i=new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(i);
                finishAffinity();
                finish();
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ProfileActivity.this,OrderHistoryActivity.class);
                startActivity(i);
            }
        });
    }
}
