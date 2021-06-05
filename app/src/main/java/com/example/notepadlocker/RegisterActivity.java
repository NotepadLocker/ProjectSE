package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    EditText edtusername, edtemail, edtpassword1, edtpassword2;
    Button btnregister;
    FirebaseAuth fAuth;
    Switch swtch;
    String uid, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        edtusername = findViewById(R.id.edtusername);
        edtemail = findViewById(R.id.edttxtemail);
        edtpassword1 = findViewById(R.id.edttxtpassword);
        edtpassword2 = findViewById(R.id.edttxtpassword1);
        btnregister = findViewById(R.id.btnregis);
        swtch = findViewById(R.id.switchreg);

        fAuth = FirebaseAuth.getInstance();

        swtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtpassword1.setTransformationMethod(null);
                    edtpassword2.setTransformationMethod(null);
                } else {
                    edtpassword1.setTransformationMethod(new PasswordTransformationMethod());
                    edtpassword2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email;
                String password1;
                String password2;
                if (edtusername.getText().toString().trim().isEmpty() || edtemail.getText().toString().trim().isEmpty() || edtpassword1.getText().toString().trim().isEmpty() || edtpassword2.getText().toString().trim().isEmpty()) {
                    edtemail.setText("");
                    edtpassword1.setText("");
                    edtpassword2.setText("");
                    edtusername.setText("");
                    Toasty.warning(RegisterActivity.this, "Please Input All Field", Toast.LENGTH_SHORT).show();
                } else {
                    username = edtusername.getText().toString().trim();
                    email = edtemail.getText().toString().trim();
                    password1 = edtpassword1.getText().toString().trim();
                    password2 = edtpassword2.getText().toString().trim();
                    if (!password1.equals(password2)) {
                        edtusername.setText("");
                        edtemail.setText("");
                        edtpassword1.setText("");
                        edtpassword2.setText("");
                        Toasty.warning(RegisterActivity.this, "Password Not Equals", Toast.LENGTH_SHORT).show();
                    } else {
                        fAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(new OnCompleteListener < AuthResult > () {
                            @Override
                            public void onComplete(@NonNull Task < AuthResult > task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = fAuth.getCurrentUser();

                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toasty.success(RegisterActivity.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.error(RegisterActivity.this, "Email not Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    uid = fAuth.getCurrentUser().getUid();
                                    DatabaseReference userdata = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userdata");
                                    userdata = userdata.child("username");
                                    userdata.setValue(username);
                                    DatabaseReference usertype = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userdata");
                                    usertype = usertype.child("type");
                                    usertype.setValue("Free");
                                    Toasty.success(RegisterActivity.this, "Register Succesfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    Toasty.error(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    edtusername.setText("");
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