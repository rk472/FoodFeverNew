package com.studio.smarters.foodfever;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText nameText,emailText,phoneText,passwordText;
    private Button signupButton;
    private TextView loginLink;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameText=findViewById(R.id.signup_name);
        emailText=findViewById(R.id.signup_email);
        phoneText=findViewById(R.id.signup_phone);
        passwordText=findViewById(R.id.signup_password);
        signupButton=findViewById(R.id.signup_button);
        loginLink=findViewById(R.id.login_link);
        mAuth=FirebaseAuth.getInstance();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog p;
                p=new ProgressDialog(SignupActivity.this);
                p.setTitle("Please Wait..");
                p.setMessage("Please wait while we are creating your account...");
                p.setCancelable(false);
                p.setCanceledOnTouchOutside(false);
                p.show();
                final String name=nameText.getText().toString();
                String password=passwordText.getText().toString();
                final String email=emailText.getText().toString();
                final String phone=phoneText.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        p.dismiss();
                        if(task.isSuccessful()){
                            DatabaseReference d= FirebaseDatabase.getInstance().getReference().child("users");
                            String uid=task.getResult().getUser().getUid();
                            d.child(uid).child("name").setValue(name);
                            d.child(uid).child("email").setValue(email);
                            d.child(uid).child("phone_no").setValue(phone);
                            Intent i=new Intent(SignupActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
