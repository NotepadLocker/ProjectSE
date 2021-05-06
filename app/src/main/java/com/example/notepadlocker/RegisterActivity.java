package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText edtemail,edtpassword1,edtpassword2;
    Button btnregister;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtemail = findViewById(R.id.edttxtemail);
        edtpassword1 = findViewById(R.id.edttxtpassword);
        edtpassword2 = findViewById(R.id.edttxtpassword1);
        btnregister = findViewById(R.id.btnregis);

        fAuth = FirebaseAuth.getInstance();

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email;
                String password1;
                String password2;
                if(edtemail.getText().toString().trim().isEmpty() || edtpassword1.getText().toString().trim().isEmpty() || edtpassword2.getText().toString().trim().isEmpty()){
                    edtemail.setText("");
                    edtpassword1.setText("");
                    edtpassword2.setText("");
                    Toast.makeText(RegisterActivity.this, "Please Input All Field", Toast.LENGTH_SHORT).show();
                }else{
                    email = edtemail.getText().toString().trim();
                    password1 = edtpassword1.getText().toString().trim();
                    password2 = edtpassword2.getText().toString().trim();
                    if(!password1.equals(password2)){
                        edtemail.setText("");
                        edtpassword1.setText("");
                        edtpassword2.setText("");
                        Toast.makeText(RegisterActivity.this, "Password Not Equals", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        fAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = fAuth.getCurrentUser();

                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Email not Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Toast.makeText(RegisterActivity.this, "Register Succesfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                }
                                else{
                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    edtemail.setText("");
                                    edtpassword1.setText("");
                                    edtpassword2.setText("");
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
