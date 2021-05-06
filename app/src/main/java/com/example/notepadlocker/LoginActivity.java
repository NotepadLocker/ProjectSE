package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.notepadlocker.MainActivity.user_id;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail,edtPassword;
    Button loginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtemail);
        edtPassword = findViewById(R.id.edtpassword);
        loginBtn = findViewById(R.id.btnlog);

        fAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email;
                String password;
                if(edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty()){
                    edtEmail.setText("");
                    edtPassword.setText("");
                    Toast.makeText(LoginActivity.this, "Please Fill All", Toast.LENGTH_SHORT).show();
                }
                else{
                    email = edtEmail.getText().toString().trim();
                    password = edtPassword.getText().toString().trim();
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Intent intent1 = new Intent(LoginActivity.this,NotepadActivity.class);
                            FirebaseUser user = fAuth.getCurrentUser();
                            if(!user.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "Please Verify your Account", Toast.LENGTH_SHORT).show();
                            }
                            else if(task.isSuccessful()){
                                user_id = fAuth.getCurrentUser().getUid();
                                SharedPreferences preferences = getSharedPreferences("Session",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("logincounter",true);
                                editor.putString("userid",user_id);
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                            }
                            else{
                                edtEmail.setText("");
                                edtPassword.setText("");
                                Toast.makeText(LoginActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}