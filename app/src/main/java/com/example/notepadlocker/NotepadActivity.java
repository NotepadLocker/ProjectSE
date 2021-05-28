package com.example.notepadlocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.example.notepadlocker.MainActivity.user_id;

public class NotepadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView naview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        getSupportActionBar().hide();

        toolbar = findViewById(R.id.toolbar2);
        drawerLayout = findViewById(R.id.drawerID);
        naview = findViewById(R.id.nav_view);
        naview.setNavigationItemSelectedListener(NotepadActivity.this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(NotepadActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NoteFragment()).commit();
        naview.setCheckedItem(R.id.notes);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if(item.getItemId() == R.id.notes){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NoteFragment()).commit();
        }else if(item.getItemId() == R.id.recycle){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RecycleBinFragment()).commit();
        }else if(item.getItemId() == R.id.singout){
            SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            Intent intent = new Intent(NotepadActivity.this,MainActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}