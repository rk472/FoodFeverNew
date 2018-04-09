package com.studio.smarters.foodfever;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameText,phoneText,emailText;
    private Button historyButton,logOut;
    private FirebaseAuth mAuth;
    private String uid,name,email,phone;
    private DatabaseReference d;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Progress Dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading Your Profile");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

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
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error Loading Profile", Toast.LENGTH_SHORT).show();
                finish();
                pd.dismiss();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Do You really want to LogOut ?")
                        .setPositiveButton("Yes, Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                                finishAffinity();
                                finish();
                            }
                        }).setNegativeButton("No, Don't",null).show();
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
