package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email;
    Button btnforget;
    FirebaseAuth fAuth;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().hide();

        image = findViewById(R.id.backimage);

        email = findViewById(R.id.edtforget);
        btnforget = findViewById(R.id.btnforget);
        fAuth = FirebaseAuth.getInstance();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btnforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forgetemail = email.getText().toString();
                if(email.getText().toString().trim().isEmpty()){
                    email.setText("");
                    Toasty.warning(ForgetPasswordActivity.this, "Please Input A Email", Toasty.LENGTH_SHORT).show();
                } else {
                    fAuth.sendPasswordResetEmail(forgetemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toasty.success(ForgetPasswordActivity.this, "Email Send Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toasty.warning(ForgetPasswordActivity.this,"Email Not Found", Toasty.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}