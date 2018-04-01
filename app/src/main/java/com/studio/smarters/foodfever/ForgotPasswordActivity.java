package com.studio.smarters.foodfever;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailText;
    private Button resetButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailText=findViewById(R.id.forgot_email);
        resetButton=findViewById(R.id.reset_button);
        mAuth=FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog p;
                p=new ProgressDialog(ForgotPasswordActivity.this);
                p.setTitle("Please Wait..");
                p.setMessage("Please wait while we are logging you in...");
                p.setCancelable(false);
                p.setCanceledOnTouchOutside(false);
                p.show();
                mAuth.sendPasswordResetEmail(emailText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        p.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "A password reset link has been sent to your mail..", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
