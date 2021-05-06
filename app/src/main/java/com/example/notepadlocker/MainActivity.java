package com.example.notepadlocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtview;
    Button btnlogin,btnregister;
    static String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtview = findViewById(R.id.txtview);
        btnlogin = findViewById(R.id.btnlogin);
        btnregister = findViewById(R.id.btnregister);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentlog = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intentlog);
            }
        });
        
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReg = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intentReg);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        /**
         * SharedPreferences Reset
         */
        //sharedPreferences.edit().clear().commit();
        Boolean counter = sharedPreferences.getBoolean("logincounter",Boolean.valueOf(String.valueOf(MODE_PRIVATE)));
        String uid = sharedPreferences.getString("userid",String.valueOf(MODE_PRIVATE));
        if(counter)
        {
            user_id = uid;
            Intent intent = new Intent(MainActivity.this,NotepadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}